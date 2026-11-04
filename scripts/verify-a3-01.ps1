# Author: Phan Tuan Thanh - CE190595
param(
    [string]$BackendUrl = 'http://127.0.0.1:8081',
    [string]$FrontendUrl = 'http://127.0.0.1:5173'
)

$ErrorActionPreference = 'Stop'
$a3Rooms = Invoke-RestMethod "$BackendUrl/api/rooms"
$a3Types = Invoke-RestMethod "$BackendUrl/api/room-types"
if ($a3Rooms.Count -lt 3 -or $a3Types.Count -lt 2) {
    throw 'Expected at least three seeded rooms and two room types.'
}
foreach ($a3Number in @('101', '102', '201')) {
    if ($a3Number -notin $a3Rooms.roomNumber) { throw "Seeded room $a3Number was not returned." }
}
if ($a3Rooms[0].PSObject.Properties.Name -contains 'password') {
    throw 'The public response must not contain a password field.'
}
$a3ProxyRooms = Invoke-RestMethod "$FrontendUrl/api/rooms"
$a3ProxyTypes = Invoke-RestMethod "$FrontendUrl/api/room-types"
if (($a3ProxyRooms | ConvertTo-Json -Depth 5 -Compress) -ne ($a3Rooms | ConvertTo-Json -Depth 5 -Compress)) {
    throw 'The frontend proxy returned different room data.'
}
if (($a3ProxyTypes | ConvertTo-Json -Depth 5 -Compress) -ne ($a3Types | ConvertTo-Json -Depth 5 -Compress)) {
    throw 'The frontend proxy returned different room-type data.'
}
$a3Frontend = Invoke-WebRequest $FrontendUrl
if ($a3Frontend.StatusCode -ne 200 -or $a3Frontend.Content -notmatch 'A3PhanTuanThanh_SE1910') {
    throw 'The frontend entry page is unavailable or has the wrong project title.'
}
Write-Output "Verified at: $([DateTimeOffset]::Now.ToString('o'))"
Write-Output "PASS: public GET /api/rooms returned $($a3Rooms.Count) rooms without credentials."
Write-Output "PASS: public GET /api/room-types returned $($a3Types.Count) room types without credentials."
Write-Output 'PASS: frontend /api proxy returned the same SQL Server room and room-type data.'
Write-Output 'PASS: frontend entry page returned HTTP 200 with the assignment project title.'
