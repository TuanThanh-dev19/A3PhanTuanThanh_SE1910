# Author: Phan Tuan Thanh - CE190595
param([switch]$Stop)
$ErrorActionPreference = 'Stop'
$a3Root = Split-Path $PSScriptRoot -Parent
$a3Runtime = Join-Path $a3Root 'tmp/runtime'
$a3Jar = Join-Path $a3Runtime 'hotel.jar'
$a3PidFile = Join-Path $a3Runtime 'backend.pid'
New-Item -ItemType Directory -Force -Path $a3Runtime | Out-Null
if (Test-Path -LiteralPath $a3PidFile) {
    $a3ProcessId = [int](Get-Content -LiteralPath $a3PidFile)
    $a3Process = Get-CimInstance Win32_Process -Filter "ProcessId = $a3ProcessId"
    if ($a3Process) {
        if ($a3Process.CommandLine -notlike "*$a3Jar*") { throw 'Recorded PID belongs to another process.' }
        Stop-Process -Id $a3ProcessId
        Wait-Process -Id $a3ProcessId -ErrorAction SilentlyContinue
    }
}
if ($Stop) { Write-Output 'Assignment backend stopped.'; return }
Copy-Item -LiteralPath (Join-Path $a3Root 'target/A3PhanTuanThanh_SE1910-0.0.1-SNAPSHOT.jar') -Destination $a3Jar -Force
$a3Process = Start-Process -FilePath (Get-Command java).Source -ArgumentList '-jar', ('"' + $a3Jar + '"') -WorkingDirectory $a3Root -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $a3Runtime 'stdout.txt') -RedirectStandardError (Join-Path $a3Runtime 'stderr.txt')
$a3Process.Id | Set-Content -LiteralPath $a3PidFile
Write-Output "Assignment backend started (PID $($a3Process.Id)); logs: tmp/runtime."
