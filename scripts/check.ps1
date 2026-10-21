<#
.SYNOPSIS
    Runs the "Definition of done" checks from the AI rules file and prints only a short summary.

.DESCRIPTION
    Meant for AI agents: the full Gradle output always goes to build/agent-logs/last-build.log, and
    only a summary reaches the screen. On success that is one line; on failure at most 80 lines with
    the Kotlin compile errors, the failed tests and their assertion messages, the lint errors, and
    the path of the full log. The exit code is Gradle's.

    With no arguments it runs the Android and web builds, all unit tests, and lint. Pass Gradle tasks
    to run only those, for example: scripts/check.ps1 :domain:allTests

.PARAMETER Ios
    Also runs the iOS checks. They need macOS, so on Windows this only prints how to run them.
#>
param(
    [switch]$Ios,
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Tasks
)

$ErrorActionPreference = 'Stop'
$MaxLines = 80
$DefaultTasks = @(':androidApp:assembleDebug', ':webApp:wasmJsBrowserDistribution', 'allTests', 'lint')
$Dash = [char]0x2014

$root = Split-Path -Parent $PSScriptRoot
$fullLogLine = 'Full log: build/agent-logs/last-build.log'

if ($Ios) {
    Write-Output 'iOS checks need macOS and Xcode. On a Mac run: scripts/check.sh --ios'
    exit 2
}

$isDefault = -not $Tasks
if ($isDefault) { $Tasks = $DefaultTasks }

New-Item -ItemType Directory -Force (Join-Path $root 'build\agent-logs') | Out-Null
$log = Join-Path $root 'build\agent-logs\last-build.log'
$start = Get-Date

Push-Location $root
try {
    # cmd keeps Gradle's stdout and stderr in one file, in order, without PowerShell error records.
    cmd /c ".\gradlew.bat $($Tasks -join ' ') --console=plain --continue > build\agent-logs\last-build.log 2>&1"
    $exitCode = $LASTEXITCODE
} finally {
    Pop-Location
}

# Files in a folder of a module's build directory, for example test-results or reports.
function Get-BuildFiles([string]$folder, [string]$filter, [switch]$Fresh) {
    Get-ChildItem $root -Recurse -Depth 5 -Directory -Filter $folder -ErrorAction SilentlyContinue |
        Where-Object { $_.Parent.Name -eq 'build' -and $_.FullName -notmatch 'node_modules' } |
        ForEach-Object { Get-ChildItem $_.FullName -Recurse -File -Filter $filter } |
        Where-Object { -not $Fresh -or $_.LastWriteTime -ge $start }
}

function Read-Xml([System.IO.FileInfo]$file) {
    $doc = New-Object System.Xml.XmlDocument
    $doc.Load($file.FullName)
    $doc
}

function Get-RelativePath([string]$path) {
    if ($path.StartsWith("$root\", [StringComparison]::OrdinalIgnoreCase)) { $path = $path.Substring($root.Length + 1) }
    $path.Replace('\', '/')
}

function Get-PassedTestCount([switch]$Fresh) {
    $count = 0
    foreach ($file in Get-BuildFiles 'test-results' 'TEST-*.xml' -Fresh:$Fresh) {
        $suite = (Read-Xml $file).testsuite
        $count += [int]$suite.tests - [int]$suite.failures - [int]$suite.errors - [int]$suite.skipped
    }
    $count
}

if ($exitCode -eq 0) {
    if ($isDefault) {
        Write-Output "BUILD OK $Dash $(Get-PassedTestCount) tests passed, lint clean"
    } else {
        $passed = Get-PassedTestCount -Fresh
        $tests = if ($passed -gt 0) { ", $passed tests passed" } else { '' }
        Write-Output "BUILD OK $Dash $($Tasks -join ' ')$tests"
    }
    exit 0
}

$out = New-Object System.Collections.Generic.List[string]
$out.Add("BUILD FAILED (exit $exitCode)")
$logLines = Get-Content $log

# Kotlin compile errors: "e: file:///C:/.../Foo.kt:12:5 Unresolved reference 'x'."
$rootPrefix = [regex]::Escape($root.Replace('\', '/') + '/')
$compileErrors = @($logLines | Where-Object { $_ -like 'e: *' } |
    ForEach-Object { $_ -replace '^e: (file:///)?', '' -replace "^$rootPrefix", '' } |
    Select-Object -Unique)
if ($compileErrors) {
    $out.Add('')
    $out.Add("Compile errors ($($compileErrors.Count)):")
    $compileErrors | ForEach-Object { $out.Add("  $_") }
}

# Failed tests, from the XML results written by this run. The folder name is the test task.
$failures = New-Object System.Collections.Generic.List[string]
foreach ($file in Get-BuildFiles 'test-results' 'TEST-*.xml' -Fresh) {
    foreach ($case in (Read-Xml $file).testsuite.testcase) {
        $problem = if ($case.failure) { $case.failure } else { $case.error }
        if (-not $problem) { continue }
        $failures.Add("  FAILED $($case.classname) > $($case.name) [$($file.Directory.Name)]")
        $text = if ($problem.InnerText) { $problem.InnerText } else { [string]$problem.message }
        $message = if ($problem.message) { [string]$problem.message } else { $text }
        $message -split "`r?`n" | Where-Object { $_.Trim() } | Select-Object -First 3 |
            ForEach-Object { $failures.Add("      $($_.Trim())") }
        $simpleName = ($case.classname -split '\.')[-1]
        $location = $text -split "`r?`n" | Where-Object { $_ -like "*$simpleName.kt:*" } | Select-Object -First 1
        if ($location) { $failures.Add("      $($location.Trim())") }
    }
}
if ($failures.Count -gt 0) {
    $out.Add('')
    $out.Add('Failed tests:')
    $out.AddRange($failures)
}

# Lint errors, from the failure message Gradle prints: "C:\...\File.xml:7: Error: Message [IssueId]".
# The report is not always rewritten (its task can be up to date), so it is only pointed to.
if ($logLines -match 'Lint found errors') {
    $lintPattern = '^\s*(.+?):(\d+): (Error|Fatal): (.+) \[(\w+)\]$'
    $lintErrors = @($logLines | Where-Object { $_ -match $lintPattern } | ForEach-Object { $_.Trim() } |
        Select-Object -Unique)
    $reports = Get-BuildFiles 'reports' 'lint-results-*.txt' |
        Where-Object { Select-String -Path $_.FullName -Pattern ': (Error|Fatal):' -Quiet } |
        ForEach-Object { Get-RelativePath $_.FullName }
    $out.Add('')
    $out.Add("Lint: $($lintErrors.Count) errors $Dash $($reports -join ', ')")
    foreach ($line in $lintErrors | Select-Object -First 10) {
        $null = $line -match $lintPattern
        $out.Add("  $($Matches[5]): $(Get-RelativePath $Matches[1]):$($Matches[2]) $($Matches[4])")
    }
}

# Anything else (configuration, dependency, or tool errors): Gradle's own explanation.
if ($out.Count -eq 1) {
    $inBlock = $false
    $blockLines = 0
    foreach ($line in $logLines) {
        if ($line -like '`* What went wrong:*') { $inBlock = $true; $blockLines = 0; $out.Add(''); continue }
        if ($line -like '`* Try:*') { $inBlock = $false }
        if ($inBlock -and $blockLines -lt 15) { $out.Add($line); $blockLines++ }
    }
}
if ($out.Count -eq 1) {
    $out.Add('')
    $logLines | Select-Object -Last 15 | ForEach-Object { $out.Add($_) }
}

if ($out.Count -gt $MaxLines - 1) {
    $more = $out.Count - ($MaxLines - 2)
    $out = $out.GetRange(0, $MaxLines - 2)
    $out.Add("... $more more lines in the full log")
}
$out.Add($fullLogLine)
$out | Write-Output
exit $exitCode
