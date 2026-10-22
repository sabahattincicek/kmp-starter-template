#!/usr/bin/env python3
"""
Records the token usage of every Claude Code session in this project and prints reports from it.

Claude Code keeps each session's transcript only on the machine where it ran
(~/.claude/projects/<project>/<session-id>.jsonl). The `sync` command, run automatically by the
SessionStart and SessionEnd hooks in .claude/settings.json, reads those transcripts and writes one
small JSON summary per session into docsagent/token-usage/. The summaries are committed, so the
sessions of every device end up in git, one file each, without merge conflicts. `report` adds
them up. Nothing here calls a model, so recording and reporting cost no tokens.

Usage:
  python3 scripts/token_usage.py report [--by session|day|device|model] [--since YYYY-MM-DD]
  python3 scripts/token_usage.py sync

Only the standard library is used, and the code runs on Python 3.8 and newer.
"""

import argparse
import glob
import json
import os
import platform
import re
import sys
from datetime import datetime, timezone

USAGE_FIELDS = (
    "input_tokens",
    "cache_creation_input_tokens",
    "cache_read_input_tokens",
    "output_tokens",
)
SUMMARY_DIR = os.path.join("docsagent", "token-usage")
SUMMARY_VERSION = 1


def project_root():
    """The project directory: the hook's CLAUDE_PROJECT_DIR, else the parent of scripts/."""
    root = os.environ.get("CLAUDE_PROJECT_DIR")
    if root:
        return os.path.abspath(root)
    return os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def transcript_dir(root):
    """
    Claude Code's transcript folder for the project at [root]. The folder name is the project
    path with every character that is not a letter or a digit replaced by "-".
    """
    config_dir = os.environ.get("CLAUDE_CONFIG_DIR") or os.path.join(os.path.expanduser("~"), ".claude")
    return os.path.join(config_dir, "projects", re.sub(r"[^A-Za-z0-9]", "-", root))


def empty_usage():
    usage = dict.fromkeys(USAGE_FIELDS, 0)
    usage["thinking_tokens"] = 0
    usage["requests"] = 0
    return usage


def add_usage(total, usage):
    for key, value in usage.items():
        total[key] = total.get(key, 0) + value


def read_transcript(path, by_model, meta):
    """
    Adds the usage of every model response in the transcript at [path] to [by_model], keyed by
    model name, and fills the session's title and first and last timestamps into [meta].
    A response is streamed into several lines that repeat its usage, so each message id counts once.
    """
    responses = {}
    with open(path, encoding="utf-8", errors="replace") as lines:
        for line in lines:
            try:
                record = json.loads(line)
            except ValueError:
                continue
            if not isinstance(record, dict):
                continue
            stamp = record.get("timestamp")
            if stamp:
                if not meta.get("started") or stamp < meta["started"]:
                    meta["started"] = stamp
                if not meta.get("ended") or stamp > meta["ended"]:
                    meta["ended"] = stamp
            if record.get("type") == "ai-title" and record.get("aiTitle"):
                meta["title"] = record["aiTitle"]
            if record.get("type") != "assistant":
                continue
            message = record.get("message") or {}
            usage = message.get("usage")
            model = message.get("model") or "unknown"
            if not usage or model == "<synthetic>":
                continue
            key = message.get("id") or record.get("requestId") or record.get("uuid")
            responses[key] = (model, usage)

    for model, usage in responses.values():
        total = by_model.setdefault(model, empty_usage())
        for field in USAGE_FIELDS:
            total[field] += int(usage.get(field) or 0)
        details = usage.get("output_tokens_details") or {}
        total["thinking_tokens"] += int(details.get("thinking_tokens") or 0)
        total["requests"] += 1


def summarize_session(main_path):
    """
    Builds the summary of one session from its transcript and its subagent transcripts, or
    returns None when the session has no model responses yet.
    """
    session_id = os.path.splitext(os.path.basename(main_path))[0]
    meta = {}
    main = {}
    subagents = {}
    read_transcript(main_path, main, meta)
    sub_paths = sorted(glob.glob(os.path.join(os.path.dirname(main_path), session_id, "subagents", "*.jsonl")))
    for path in sub_paths:
        read_transcript(path, subagents, {})
    if not main and not subagents:
        return None
    return {
        "version": SUMMARY_VERSION,
        "session_id": session_id,
        "title": meta.get("title", ""),
        "device": os.environ.get("TOKEN_USAGE_DEVICE") or platform.node() or "unknown",
        "started": meta.get("started", ""),
        "ended": meta.get("ended", ""),
        "main": main,
        "subagents": subagents,
        "transcript_bytes": sum(os.path.getsize(p) for p in [main_path] + sub_paths),
    }


def sync(root):
    """
    Writes or refreshes the summary of every local session of the project. A summary whose
    transcripts have not grown since it was written is skipped. Returns how many files changed.
    """
    source = transcript_dir(root)
    target = os.path.join(root, SUMMARY_DIR)
    changed = 0
    for main_path in sorted(glob.glob(os.path.join(source, "*.jsonl"))):
        session_id = os.path.splitext(os.path.basename(main_path))[0]
        out_path = os.path.join(target, session_id + ".json")
        old = load_json(out_path)
        if old and old.get("transcript_bytes") == current_bytes(main_path):
            continue
        summary = summarize_session(main_path)
        if summary is None:
            continue
        if old and old.get("device"):
            summary["device"] = old["device"]
        text = json.dumps(summary, indent=2, sort_keys=True, ensure_ascii=False) + "\n"
        os.makedirs(target, exist_ok=True)
        with open(out_path, "w", encoding="utf-8", newline="\n") as out:
            out.write(text)
        changed += 1
    return changed


def current_bytes(main_path):
    session_id = os.path.splitext(os.path.basename(main_path))[0]
    paths = [main_path] + glob.glob(os.path.join(os.path.dirname(main_path), session_id, "subagents", "*.jsonl"))
    return sum(os.path.getsize(p) for p in paths)


def load_json(path):
    try:
        with open(path, encoding="utf-8") as source:
            return json.load(source)
    except (OSError, ValueError):
        return None


def load_summaries(root):
    summaries = []
    for path in sorted(glob.glob(os.path.join(root, SUMMARY_DIR, "*.json"))):
        summary = load_json(path)
        if isinstance(summary, dict) and summary.get("session_id"):
            summaries.append(summary)
    return summaries


def local_time(stamp):
    """Turns an ISO 8601 UTC timestamp into local time, or returns None if it does not parse."""
    try:
        moment = datetime.fromisoformat(stamp.replace("Z", "+00:00"))
    except (AttributeError, ValueError):
        return None
    if moment.tzinfo is None:
        moment = moment.replace(tzinfo=timezone.utc)
    return moment.astimezone()


def session_total(summary, model=None):
    total = empty_usage()
    for part in ("main", "subagents"):
        for name, usage in (summary.get(part) or {}).items():
            if model is None or name == model:
                add_usage(total, usage)
    return total


def report_rows(summaries, by):
    """Groups the sessions by [by] and returns (key, title, usage) rows, oldest first."""
    rows = {}
    for summary in sorted(summaries, key=lambda s: s.get("started", "")):
        started = local_time(summary.get("started", ""))
        if by == "session":
            key = started.strftime("%Y-%m-%d %H:%M") if started else "?"
            rows[summary["session_id"]] = [key, summary.get("title", ""), session_total(summary)]
            continue
        if by == "model":
            models = set(summary.get("main") or {}) | set(summary.get("subagents") or {})
            for model in models:
                row = rows.setdefault(model, [model, "", empty_usage()])
                add_usage(row[2], session_total(summary, model))
            continue
        key = summary.get("device", "?") if by == "device" else (started.strftime("%Y-%m-%d") if started else "?")
        row = rows.setdefault(key, [key, "", empty_usage(), 0])
        add_usage(row[2], session_total(summary))
        row[3] += 1
    result = []
    for row in rows.values():
        title = row[1] if by == "session" else (plural(row[3]) if len(row) > 3 else "")
        result.append((row[0], title, row[2]))
    return result


def format_report(summaries, by):
    columns = (
        ("Requests", "requests"),
        ("Input", "input_tokens"),
        ("Cache write", "cache_creation_input_tokens"),
        ("Cache read", "cache_read_input_tokens"),
        ("Output", "output_tokens"),
        ("Total", None),
    )
    rows = report_rows(summaries, by)
    if not rows:
        return "No sessions recorded yet in %s/." % SUMMARY_DIR.replace(os.sep, "/")
    grand = empty_usage()
    table = []
    for key, title, usage in rows:
        add_usage(grand, usage)
        table.append([key] + [cell(usage, field) for _, field in columns] + [title[:48]])
    table.append(["TOTAL"] + [cell(grand, field) for _, field in columns] + [plural(len(summaries))])
    header = [by.capitalize()] + [name for name, _ in columns] + [""]
    widths = [max(len(r[i]) for r in table + [header]) for i in range(len(header))]

    def line(row):
        parts = [row[0].ljust(widths[0])] + [row[i].rjust(widths[i]) for i in range(1, len(row) - 1)]
        return "  ".join(parts + [row[-1]]).rstrip()

    rule = "-" * len(line(header))
    return "\n".join([line(header), rule] + [line(r) for r in table[:-1]] + [rule, line(table[-1])])


def plural(count):
    return "%d session%s" % (count, "" if count == 1 else "s")


def cell(usage, field):
    value = sum(usage.get(f, 0) for f in USAGE_FIELDS) if field is None else usage.get(field, 0)
    return "{:,}".format(value)


def main(argv=None):
    parser = argparse.ArgumentParser(description="Token usage of the Claude Code sessions in this project.")
    commands = parser.add_subparsers(dest="command")
    commands.add_parser("sync", help="record all local sessions (run by the hooks)")
    report = commands.add_parser("report", help="print the recorded token usage")
    report.add_argument("--by", choices=("session", "day", "device", "model"), default="session")
    report.add_argument("--since", help="only sessions that started on or after YYYY-MM-DD")
    args = parser.parse_args(argv)
    root = project_root()

    if args.command == "sync":
        # Runs inside Claude Code hooks: it must stay silent and must never fail the session.
        try:
            sync(root)
        except Exception:
            pass
        return 0
    if args.command == "report":
        if hasattr(sys.stdout, "reconfigure"):
            sys.stdout.reconfigure(encoding="utf-8")
        sync(root)
        summaries = load_summaries(root)
        if args.since:
            summaries = [s for s in summaries if s.get("started", "")[:10] >= args.since]
        print(format_report(summaries, args.by))
        return 0
    parser.print_help()
    return 1


if __name__ == "__main__":
    sys.exit(main())
