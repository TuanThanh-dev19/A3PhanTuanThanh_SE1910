# Author: Phan Tuan Thanh - CE190595
param([Parameter(Mandatory = $true)][string]$Collection)
$ErrorActionPreference = 'Stop'
$a3Root = Split-Path $PSScriptRoot -Parent
$a3Config = @{}
if (Test-Path -LiteralPath (Join-Path $a3Root 'application-local.properties')) {
    Get-Content -LiteralPath (Join-Path $a3Root 'application-local.properties') | ForEach-Object {
        if ($_ -match '^([^#=]+)=(.*)$') { $a3Config[$Matches[1].Trim()] = $Matches[2] }
    }
}
$a3StaffEmail = if ($env:STAFF_EMAIL) { $env:STAFF_EMAIL } else { $a3Config['app.staff.email'] }
$a3StaffPassword = if ($env:STAFF_PASSWORD) { $env:STAFF_PASSWORD } else { $a3Config['app.staff.password'] }
if (-not $a3StaffEmail -or -not $a3StaffPassword) { throw 'Configure STAFF_EMAIL and STAFF_PASSWORD, or the ignored local properties, before running Postman.' }
$a3Environment = @{
    name = 'Local assignment verification'
    values = @(
        @{ key = 'staffEmail'; value = $a3StaffEmail; enabled = $true },
        @{ key = 'staffPassword'; value = $a3StaffPassword; enabled = $true; type = 'secret' }
    )
}
$a3Temp = Join-Path $a3Root 'tmp'
New-Item -ItemType Directory -Force -Path $a3Temp | Out-Null
$a3EnvironmentPath = Join-Path $a3Temp 'postman-environment.json'
$a3Environment | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $a3EnvironmentPath
postman collection run $Collection -e $a3EnvironmentPath --reporters cli
if ($LASTEXITCODE -ne 0) { throw 'Postman verification failed. Review the local run output.' }
