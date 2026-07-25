$ErrorActionPreference = "Stop"

$backendRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$envFile = Join-Path $backendRoot ".env.local"

if (-not (Test-Path -LiteralPath $envFile)) {
    throw "Missing .env.local. Create it from .env.local.example and add your Elastic Email SMTP values."
}

Get-Content -LiteralPath $envFile | ForEach-Object {
    $line = $_.Trim()

    if (-not $line -or $line.StartsWith("#")) {
        return
    }

    $separatorIndex = $line.IndexOf("=")
    if ($separatorIndex -lt 1) {
        throw "Invalid .env.local line: $line"
    }

    $name = $line.Substring(0, $separatorIndex).Trim()
    $value = $line.Substring($separatorIndex + 1).Trim()

    if (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'"))) {
        $value = $value.Substring(1, $value.Length - 2)
    }

    [Environment]::SetEnvironmentVariable($name, $value, "Process")
}

$requiredVariables = @(
    "BOOKING_EMAIL_ENABLED",
    "BOOKING_EMAIL_TRANSPORT",
    "BOOKING_EMAIL_FROM"
)

if ($env:BOOKING_EMAIL_TRANSPORT -eq "elastic-api") {
    $requiredVariables += "ELASTIC_EMAIL_API_KEY"
}
else {
    $requiredVariables += @(
    "SMTP_HOST",
    "SMTP_PORT",
    "SMTP_USERNAME",
    "SMTP_PASSWORD",
    "SMTP_AUTH",
    "SMTP_STARTTLS_ENABLE"
    )
}

foreach ($name in $requiredVariables) {
    $value = [Environment]::GetEnvironmentVariable($name, "Process")

    if ([string]::IsNullOrWhiteSpace($value) -or $value -like "your-*" -or $value -like "replace-*") {
        throw "Set $name in .env.local before starting the backend."
    }
}

if ($env:BOOKING_EMAIL_ENABLED -ne "true") {
    throw "BOOKING_EMAIL_ENABLED must be true for confirmation emails to send."
}

Push-Location $backendRoot
try {
    & .\mvnw.cmd spring-boot:run
}
finally {
    Pop-Location
}
