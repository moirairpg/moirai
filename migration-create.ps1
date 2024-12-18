param (
    [string]$migrationName
)

$templateFilePath = ".\src\main\resources\db\changelog\sample_migration.sql"
$destinationFolder = ".\src\main\resources\db\changelog\migrations"
$timestamp = [DateTimeOffset]::Now.ToUnixTimeSeconds()
$newFileName = "${timestamp}_${migrationName}.sql"
$newFilePath = Join-Path -Path $destinationFolder -ChildPath $newFileName

echo "Creating migration with name ${newFileName}..."

Copy-Item -Path $templateFilePath -Destination $newFilePath
(Get-Content -Path $newFilePath) -replace 'create_sample_table', "${timestamp}_${migrationName}" | Set-Content -Path $newFilePath

echo "Migration with name ${newFileName} created."
