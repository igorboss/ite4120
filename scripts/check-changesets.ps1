# Check every Liquibase changelog against the formatted-SQL grammar and layout
# rules. Run before any PR that touches backend/src/main/resources/**/db/changelog.
$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)
python3 scripts/check_changesets.py
exit $LASTEXITCODE
