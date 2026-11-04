# Author: Phan Tuan Thanh - CE190595
param([Parameter(Mandatory = $true)][string]$Collection)
$ErrorActionPreference = 'Stop'
$a3Root = Split-Path $PSScriptRoot -Parent
$a3Config = @{}
Get-Content -LiteralPath (Join-Path $a3Root 'application-local.properties') | ForEach-Object {
    if ($_ -match '^([^#=]+)=(.*)$') { $a3Config[$Matches[1].Trim()] = $Matches[2] }
}
$a3Environment = @{
    name = 'Local assignment verification'
    values = @(
        @{ key = 'staffEmail'; value = $a3Config['app.staff.email']; enabled = $true },
        @{ key = 'staffPassword'; value = $a3Config['app.staff.password']; enabled = $true; type = 'secret' }
    )
}
$a3Temp = Join-Path $a3Root 'tmp'
New-Item -ItemType Directory -Force -Path $a3Temp | Out-Null
$a3EnvironmentPath = Join-Path $a3Temp 'postman-environment.json'
$a3Environment | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $a3EnvironmentPath
postman collection run $Collection -e $a3EnvironmentPath --reporters cli
if ($LASTEXITCODE -ne 0) { throw 'Postman verification failed. Review the local run output.' }
