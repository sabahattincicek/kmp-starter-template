"""
Unit tests for scripts/token_usage.py, run with: python3 -m unittest discover -s scripts
"""

import io
import json
import os
import shutil
import sys
import tempfile
import unittest
from contextlib import redirect_stdout

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import token_usage  # noqa: E402


def assistant(message_id, model="claude-test", output=10, stamp="2026-01-02T10:00:00Z", thinking=0):
    usage = {
        "input_tokens": 2,
        "cache_creation_input_tokens": 100,
        "cache_read_input_tokens": 1000,
        "output_tokens": output,
        "output_tokens_details": {"thinking_tokens": thinking},
    }
    return {"type": "assistant", "timestamp": stamp, "message": {"id": message_id, "model": model, "usage": usage}}


class TokenUsageTest(unittest.TestCase):

    def setUp(self):
        self.temp = tempfile.mkdtemp()
        self.root = os.path.join(self.temp, "My_App")
        os.makedirs(self.root)
        self.config = os.path.join(self.temp, "config")
        os.environ["CLAUDE_CONFIG_DIR"] = self.config
        os.environ["TOKEN_USAGE_DEVICE"] = "test-device"
        self.transcripts = token_usage.transcript_dir(self.root)
        os.makedirs(self.transcripts)

    def tearDown(self):
        os.environ.pop("CLAUDE_CONFIG_DIR", None)
        os.environ.pop("TOKEN_USAGE_DEVICE", None)
        shutil.rmtree(self.temp)

    def write_transcript(self, name, records):
        path = os.path.join(self.transcripts, name + ".jsonl")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w", encoding="utf-8") as out:
            for record in records:
                out.write((record if isinstance(record, str) else json.dumps(record)) + "\n")
        return path

    def summary(self, session_id):
        return token_usage.load_json(os.path.join(self.root, token_usage.SUMMARY_DIR, session_id + ".json"))

    def test_transcript_dir_replaces_non_alphanumeric_characters(self):
        folder = os.path.basename(token_usage.transcript_dir(os.path.join("C:", os.sep, "SABO", "template_app")))
        self.assertEqual("C--SABO-template-app", folder)

    def test_streamed_lines_of_one_response_count_once(self):
        self.write_transcript("s1", [
            {"type": "user", "timestamp": "2026-01-02T09:59:00Z"},
            assistant("m1", output=5),
            assistant("m1", output=5),
            assistant("m2", output=7, thinking=3, stamp="2026-01-02T10:05:00Z"),
            {"type": "ai-title", "aiTitle": "Fix login"},
            "not json",
        ])

        token_usage.sync(self.root)

        summary = self.summary("s1")
        usage = summary["main"]["claude-test"]
        self.assertEqual(2, usage["requests"])
        self.assertEqual(12, usage["output_tokens"])
        self.assertEqual(3, usage["thinking_tokens"])
        self.assertEqual(2000, usage["cache_read_input_tokens"])
        self.assertEqual("Fix login", summary["title"])
        self.assertEqual("test-device", summary["device"])
        self.assertEqual("2026-01-02T09:59:00Z", summary["started"])
        self.assertEqual("2026-01-02T10:05:00Z", summary["ended"])

    def test_subagent_transcripts_are_recorded_separately(self):
        self.write_transcript("s1", [assistant("m1")])
        self.write_transcript(os.path.join("s1", "subagents", "agent-a"), [assistant("a1", model="claude-small", output=4)])

        token_usage.sync(self.root)

        summary = self.summary("s1")
        self.assertEqual(4, summary["subagents"]["claude-small"]["output_tokens"])
        self.assertEqual(14, token_usage.session_total(summary)["output_tokens"])

    def test_sessions_without_responses_and_synthetic_messages_are_skipped(self):
        self.write_transcript("empty", [{"type": "user"}, assistant("x", model="<synthetic>")])

        self.assertEqual(0, token_usage.sync(self.root))
        self.assertIsNone(self.summary("empty"))

    def test_unchanged_transcript_is_not_rewritten_and_device_is_kept(self):
        path = self.write_transcript("s1", [assistant("m1")])
        self.assertEqual(1, token_usage.sync(self.root))
        self.assertEqual(0, token_usage.sync(self.root))

        os.environ["TOKEN_USAGE_DEVICE"] = "other-device"
        with open(path, "a", encoding="utf-8") as out:
            out.write(json.dumps(assistant("m2")) + "\n")
        self.assertEqual(1, token_usage.sync(self.root))

        summary = self.summary("s1")
        self.assertEqual(2, summary["main"]["claude-test"]["requests"])
        self.assertEqual("test-device", summary["device"])

    def test_report_groups_by_day_and_totals_all_sessions(self):
        self.write_transcript("s1", [assistant("m1", stamp="2026-01-02T10:00:00Z")])
        self.write_transcript("s2", [assistant("m2", stamp="2026-01-02T11:00:00Z")])
        self.write_transcript("s3", [assistant("m3", stamp="2026-01-05T11:00:00Z")])
        token_usage.sync(self.root)

        rows = token_usage.report_rows(token_usage.load_summaries(self.root), "day")

        self.assertEqual(2, len(rows))
        self.assertEqual("2 sessions", rows[0][1])
        self.assertEqual(20, rows[0][2]["output_tokens"])
        text = token_usage.format_report(token_usage.load_summaries(self.root), "day")
        self.assertIn("TOTAL", text)
        self.assertIn("3 sessions", text)

    def test_sync_command_stays_silent_and_succeeds_when_nothing_exists(self):
        shutil.rmtree(self.transcripts)
        os.environ["CLAUDE_PROJECT_DIR"] = self.root
        try:
            output = io.StringIO()
            with redirect_stdout(output):
                code = token_usage.main(["sync"])
        finally:
            os.environ.pop("CLAUDE_PROJECT_DIR", None)
        self.assertEqual(0, code)
        self.assertEqual("", output.getvalue())


if __name__ == "__main__":
    unittest.main()
