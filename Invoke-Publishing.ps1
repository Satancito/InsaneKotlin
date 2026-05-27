[CmdletBinding(DefaultParameterSetName = "Show")]
param(
    [Parameter()]
    [string]$EnvFilePath,
    [Parameter(Mandatory = $true, ParameterSetName = "Edit")]
    [switch]$Edit,
    [Parameter(ParameterSetName = "Edit")]
    [string]$Editor,
    [Parameter(Mandatory = $true, ParameterSetName = "Show")]
    [switch]$Show,
    [Parameter(Mandatory = $true, ParameterSetName = "PublishPackage")]
    [switch]$PublishPackage,
    [Parameter(Mandatory = $true, ParameterSetName = "PublishPublicKey")]
    [switch]$PublishPublicKey
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$scriptDirectory = Split-Path -Path $MyInvocation.MyCommand.Path -Parent

if (-not ($Edit -or $Show -or $PublishPackage -or $PublishPublicKey)) {
    throw "Choose one mode: -Edit, -Show, -PublishPackage, or -PublishPublicKey."
}

function Get-JavaHome {
    param(
        [string]$RequestedJavaHome
    )

    if (-not [string]::IsNullOrWhiteSpace($RequestedJavaHome)) {
        $resolvedRequestedJavaHome = [System.IO.Path]::GetFullPath($RequestedJavaHome)
        if (-not (Test-Path -LiteralPath $resolvedRequestedJavaHome -PathType Container)) {
            throw "JAVA_HOME points to a directory that does not exist: $resolvedRequestedJavaHome"
        }

        $requestedJavaExecutablePath = Join-Path $resolvedRequestedJavaHome "bin\\java.exe"
        if (-not (Test-Path -LiteralPath $requestedJavaExecutablePath -PathType Leaf)) {
            throw "JAVA_HOME does not contain bin\\java.exe: $resolvedRequestedJavaHome"
        }

        return $resolvedRequestedJavaHome
    }

    $javaCommand = Get-Command java -ErrorAction SilentlyContinue
    if ($null -eq $javaCommand) {
        throw "Java was not found in PATH and no valid JAVA_HOME was provided in the secrets file."
    }

    $javaExecutablePath = $javaCommand.Source
    if ([string]::IsNullOrWhiteSpace($javaExecutablePath) -or -not (Test-Path -LiteralPath $javaExecutablePath -PathType Leaf)) {
        throw "Could not resolve java executable path from Get-Command."
    }

    $binDirectory = Split-Path -Path $javaExecutablePath -Parent
    $detectedJavaHome = Split-Path -Path $binDirectory -Parent
    if ([string]::IsNullOrWhiteSpace($detectedJavaHome) -or -not (Test-Path -LiteralPath $detectedJavaHome -PathType Container)) {
        throw "Could not resolve JAVA_HOME from java executable path: $javaExecutablePath"
    }

    return $detectedJavaHome
}

function Get-EnvFilePath {
    param(
        [string]$RequestedPath
    )

    if (-not [string]::IsNullOrWhiteSpace($RequestedPath)) {
        return [System.IO.Path]::GetFullPath($RequestedPath)
    }

    return (Join-Path $scriptDirectory "env.json")
}

function Initialize-EnvFile {
    param(
        [Parameter(Mandatory)]
        [string]$Path
    )

    if (Test-Path -LiteralPath $Path -PathType Leaf) {
        return $false
    }

    $generatedId = [guid]::NewGuid().ToString()
    @{ Id = $generatedId } | ConvertTo-Json -Depth 2 | Set-Content -LiteralPath $Path
    return $true
}

function Invoke-EditorIfAvailable {
    param(
        [Parameter(Mandatory)]
        [string[]]$Paths,
        [string]$EditorCommand
    )

    $runningOnWindows = $PSVersionTable.PSEdition -eq "Desktop" -or $IsWindows
    $resolvedEditorCommand = if ([string]::IsNullOrWhiteSpace($EditorCommand)) {
        if ($runningOnWindows) { "notepad" } else { "vi" }
    }
    else {
        $EditorCommand
    }
    $editor = Get-Command $resolvedEditorCommand -ErrorAction SilentlyContinue
    if ($null -ne $editor) {
        Start-Process -FilePath $editor.Source -ArgumentList $Paths | Out-Null
    }
    else {
        Write-Warning "Editor command '$resolvedEditorCommand' was not found in PATH. Open these files manually: $($Paths -join ', ')"
    }
}

function Get-ConfigId {
    param(
        [Parameter(Mandatory)]
        [string]$ResolvedEnvFilePath
    )

    $config = Get-Content -LiteralPath $ResolvedEnvFilePath -Raw | ConvertFrom-Json
    if ($null -eq $config -or [string]::IsNullOrWhiteSpace($config.Id)) {
        throw "The env file is missing the Id property: $ResolvedEnvFilePath"
    }

    return $config.Id
}

function Get-InsaneSecretsFilePath {
    param(
        [Parameter(Mandatory)]
        [string]$ConfigId
    )

    $insaneDirectory = Join-Path $HOME ".insane"
    New-Item -ItemType Directory -Force -Path $insaneDirectory | Out-Null
    return Join-Path $insaneDirectory "$ConfigId.json"
}

function Read-Secrets {
    param(
        [Parameter(Mandatory)]
        [string]$SecretsFilePath
    )

    if (-not (Test-Path -LiteralPath $SecretsFilePath -PathType Leaf)) {
        return @{}
    }

    $json = Get-Content -LiteralPath $SecretsFilePath -Raw
    if ([string]::IsNullOrWhiteSpace($json)) {
        return @{}
    }

    $loaded = $json | ConvertFrom-Json
    $result = @{}
    foreach ($property in $loaded.PSObject.Properties) {
        $result[$property.Name] = $property.Value
    }

    return $result
}

function Write-Secrets {
    param(
        [Parameter(Mandatory)]
        [string]$SecretsFilePath,
        [Parameter(Mandatory)]
        [hashtable]$Secrets
    )

    $orderedSecrets = [ordered]@{}
    foreach ($key in ($Secrets.Keys | Sort-Object)) {
        $orderedSecrets[$key] = $Secrets[$key]
    }

    $orderedSecrets | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $SecretsFilePath
}

function New-SecretsTemplate {
    return @{
        SIGNING_KEY_PATH = ""
        SIGNING_PASSWORD = ""
        SONATYPE_CENTRAL_USERNAME = ""
        SONATYPE_CENTRAL_PASSWORD = ""
        SONATYPE_CENTRAL_PUBLISHING_TYPE = ""
        JAVA_HOME = ""
        SIGNING_PUBLIC_KEY_PATH = ""
        GPG_KEY_SERVERS = @(
            "keyserver.ubuntu.com",
            "keys.openpgp.org",
            "pgp.mit.edu"
        )
    }
}

function Initialize-OrUpdate-SecretsFile {
    param(
        [Parameter(Mandatory)]
        [string]$SecretsFilePath
    )

    $requiredTemplate = New-SecretsTemplate
    if (-not (Test-Path -LiteralPath $SecretsFilePath -PathType Leaf)) {
        Write-Secrets -SecretsFilePath $SecretsFilePath -Secrets $requiredTemplate
        return $true
    }

    $existingSecrets = Read-Secrets -SecretsFilePath $SecretsFilePath
    $updated = $false
    foreach ($entry in $requiredTemplate.GetEnumerator()) {
        if (-not $existingSecrets.ContainsKey($entry.Key)) {
            $existingSecrets[$entry.Key] = $entry.Value
            $updated = $true
        }
    }

    if ($updated) {
        Write-Secrets -SecretsFilePath $SecretsFilePath -Secrets $existingSecrets
    }

    return $updated
}

function Get-RequiredSecretValue {
    param(
        [Parameter(Mandatory)]
        [hashtable]$Secrets,
        [Parameter(Mandatory)]
        [string]$Name,
        [Parameter(Mandatory)]
        [string]$SecretsFilePath
    )

    $value = [string]$Secrets[$Name]
    if ([string]::IsNullOrWhiteSpace($value)) {
        throw "Missing required property '$Name' in secrets file: $SecretsFilePath"
    }

    return $value
}

function Get-OptionalSecretValue {
    param(
        [Parameter(Mandatory)]
        [hashtable]$Secrets,
        [Parameter(Mandatory)]
        [string]$Name
    )

    return [string]$Secrets[$Name]
}

function Get-KeyServerList {
    param(
        [Parameter(Mandatory)]
        [hashtable]$Secrets,
        [Parameter(Mandatory)]
        [string]$SecretsFilePath
    )

    $rawValue = $Secrets["GPG_KEY_SERVERS"]
    if ($null -eq $rawValue) {
        throw "Missing required property 'GPG_KEY_SERVERS' in secrets file: $SecretsFilePath"
    }

    $serverList = @()
    if ($rawValue -is [System.Array]) {
        $serverList = @($rawValue | ForEach-Object { [string]$_ })
    }
    else {
        $serverList = @(
            ([string]$rawValue -split "[,\r\n;]" | ForEach-Object { $_.Trim() } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
        )
    }

    $serverList = @($serverList | Select-Object -Unique)
    if ($serverList.Count -eq 0) {
        throw "Property 'GPG_KEY_SERVERS' must contain at least one key server in secrets file: $SecretsFilePath"
    }

    return $serverList
}

function Get-GpgExecutablePath {
    $gpgCommand = Get-Command gpg -ErrorAction SilentlyContinue
    if ($null -eq $gpgCommand -or [string]::IsNullOrWhiteSpace($gpgCommand.Source)) {
        $fallbackPaths = @(
            "C:\Program Files\GnuPG\bin\gpg.exe",
            "C:\Program Files\Git\usr\bin\gpg.exe"
        )

        foreach ($fallbackPath in $fallbackPaths) {
            if (Test-Path -LiteralPath $fallbackPath -PathType Leaf) {
                return $fallbackPath
            }
        }

        throw "GPG was not found in PATH and no supported fallback location was found. Install GnuPG or make sure 'gpg' is available."
    }

    if (-not (Test-Path -LiteralPath $gpgCommand.Source -PathType Leaf)) {
        throw "Resolved GPG executable does not exist: $($gpgCommand.Source)"
    }

    return $gpgCommand.Source
}

function Get-PublicKeyFingerprint {
    param(
        [Parameter(Mandatory)]
        [string]$GpgExecutablePath,
        [Parameter(Mandatory)]
        [string]$PublicKeyPath
    )

    $output = & $GpgExecutablePath --show-keys --with-colons --fingerprint $PublicKeyPath 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to read the public key fingerprint from '$PublicKeyPath'. GPG output: $($output -join [Environment]::NewLine)"
    }

    $fingerprint = $output |
        Where-Object { $_ -like "fpr:*" } |
        ForEach-Object { ($_ -split ":")[9] } |
        Where-Object { -not [string]::IsNullOrWhiteSpace($_) } |
        Select-Object -First 1

    if ([string]::IsNullOrWhiteSpace($fingerprint)) {
        throw "Could not extract a fingerprint from public key file: $PublicKeyPath"
    }

    return $fingerprint.Trim()
}

function Publish-PublicKeyToServers {
    param(
        [Parameter(Mandatory)]
        [string]$GpgExecutablePath,
        [Parameter(Mandatory)]
        [string]$PublicKeyPath,
        [Parameter(Mandatory)]
        [string]$Fingerprint,
        [Parameter(Mandatory)]
        [string[]]$KeyServers
    )

    $temporaryHome = Join-Path ([System.IO.Path]::GetTempPath()) ("insane-gpg-" + [guid]::NewGuid().ToString("N"))
    New-Item -ItemType Directory -Force -Path $temporaryHome | Out-Null

    try {
        $null = & $GpgExecutablePath --homedir $temporaryHome --import $PublicKeyPath 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to import the public key into the temporary GPG home."
        }

        foreach ($keyServer in $KeyServers) {
            Write-Host "Uploading public key fingerprint $Fingerprint to $keyServer..." -ForegroundColor Cyan
            $uploadOutput = & $GpgExecutablePath --homedir $temporaryHome --keyserver $keyServer --send-keys $Fingerprint 2>&1
            if ($LASTEXITCODE -ne 0) {
                throw "Failed to upload the public key to '$keyServer'. GPG output: $($uploadOutput -join [Environment]::NewLine)"
            }
        }
    }
    finally {
        if (Test-Path -LiteralPath $temporaryHome -PathType Container) {
            Remove-Item -LiteralPath $temporaryHome -Recurse -Force
        }
    }
}

function Test-PublicKeyAvailabilityOnServers {
    param(
        [Parameter(Mandatory)]
        [string]$GpgExecutablePath,
        [Parameter(Mandatory)]
        [string]$Fingerprint,
        [Parameter(Mandatory)]
        [string[]]$KeyServers
    )

    foreach ($keyServer in $KeyServers) {
        $temporaryHome = Join-Path ([System.IO.Path]::GetTempPath()) ("insane-gpg-verify-" + [guid]::NewGuid().ToString("N"))
        New-Item -ItemType Directory -Force -Path $temporaryHome | Out-Null

        try {
            Write-Host "Verifying public key fingerprint $Fingerprint on $keyServer..." -ForegroundColor Cyan
            $verifyOutput = & $GpgExecutablePath --homedir $temporaryHome --keyserver $keyServer --recv-keys $Fingerprint 2>&1
            $recvExitCode = $LASTEXITCODE
            $verifyOutputText = $verifyOutput -join [Environment]::NewLine
            $shortKeyId = $Fingerprint.Substring([Math]::Max(0, $Fingerprint.Length - 16))

            $fingerprintLookup = & $GpgExecutablePath --homedir $temporaryHome --list-keys --with-colons $Fingerprint 2>&1
            $resolvedFingerprint = $fingerprintLookup |
                Where-Object { $_ -like "fpr:*" } |
                ForEach-Object { ($_ -split ":")[9] } |
                Where-Object { -not [string]::IsNullOrWhiteSpace($_) } |
                Select-Object -First 1

            $fingerprintMatched = -not [string]::IsNullOrWhiteSpace($resolvedFingerprint) -and
                $resolvedFingerprint.Trim().ToUpperInvariant() -eq $Fingerprint.Trim().ToUpperInvariant()

            $processedKeyMatched = $verifyOutputText -match [regex]::Escape($shortKeyId) -and
                $verifyOutputText -match "Total number processed:\s*[1-9]\d*"

            if (-not $fingerprintMatched -and -not $processedKeyMatched) {
                throw "Key server '$keyServer' did not return the expected public key. recv-keys exit code: $recvExitCode. GPG output: $verifyOutputText"
            }
        }
        finally {
            if (Test-Path -LiteralPath $temporaryHome -PathType Container) {
                Remove-Item -LiteralPath $temporaryHome -Recurse -Force
            }
        }
    }
}

function Write-SecretsSummary {
    param(
        [Parameter(Mandatory)]
        [hashtable]$Secrets,
        [Parameter(Mandatory)]
        [string]$EnvFilePath,
        [Parameter(Mandatory)]
        [string]$SecretsFilePath
    )

    $secretKeys = @(
        "SIGNING_PASSWORD",
        "SONATYPE_CENTRAL_PASSWORD"
    )

    Write-Host "Env file: $EnvFilePath"
    Write-Host "Secrets file: $SecretsFilePath"
    Write-Host ""

    foreach ($key in ($Secrets.Keys | Sort-Object)) {
        $value = $Secrets[$key]
        if ($secretKeys -contains $key) {
            $displayValue = if ($null -eq $value -or [string]::IsNullOrWhiteSpace([string]$value)) { "<empty>" } else { "<secret>" }
        }
        elseif ($value -is [System.Array]) {
            $displayValue = if ($value.Count -eq 0) { "<empty>" } else { ($value -join ", ") }
        }
        else {
            $displayValue = if ([string]::IsNullOrWhiteSpace([string]$value)) { "<empty>" } else { [string]$value }
        }

        Write-Host ("{0} = {1}" -f $key, $displayValue)
    }
}

function Publish-Package {
    param(
        [Parameter(Mandatory)]
        [hashtable]$Secrets,
        [Parameter(Mandatory)]
        [string]$SecretsFilePath
    )

    $signingKeyPath = Get-RequiredSecretValue -Secrets $Secrets -Name "SIGNING_KEY_PATH" -SecretsFilePath $SecretsFilePath
    if (-not (Test-Path -LiteralPath $signingKeyPath -PathType Leaf)) {
        throw "Signing key file not found: $signingKeyPath"
    }

    $signingKey = Get-Content -LiteralPath $signingKeyPath -Raw
    if ([string]::IsNullOrWhiteSpace($signingKey)) {
        throw "Signing key file is empty: $signingKeyPath"
    }

    $signingPassword = Get-RequiredSecretValue -Secrets $Secrets -Name "SIGNING_PASSWORD" -SecretsFilePath $SecretsFilePath
    $sonatypeCentralUsername = Get-RequiredSecretValue -Secrets $Secrets -Name "SONATYPE_CENTRAL_USERNAME" -SecretsFilePath $SecretsFilePath
    $sonatypeCentralPassword = Get-RequiredSecretValue -Secrets $Secrets -Name "SONATYPE_CENTRAL_PASSWORD" -SecretsFilePath $SecretsFilePath
    $sonatypeCentralPublishingType = Get-OptionalSecretValue -Secrets $Secrets -Name "SONATYPE_CENTRAL_PUBLISHING_TYPE"
    if ([string]::IsNullOrWhiteSpace($sonatypeCentralPublishingType)) {
        $sonatypeCentralPublishingType = "user_managed"
    }
    if ($sonatypeCentralPublishingType -notin @("user_managed", "automatic")) {
        throw "Invalid SONATYPE_CENTRAL_PUBLISHING_TYPE '$sonatypeCentralPublishingType' in secrets file: $SecretsFilePath"
    }

    $javaHome = Get-JavaHome -RequestedJavaHome (Get-OptionalSecretValue -Secrets $Secrets -Name "JAVA_HOME")

    $env:ORG_GRADLE_PROJECT_SIGNING_KEY = $signingKey
    $env:ORG_GRADLE_PROJECT_SIGNING_PASSWORD = $signingPassword
    $env:ORG_GRADLE_PROJECT_SONATYPE_CENTRAL_USERNAME = $sonatypeCentralUsername
    $env:ORG_GRADLE_PROJECT_SONATYPE_CENTRAL_PASSWORD = $sonatypeCentralPassword
    $env:ORG_GRADLE_PROJECT_SONATYPE_CENTRAL_PUBLISHING_TYPE = $sonatypeCentralPublishingType
    $env:JAVA_HOME = $javaHome
    $env:Path = "$javaHome\bin;$env:Path"

    Write-Host "Publishing package environment loaded for this PowerShell session." -ForegroundColor Green
    Write-Host "Secrets file: $SecretsFilePath"
    Write-Host "SONATYPE_CENTRAL_USERNAME: $sonatypeCentralUsername"
    Write-Host "SONATYPE_CENTRAL_PUBLISHING_TYPE: $sonatypeCentralPublishingType"
    Write-Host "JAVA_HOME: $javaHome"
    Write-Host ""
    Write-Host "Running publishReleaseToCentralPortal..." -ForegroundColor Cyan

    & ".\gradlew.bat" "publishReleaseToCentralPortal" "--stacktrace"
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle publish failed with exit code $LASTEXITCODE."
    }
}

function Publish-PublicKey {
    param(
        [Parameter(Mandatory)]
        [hashtable]$Secrets,
        [Parameter(Mandatory)]
        [string]$SecretsFilePath
    )

    $publicKeyPath = Get-RequiredSecretValue -Secrets $Secrets -Name "SIGNING_PUBLIC_KEY_PATH" -SecretsFilePath $SecretsFilePath
    if (-not (Test-Path -LiteralPath $publicKeyPath -PathType Leaf)) {
        throw "Public signing key file not found: $publicKeyPath"
    }

    $keyServers = Get-KeyServerList -Secrets $Secrets -SecretsFilePath $SecretsFilePath
    $gpgExecutablePath = Get-GpgExecutablePath
    $fingerprint = Get-PublicKeyFingerprint -GpgExecutablePath $gpgExecutablePath -PublicKeyPath $publicKeyPath

    Write-Host "Publishing public key environment loaded for this PowerShell session." -ForegroundColor Green
    Write-Host "Secrets file: $SecretsFilePath"
    Write-Host "GPG executable: $gpgExecutablePath"
    Write-Host "Public key: $publicKeyPath"
    Write-Host "Fingerprint: $fingerprint"
    Write-Host "Key servers: $($keyServers -join ', ')"
    Write-Host ""

    Publish-PublicKeyToServers -GpgExecutablePath $gpgExecutablePath -PublicKeyPath $publicKeyPath -Fingerprint $fingerprint -KeyServers $keyServers
    Write-Host ""
    Test-PublicKeyAvailabilityOnServers -GpgExecutablePath $gpgExecutablePath -Fingerprint $fingerprint -KeyServers $keyServers
    Write-Host "Public key upload finished successfully." -ForegroundColor Green
}

$resolvedEnvFilePath = Get-EnvFilePath -RequestedPath $EnvFilePath
$createdEnvFile = Initialize-EnvFile -Path $resolvedEnvFilePath
$configId = Get-ConfigId -ResolvedEnvFilePath $resolvedEnvFilePath
$secretsFilePath = Get-InsaneSecretsFilePath -ConfigId $configId
$createdOrUpdatedSecretsFile = Initialize-OrUpdate-SecretsFile -SecretsFilePath $secretsFilePath

if ($Edit) {
    Invoke-EditorIfAvailable -Paths @($secretsFilePath) -EditorCommand $Editor
    Write-Warning "Opened the secrets file for editing: $secretsFilePath"
    return
}

if ($createdEnvFile -or $createdOrUpdatedSecretsFile) {
    Invoke-EditorIfAvailable -Paths @($secretsFilePath) -EditorCommand $Editor
    Write-Warning "Environment files were initialized or updated. Fill in the required properties and run the script again."
    return
}

$secrets = Read-Secrets -SecretsFilePath $secretsFilePath

if ($Show) {
    Write-SecretsSummary -Secrets $secrets -EnvFilePath $resolvedEnvFilePath -SecretsFilePath $secretsFilePath
    return
}

if ($PublishPackage) {
    Publish-Package -Secrets $secrets -SecretsFilePath $secretsFilePath
    return
}

if ($PublishPublicKey) {
    Publish-PublicKey -Secrets $secrets -SecretsFilePath $secretsFilePath
    return
}
