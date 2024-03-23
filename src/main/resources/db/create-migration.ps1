param (
    [string]$migrationName
)

# Define the path to the template file and the destination folder
$templateFilePath = ".\changelog\sample_migration.sql"
$destinationFolder = ".\changelog\migrations"

# Get the current UNIX timestamp
# $timestamp = Get-Date -UFormat %s
$timestamp = [DateTimeOffset]::Now.ToUnixTimeSeconds()

# Define the new file name
$newFileName = "${timestamp}_${migrationName}.sql"

# Define the path to the new file
$newFilePath = Join-Path -Path $destinationFolder -ChildPath $newFileName

# Copy the template file to the destination folder
Copy-Item -Path $templateFilePath -Destination $newFilePath

# Replace the placeholder with the new file name in the copied file
(Get-Content -Path $newFilePath) -replace 'create_sample_table', "${timestamp}_${migrationName}" | Set-Content -Path $newFilePath
