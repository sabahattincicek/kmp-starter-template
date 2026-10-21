#!/usr/bin/env bash
# Runs the "Definition of done" checks from the AI rules file and prints only a short summary.
#
# Meant for AI agents: the full output always goes to build/agent-logs/last-build.log, and only a
# summary reaches the screen. On success that is one line; on failure at most 80 lines with the
# Kotlin compile errors, the failed tests and their assertion messages, the lint errors, and the
# path of the full log. The exit code is Gradle's (or Xcode's, for the iOS app build).
#
# Usage:
#   scripts/check.sh                     Android and web builds, all unit tests, and lint
#   scripts/check.sh --ios               the same, plus the iOS simulator tests and the Xcode build (macOS only)
#   scripts/check.sh :domain:allTests    only the given Gradle tasks
#
# Windows has scripts/check.ps1 with the same behavior.

max_lines=80
default_tasks=(:androidApp:assembleDebug :webApp:wasmJsBrowserDistribution allTests lint)
ios_tasks=(iosSimulatorArm64Test)

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root" || exit 1
log_dir=build/agent-logs
log="$log_dir/last-build.log"
marker="$log_dir/.started"
summary="$log_dir/.summary"
full_log_line="Full log: build/agent-logs/last-build.log"

ios=false
tasks=()
for arg in "$@"; do
  case "$arg" in
    --ios) ios=true ;;
    *) tasks+=("$arg") ;;
  esac
done

if $ios && [ "$(uname -s)" != Darwin ]; then
  echo "iOS checks need macOS and Xcode."
  exit 2
fi

is_default=false
if [ ${#tasks[@]} -eq 0 ]; then
  is_default=true
  tasks=("${default_tasks[@]}")
fi
if $ios; then tasks+=("${ios_tasks[@]}"); fi

mkdir -p "$log_dir"
touch "$marker"

./gradlew "${tasks[@]}" --console=plain --continue >"$log" 2>&1
status=$?

if [ $status -eq 0 ] && $ios; then
  # Same Xcode build as CI: arm64 simulator only, no code signing.
  {
    echo
    echo "> xcodebuild iosApp"
    (cd iosApp && xcodebuild -project iosApp.xcodeproj -scheme iosApp -configuration Debug \
      -sdk iphonesimulator -arch arm64 CODE_SIGNING_ALLOWED=NO build)
  } >>"$log" 2>&1
  status=$?
fi

# Files in a folder of a module's build directory. $1: folder, $2: file name pattern,
# $3: "fresh" for only the files written by this run.
build_files() {
  if [ "$3" = fresh ]; then
    find . \( -name node_modules -o -name .git -o -name .gradle \) -prune -o \
      -path "*/build/$1/*" -name "$2" -newer "$marker" -print
  else
    find . \( -name node_modules -o -name .git -o -name .gradle \) -prune -o \
      -path "*/build/$1/*" -name "$2" -print
  fi
}

# Number of passed tests in the given JUnit XML files.
count_passed() {
  [ $# -eq 0 ] && { echo 0; return; }
  awk '
    function attr(line, name) {
      if (match(line, " " name "=\"[^\"]*\"")) return substr(line, RSTART + length(name) + 3, RLENGTH - length(name) - 4)
      return 0
    }
    /<testsuite / { n += attr($0, "tests") - attr($0, "failures") - attr($0, "errors") - attr($0, "skipped") }
    END { print n + 0 }
  ' "$@"
}

if [ $status -eq 0 ]; then
  if $is_default; then
    # shellcheck disable=SC2046
    echo "BUILD OK — $(count_passed $(build_files test-results 'TEST-*.xml')) tests passed, lint clean"
  else
    # shellcheck disable=SC2046
    passed=$(count_passed $(build_files test-results 'TEST-*.xml' fresh))
    tests=""
    [ "$passed" -gt 0 ] && tests=", $passed tests passed"
    echo "BUILD OK — ${tasks[*]}$tests"
  fi
  exit 0
fi

{
  echo "BUILD FAILED (exit $status)"

  # Kotlin compile errors: "e: file:///.../Foo.kt:12:5 Unresolved reference 'x'."
  compile_errors=$(grep '^e: ' "$log" | sed -e 's#^e: ##' -e 's#^file://##' -e "s#^$root/##" | awk '!seen[$0]++')
  if [ -n "$compile_errors" ]; then
    echo
    echo "Compile errors ($(echo "$compile_errors" | wc -l | tr -d ' ')):"
    echo "$compile_errors" | sed 's/^/  /'
  fi

  # Failed tests, from the XML results written by this run. The folder name is the test task.
  results=$(build_files test-results 'TEST-*.xml' fresh)
  if [ -n "$results" ]; then
    # shellcheck disable=SC2086
    failures=$(awk '
      function attr(line, name) {
        if (match(line, " " name "=\"[^\"]*\"")) return substr(line, RSTART + length(name) + 3, RLENGTH - length(name) - 4)
        return ""
      }
      function unescape(s) {
        gsub(/&lt;/, "<", s); gsub(/&gt;/, ">", s); gsub(/&quot;/, "\"", s); gsub(/&apos;/, "'\''", s)
        gsub(/&#10;/, "\n", s); gsub(/&amp;/, "\\&", s)
        return s
      }
      FNR == 1 { count = split(FILENAME, parts, "/"); task = parts[count - 1]; inside = 0 }
      /<testcase / {
        cls = attr($0, "classname"); name = attr($0, "name")
        count = split(cls, parts, "."); simple = parts[count]
      }
      /<(failure|error) / {
        print "  FAILED " cls " > " name " [" task "]"
        count = split(unescape(attr($0, "message")), lines, "\n")
        for (i = 1; i <= count && i <= 3; i++) if (lines[i] != "") print "      " lines[i]
        inside = 1; located = 0
      }
      inside && !located && index($0, simple ".kt:") {
        line = $0; sub(/^[ \t]+/, "", line); print "      " line; located = 1
      }
      /<\/(failure|error)>/ { inside = 0 }
    ' $results)
    if [ -n "$failures" ]; then
      echo
      echo "Failed tests:"
      echo "$failures"
    fi
  fi

  # Lint errors, from the failure message Gradle prints: "/.../File.xml:7: Error: Message [IssueId]".
  # The report is not always rewritten (its task can be up to date), so it is only pointed to.
  if grep -q 'Lint found errors' "$log"; then
    lint_errors=$(grep -E '^ *.+:[0-9]+: (Error|Fatal): .+ \[[A-Za-z0-9_]+\]$' "$log" | sed 's/^ *//' | awk '!seen[$0]++')
    reports=""
    for report in $(build_files reports 'lint-results-*.txt'); do
      grep -qE ': (Error|Fatal):' "$report" && reports="$reports ${report#./}"
    done
    echo
    echo "Lint: $(echo "$lint_errors" | grep -c .) errors —$reports"
    echo "$lint_errors" | head -n 10 | sed -E -e "s#^$root/##" \
      -e 's/^(.+):([0-9]+): (Error|Fatal): (.+) \[([A-Za-z0-9_]+)\]$/  \5: \1:\2 \4/'
  fi

  # Xcode errors: "/.../ContentView.swift:12:5: error: cannot find 'x' in scope".
  if $ios; then
    grep -E '(: error: |^\*\* BUILD FAILED)' "$log" | sed "s#^$root/##" | awk '!seen[$0]++' | head -n 20
  fi
} >"$summary"

# Anything else (configuration, dependency, or tool errors): Gradle's own explanation, or the log end.
if [ "$(wc -l <"$summary")" -le 1 ]; then
  awk '/^\* What went wrong:/ { p = 1; c = 0; print ""; next } /^\* Try:/ { p = 0 } p && c < 15 { print; c++ }' \
    "$log" >>"$summary"
fi
if [ "$(wc -l <"$summary")" -le 1 ]; then
  { echo; tail -n 15 "$log"; } >>"$summary"
fi

total=$(wc -l <"$summary")
if [ "$total" -gt $((max_lines - 1)) ]; then
  head -n $((max_lines - 2)) "$summary"
  echo "... $((total - max_lines + 2)) more lines in the full log"
else
  cat "$summary"
fi
echo "$full_log_line"
exit $status
