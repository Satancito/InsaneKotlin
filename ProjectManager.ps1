[CmdletBinding(DefaultParameterSetName = "Help")]
param(
    [Parameter(Mandatory = $true, ParameterSetName = "Tools")]
    [ValidateSet("Add", "List", "Remove", "Update")]
    [string]$Tools,

    [Parameter(ParameterSetName = "Tools")]
    [string]$RepositoryName,

    [Parameter(ParameterSetName = "Tools")]
    [string]$RepositoryUrl,

    [Parameter(ParameterSetName = "Tools")]
    [AllowEmptyString()]
    [string]$Tag,

    [Parameter(Mandatory = $true, ParameterSetName = "Project")]
    [ValidateSet("Get", "Set")]
    [string]$Project,

    [AllowNull()]
    [AllowEmptyString()]
    [Parameter(ParameterSetName = "Project")]
    [object]$Name,

    [Parameter(Mandatory = $true, ParameterSetName = "SelfUpdate")]
    [switch]$SelfUpdate,

    [Parameter(Mandatory = $true, ParameterSetName = "SubModules")]
    [switch]$SubModules,

    [Parameter(Mandatory = $true, ParameterSetName = "Init")]
    [switch]$Init,

    [Parameter(Mandatory = $true, ParameterSetName = "Edit")]
    [switch]$Edit,

    [Parameter(ParameterSetName = "Edit")]
    [string]$Editor,

    [Parameter(Mandatory = $true, ParameterSetName = "List")]
    [switch]$List,

    [Parameter(Mandatory = $true, ParameterSetName = "Version")]
    [switch]$Version,

    [Parameter(ParameterSetName = "Help")]
    [Alias("h", "usage")]
    [switch]$Help
)

$ErrorActionPreference = "Stop"
$ProjectManagerVersion = "0.1.0"
$ProjectConfigurationPath = "Project.json"
$ProjectManagerScriptUrl = "https://raw.githubusercontent.com/Satancito/ToolsManagerPs/main/ProjectManager.ps1"

function Show-Usage {
    $usage = @"
Project Manager

Usage:
  .\ProjectManager.ps1 -Tools Update
  .\ProjectManager.ps1 -Tools Update -RepositoryName <Name> [-RepositoryUrl <Url>] [-Tag <Value>]
  .\ProjectManager.ps1 -Tools Add -RepositoryName <Name> -RepositoryUrl <Url> [-Tag <Value>]
  .\ProjectManager.ps1 -Tools List
  .\ProjectManager.ps1 -Tools Remove -RepositoryName <Name>
  .\ProjectManager.ps1 -SubModules
  .\ProjectManager.ps1 -Init
  .\ProjectManager.ps1 -List
  .\ProjectManager.ps1 -Edit [-Editor <Editor>]
  .\ProjectManager.ps1 -Project Get
  .\ProjectManager.ps1 -Project Set -Name <Name>
  .\ProjectManager.ps1 -Version
  .\ProjectManager.ps1 -SelfUpdate
  .\ProjectManager.ps1 -Help
  .\ProjectManager.ps1 -h
  .\ProjectManager.ps1 -usage

Notes:
  Parameters and command values are case-insensitive. For example, -List and -list are equivalent,
  and -Project Get and -project get are equivalent.

Help aliases:
  -Help
  -h
  -usage

Modes:
  -Version
    Returns the ProjectManager.ps1 script version as a capturable JSON string.

  -Project
    Use -Project Get to read the Project value as JSON.
    Use -Project Set -Name <Name> to update Project and return the saved value as JSON.

  -SelfUpdate
    Downloads the latest ProjectManager.ps1 from:
      $ProjectManagerScriptUrl

    If the downloaded content differs from the current script, it replaces the current script file, stages it,
    and creates a commit:
      Update ProjectManager.ps1

  -SubModules
    Runs -Tools Update first, then lists only the Git submodules managed by ProjectTools in Project.json.

  -Init
    Creates Project.json if it does not exist. If Project.json exists, it ensures the Project property exists
    with null as the default value and ProjectTools exists as an array. If Project.json or ProjectTools are
    regenerated, the script runs -Tools Update to repair the configured submodules.

  -List
    Initializes Project.json and writes the complete configuration as indented JSON.

  -Edit
    Initializes Project.json and opens it in an editor. If -Editor is not provided, Windows uses notepad and
    Unix-like systems use vi.

  -Tools Update
    Reads Project.json and ensures every ProjectTools entry exists as a Git submodule under Tools/<RepositoryName>.
    Directories and .gitmodules entries under Tools that are not listed in ProjectTools are removed.
    If the submodule does not exist, it is added with:
      git submodule add <RepositoryUrl> Tools/<RepositoryName>

    Then the script keeps the submodule aligned to the configured Tag value by running:
      git submodule sync --recursive Tools/<RepositoryName>
      git submodule update --init --recursive Tools/<RepositoryName>
      git -C Tools/<RepositoryName> fetch --tags

    If Tag has a value, the script first tries to checkout it as a commit hash. If that fails, it tries the
    value as a tag. If Tag is null or empty, the script checks out the latest remote HEAD commit.

    When the submodule checkout changes, the script stages:
      git add Tools/<RepositoryName>

    It creates a commit only when at least one tool submodule changed:
      Use <RepositoryName> <Tag>

  -Tools Update -RepositoryName <Name>
    Updates an existing ProjectTools entry. RepositoryName is required for this mode. If the tool exists,
    the script runs -Tools Update first. If the tool does not exist in Project.json, the operation is cancelled.

    RepositoryUrl and Tag are optional. If RepositoryUrl is provided, it is validated before saving. If Tag is
    provided as empty, Tag is stored as null. After Project.json is updated, the script runs:
      .\ProjectManager.ps1 -Tools Update

  -Tools Add
    Runs -Tools Update first, validates RepositoryName, RepositoryUrl, and optional Tag, adds a new
    ProjectTools entry in Project.json, stages and commits Project.json, then runs:
      .\ProjectManager.ps1 -Tools Update

    If RepositoryName already exists in Project.json or is already registered as a Git submodule, the add
    operation is rejected.

    If Tag is empty or omitted, Tag is stored as null. A null Tag means -Tools Update always checks out the
    latest remote HEAD commit.

  -Tools List
    Reads Project.json and writes the ProjectTools array as indented JSON.

  -Tools Remove
    Validates RepositoryName. If the tool is not listed in Project.json, it only reports that the tool does not
    exist in the list.

    If the tool exists, the script runs -Tools Update first, removes the matching ProjectTools entry from
    Project.json, removes the Git submodule under Tools/<RepositoryName>, stages and commits the changes,
    then runs:
      .\ProjectManager.ps1 -Tools Update
"@

    Write-Host $usage
}

function Resolve-FullPath {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $resolved = Resolve-Path -LiteralPath $Path -ErrorAction SilentlyContinue
    if ($resolved) {
        return $resolved.Path
    }

    return [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $Path))
}

function Save-ProjectConfiguration {
    param(
        [Parameter(Mandatory = $true)]
        [object]$Configuration,

        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $json = $Configuration | ConvertTo-Json -Depth 10
    Set-Content -LiteralPath $Path -Value $json -Encoding UTF8
}

function Initialize-ProjectConfiguration {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $resolvedPath = Resolve-FullPath -Path $Path
    $configurationChanged = $false

    if (-not (Test-Path -LiteralPath $resolvedPath -PathType Leaf)) {
        $configuration = [PSCustomObject]@{
            Project = $null
            ProjectTools = @()
        }
        Save-ProjectConfiguration -Configuration $configuration -Path $resolvedPath
        return $true
    }

    $json = Get-Content -LiteralPath $resolvedPath -Raw
    if ([string]::IsNullOrWhiteSpace($json)) {
        $configuration = [PSCustomObject]@{
            Project = $null
            ProjectTools = @()
        }
        Save-ProjectConfiguration -Configuration $configuration -Path $resolvedPath
        return $true
    }

    $configuration = ConvertFrom-Json -InputObject $json -ErrorAction Stop
    $properties = @($configuration.PSObject.Properties.Name)

    if ($properties -notcontains "Project") {
        $configuration | Add-Member -NotePropertyName "Project" -NotePropertyValue $null
        $configurationChanged = $true
    }

    if ($properties -notcontains "ProjectTools" -or $null -eq $configuration.ProjectTools) {
        if ($properties -contains "ProjectTools") {
            $configuration.ProjectTools = @()
        }
        else {
            $configuration | Add-Member -NotePropertyName "ProjectTools" -NotePropertyValue @()
        }
        $configurationChanged = $true
    }
    elseif ($configuration.ProjectTools -isnot [array]) {
        $configuration.ProjectTools = @($configuration.ProjectTools)
        $configurationChanged = $true
    }

    if ($configurationChanged) {
        Save-ProjectConfiguration -Configuration $configuration -Path $resolvedPath
    }

    return $configurationChanged
}

function Get-DefaultProjectEditor {
    if ([System.Environment]::OSVersion.Platform -eq [System.PlatformID]::Win32NT) {
        return "notepad"
    }

    return "vi"
}

function Edit-ProjectConfiguration {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [string]$Editor
    )

    $resolvedPath = Resolve-FullPath -Path $Path
    $selectedEditor = $Editor

    if ([string]::IsNullOrWhiteSpace($selectedEditor)) {
        $selectedEditor = Get-DefaultProjectEditor
    }

    Write-Host "Opening $Path with $selectedEditor..."
    & $selectedEditor $resolvedPath
}

function Show-ProjectConfiguration {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $configuration = Get-ProjectConfiguration -Path $Path
    $configuration | ConvertTo-Json -Depth 10
}

function Invoke-Git {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $output = & git @Arguments 2>&1
    $exitCode = $LASTEXITCODE

    foreach ($line in @($output)) {
        Write-Host ([string]$line)
    }

    if ($exitCode -ne 0) {
        throw "Git command failed: git $($Arguments -join ' ')"
    }
}

function Invoke-GitOutput {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $output = & git @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "Git command failed: git $($Arguments -join ' ')"
    }

    return $output
}

function Test-StagedChanges {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    & git diff --cached --quiet -- $Path
    if ($LASTEXITCODE -eq 1) {
        return $true
    }

    if ($LASTEXITCODE -eq 0) {
        return $false
    }

    throw "Git command failed: git diff --cached --quiet -- $Path"
}

function Test-GitPathTracked {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    & git ls-files --error-unmatch -- $Path *> $null
    if ($LASTEXITCODE -eq 0) {
        return $true
    }

    if ($LASTEXITCODE -eq 1) {
        return $false
    }

    throw "Git command failed: git ls-files --error-unmatch -- $Path"
}

function Update-ProjectManagerScript {
    $scriptPath = Resolve-FullPath -Path $PSCommandPath
    if (-not (Test-Path -LiteralPath $scriptPath -PathType Leaf)) {
        throw "Current script file was not found: $scriptPath"
    }

    $temporaryPath = [System.IO.Path]::GetTempFileName()
    try {
        Invoke-WebRequest -Uri $ProjectManagerScriptUrl -OutFile $temporaryPath -UseBasicParsing

        $currentContent = Get-Content -LiteralPath $scriptPath -Raw
        $downloadedContent = Get-Content -LiteralPath $temporaryPath -Raw

        if ($currentContent -eq $downloadedContent) {
            Write-Host "ProjectManager.ps1 is already up to date."
            return
        }

        Copy-Item -LiteralPath $temporaryPath -Destination $scriptPath -Force
        Invoke-Git -Arguments @("add", $scriptPath)
        Invoke-Git -Arguments @("commit", "-m", "Update ProjectManager.ps1")

        Write-Host "ProjectManager.ps1 was updated."
    }
    finally {
        if (Test-Path -LiteralPath $temporaryPath -PathType Leaf) {
            Remove-Item -LiteralPath $temporaryPath -Force
        }
    }
}

function Show-GitSubModules {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $configuration = Get-ProjectConfiguration -Path $Path
    $tools = @($configuration.ProjectTools)

    if ($tools.Count -eq 0) {
        Write-Output "[]"
        return
    }

    $submodules = @()
    foreach ($tool in $tools) {
        $repositoryName = [string]$tool.RepositoryName
        if ([string]::IsNullOrWhiteSpace($repositoryName)) {
            throw "Every ProjectTools entry must define RepositoryName."
        }

        $submoduleStatus = Invoke-GitOutput -Arguments @("submodule", "status", "--", "Tools/$repositoryName")
        foreach ($line in @($submoduleStatus)) {
            $submodules += [string]$line
        }
    }

    if ($submodules.Count -eq 0) {
        Write-Output "[]"
        return
    }

    Write-Output (ConvertTo-Json -InputObject $submodules -Depth 10)
}

function Test-RepositoryName {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    if ([string]::IsNullOrWhiteSpace($Name) -or $Name -eq "." -or $Name -eq "..") {
        return $false
    }

    if ($Name.EndsWith(" ") -or $Name.EndsWith(".")) {
        return $false
    }

    $portableInvalidCharacters = @("/", "\", ":", "*", "?", '"', "<", ">", "|")
    foreach ($character in $portableInvalidCharacters) {
        if ($Name.Contains($character)) {
            return $false
        }
    }

    foreach ($character in $Name.ToCharArray()) {
        if ([int][char]$character -lt 32) {
            return $false
        }
    }

    $reservedNames = @("CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9")
    if ($reservedNames -contains $Name.ToUpperInvariant()) {
        return $false
    }

    return $true
}

function Assert-RepositoryName {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    if (-not (Test-RepositoryName -Name $Name)) {
        throw "RepositoryName must be a valid cross-platform directory name: $Name"
    }
}

function Get-RemoteHeadCommit {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Url
    )

    $remoteHead = @(Invoke-GitOutput -Arguments @("ls-remote", $Url, "HEAD"))
    if ($remoteHead.Count -eq 0 -or [string]::IsNullOrWhiteSpace([string]$remoteHead[0])) {
        throw "Repository URL was not found or does not expose HEAD: $Url"
    }

    $parts = ([string]$remoteHead[0]) -split "\s+"
    return $parts[0]
}

function Resolve-ProjectToolTag {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Url,

        [AllowEmptyString()]
        [string]$Tag
    )

    if ([string]::IsNullOrWhiteSpace($Tag)) {
        Get-RemoteHeadCommit -Url $Url | Out-Null
        return $null
    }

    if ($Tag -match "^[0-9a-fA-F]{7,40}$") {
        Get-RemoteHeadCommit -Url $Url | Out-Null
        return $Tag
    }

    $remoteTag = @(Invoke-GitOutput -Arguments @("ls-remote", "--tags", $Url, "refs/tags/$Tag"))
    if ($remoteTag.Count -eq 0) {
        $peeledRemoteTag = @(Invoke-GitOutput -Arguments @("ls-remote", "--tags", $Url, "refs/tags/$Tag^{}"))
        if ($peeledRemoteTag.Count -eq 0) {
            throw "Tag was not found in remote repository: $Tag"
        }
    }

    return $Tag
}

function Test-GitCommitReference {
    param(
        [Parameter(Mandatory = $true)]
        [string]$RepositoryPath,

        [Parameter(Mandatory = $true)]
        [string]$Reference
    )

    & git -C $RepositoryPath cat-file -e "$Reference^{commit}" 2>$null
    return $LASTEXITCODE -eq 0
}

function Resolve-SubmoduleCheckoutReference {
    param(
        [Parameter(Mandatory = $true)]
        [string]$RepositoryPath,

        [AllowEmptyString()]
        [string]$Tag
    )

    if ([string]::IsNullOrWhiteSpace($Tag)) {
        & git -C $RepositoryPath remote set-head origin --auto 2>$null | Out-Null
        $remoteHead = & git -C $RepositoryPath rev-parse --verify "refs/remotes/origin/HEAD^{commit}" 2>$null
        if ($LASTEXITCODE -eq 0 -and -not [string]::IsNullOrWhiteSpace([string]$remoteHead)) {
            return ([string]$remoteHead).Trim()
        }

        $fetchHead = & git -C $RepositoryPath rev-parse --verify "FETCH_HEAD^{commit}" 2>$null
        if ($LASTEXITCODE -eq 0 -and -not [string]::IsNullOrWhiteSpace([string]$fetchHead)) {
            return ([string]$fetchHead).Trim()
        }

        throw "Could not resolve latest remote HEAD for $RepositoryPath."
    }

    if ($Tag -match "^[0-9a-fA-F]{7,40}$" -and (Test-GitCommitReference -RepositoryPath $RepositoryPath -Reference $Tag)) {
        return $Tag
    }

    $tagReference = "refs/tags/$Tag"
    if (Test-GitCommitReference -RepositoryPath $RepositoryPath -Reference $tagReference) {
        return $tagReference
    }

    throw "Could not resolve Tag as a commit hash or tag in ${RepositoryPath}: $Tag"
}

function Get-ProjectConfiguration {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $resolvedPath = Resolve-FullPath -Path $Path
    if (-not (Test-Path -LiteralPath $resolvedPath -PathType Leaf)) {
        throw "Project configuration file was not found: $resolvedPath"
    }

    $json = Get-Content -LiteralPath $resolvedPath -Raw
    if ([string]::IsNullOrWhiteSpace($json)) {
        throw "Project configuration file is empty: $resolvedPath"
    }

    $configuration = ConvertFrom-Json -InputObject $json -ErrorAction Stop
    if ($null -eq $configuration.ProjectTools) {
        throw "Project configuration must contain a ProjectTools array."
    }

    return $configuration
}

function Test-ProjectToolListed {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$RepositoryName
    )

    Assert-RepositoryName -Name $RepositoryName

    $configuration = Get-ProjectConfiguration -Path $Path
    $tool = @($configuration.ProjectTools) | Where-Object { [string]$_.RepositoryName -eq $RepositoryName } | Select-Object -First 1
    return $null -ne $tool
}

function Add-ProjectTool {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$RepositoryName,

        [Parameter(Mandatory = $true)]
        [string]$RepositoryUrl,

        [AllowEmptyString()]
        [string]$Tag
    )

    Assert-RepositoryName -Name $RepositoryName

    if ([string]::IsNullOrWhiteSpace($RepositoryUrl)) {
        throw "RepositoryUrl is required."
    }

    $resolvedPath = Resolve-FullPath -Path $Path
    $configuration = Get-ProjectConfiguration -Path $resolvedPath
    $tools = @($configuration.ProjectTools)
    $existingTool = $tools | Where-Object { [string]$_.RepositoryName -eq $RepositoryName } | Select-Object -First 1

    if ($existingTool) {
        throw "ProjectTools already contains $RepositoryName. Use -Tools Update -RepositoryName $RepositoryName to modify it."
    }

    $submodulePath = "Tools/$RepositoryName"
    if (Test-SubmoduleRegistered -Path $submodulePath) {
        throw "Git submodules already contain $submodulePath. Remove it before adding it to ProjectTools."
    }

    $resolvedTag = Resolve-ProjectToolTag -Url $RepositoryUrl -Tag $Tag
    $tools += [PSCustomObject]@{
        RepositoryUrl = $RepositoryUrl
        RepositoryName = $RepositoryName
        Tag = $resolvedTag
    }
    $configuration.ProjectTools = @($tools)

    Save-ProjectConfiguration -Configuration $configuration -Path $resolvedPath
    Invoke-Git -Arguments @("add", $resolvedPath)
    $targetName = if ($null -eq $resolvedTag) { "latest" } else { $resolvedTag }
    if (Test-StagedChanges -Path $resolvedPath) {
        Invoke-Git -Arguments @("commit", "-m", "Add $RepositoryName $targetName")
    }
    else {
        Write-Host "Project.json already contains $RepositoryName $targetName."
    }

    Use-ProjectTools -Path $resolvedPath
}

function Update-ProjectTool {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$RepositoryName,

        [string]$RepositoryUrl,

        [AllowEmptyString()]
        [string]$Tag,

        [Parameter(Mandatory = $true)]
        [bool]$RepositoryUrlWasProvided,

        [Parameter(Mandatory = $true)]
        [bool]$TagWasProvided
    )

    Assert-RepositoryName -Name $RepositoryName

    $resolvedPath = Resolve-FullPath -Path $Path
    $configuration = Get-ProjectConfiguration -Path $resolvedPath
    $tools = @($configuration.ProjectTools)
    $existingTool = $tools | Where-Object { [string]$_.RepositoryName -eq $RepositoryName } | Select-Object -First 1

    if (-not $existingTool) {
        Write-Host "ProjectTools does not contain $RepositoryName."
        return
    }

    $effectiveRepositoryUrl = [string]$existingTool.RepositoryUrl
    if ($RepositoryUrlWasProvided) {
        if ([string]::IsNullOrWhiteSpace($RepositoryUrl)) {
            throw "RepositoryUrl cannot be empty when provided."
        }

        Get-RemoteHeadCommit -Url $RepositoryUrl | Out-Null
        $effectiveRepositoryUrl = $RepositoryUrl
        $existingTool.RepositoryUrl = $RepositoryUrl
    }

    if ($TagWasProvided) {
        $existingTool.Tag = Resolve-ProjectToolTag -Url $effectiveRepositoryUrl -Tag $Tag
    }

    Save-ProjectConfiguration -Configuration $configuration -Path $resolvedPath
    Invoke-Git -Arguments @("add", $resolvedPath)
    $targetName = if ($TagWasProvided) {
        if ($null -eq $existingTool.Tag) { "latest" } else { [string]$existingTool.Tag }
    }
    else {
        "metadata"
    }

    if (Test-StagedChanges -Path $resolvedPath) {
        Invoke-Git -Arguments @("commit", "-m", "Update $RepositoryName $targetName")
    }
    else {
        Write-Host "Project.json already contains the requested values for $RepositoryName."
    }

    Use-ProjectTools -Path $resolvedPath
}

function Show-ProjectTools {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $configuration = Get-ProjectConfiguration -Path $Path
    $tools = @($configuration.ProjectTools)

    if ($tools.Count -eq 0) {
        Write-Output "[]"
        return
    }

    Write-Output (ConvertTo-Json -InputObject $tools -Depth 10)
}

function Remove-ProjectTool {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$RepositoryName
    )

    Assert-RepositoryName -Name $RepositoryName

    $resolvedPath = Resolve-FullPath -Path $Path
    $configuration = Get-ProjectConfiguration -Path $resolvedPath
    $tools = @($configuration.ProjectTools)
    $existingTool = $tools | Where-Object { [string]$_.RepositoryName -eq $RepositoryName } | Select-Object -First 1

    if (-not $existingTool) {
        Write-Host "ProjectTools does not contain $RepositoryName."
        return
    }

    $configuration.ProjectTools = @($tools | Where-Object { [string]$_.RepositoryName -ne $RepositoryName })
    Save-ProjectConfiguration -Configuration $configuration -Path $resolvedPath

    $submodulePath = "Tools/$RepositoryName"
    if (Test-SubmoduleRegistered -Path $submodulePath) {
        Invoke-Git -Arguments @("submodule", "deinit", "-f", "--", $submodulePath)
    }

    if (Test-GitPathTracked -Path $submodulePath) {
        Invoke-Git -Arguments @("rm", "-f", $submodulePath)
    }
    elseif (Test-Path -LiteralPath $submodulePath) {
        Remove-Item -LiteralPath $submodulePath -Recurse -Force
    }

    $submoduleGitDirectory = Join-Path (Join-Path (Join-Path (Get-Location) ".git") "modules") $submodulePath
    if (Test-Path -LiteralPath $submoduleGitDirectory) {
        Remove-Item -LiteralPath $submoduleGitDirectory -Recurse -Force
    }

    Invoke-Git -Arguments @("add", $resolvedPath)
    $gitModulesPath = Resolve-FullPath -Path ".gitmodules"
    if (Test-Path -LiteralPath $gitModulesPath -PathType Leaf) {
        Invoke-Git -Arguments @("add", $gitModulesPath)
    }

    if ((Test-StagedChanges -Path $resolvedPath) -or (Test-StagedChanges -Path ".gitmodules") -or (Test-StagedChanges -Path $submodulePath)) {
        Invoke-Git -Arguments @("commit", "-m", "Remove $RepositoryName")
    }
    else {
        Write-Host "No staged changes were produced while removing $RepositoryName."
    }

    Use-ProjectTools -Path $resolvedPath
}

function Test-SubmoduleRegistered {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $gitModulesPath = Join-Path (Get-Location) ".gitmodules"
    if (-not (Test-Path -LiteralPath $gitModulesPath -PathType Leaf)) {
        return $false
    }

    $gitModules = Get-Content -LiteralPath $gitModulesPath -Raw
    return $gitModules -match [Regex]::Escape("path = $Path")
}

function Get-RegisteredToolSubmodules {
    $gitModulesPath = Join-Path (Get-Location) ".gitmodules"
    if (-not (Test-Path -LiteralPath $gitModulesPath -PathType Leaf)) {
        return @()
    }

    $entries = @()
    $registeredPaths = & git config --file ".gitmodules" --get-regexp "submodule\..*\.path"
    if ($LASTEXITCODE -eq 1) {
        return @()
    }

    if ($LASTEXITCODE -ne 0) {
        throw "Git command failed: git config --file .gitmodules --get-regexp submodule\..*\.path"
    }

    foreach ($entry in @($registeredPaths)) {
        $parts = [string]$entry -split "\s+", 2
        if ($parts.Count -ne 2) {
            continue
        }

        $key = $parts[0]
        $path = $parts[1]
        if (-not $path.StartsWith("Tools/")) {
            continue
        }

        $section = $key -replace "\.path$", ""
        $entries += [PSCustomObject]@{
            Path = $path
            Section = $section
        }
    }

    return $entries
}

function Get-CurrentSubmoduleHead {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $head = & git -C $Path rev-parse HEAD 2>$null
    if ($LASTEXITCODE -ne 0) {
        return $null
    }

    return ($head | Out-String).Trim()
}

function Remove-UnlistedToolDirectories {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ToolsDirectory,

        [Parameter(Mandatory = $true)]
        [AllowEmptyCollection()]
        [string[]]$AllowedRepositoryNames
    )

    $allowedNames = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    foreach ($name in $AllowedRepositoryNames) {
        if (-not [string]::IsNullOrWhiteSpace($name)) {
            [void]$allowedNames.Add($name)
        }
    }

    $registeredByPath = @{}
    foreach ($registeredSubmodule in @(Get-RegisteredToolSubmodules)) {
        $registeredByPath[$registeredSubmodule.Path] = $registeredSubmodule
    }

    $candidatePaths = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    if (Test-Path -LiteralPath $ToolsDirectory -PathType Container) {
        foreach ($directory in Get-ChildItem -LiteralPath $ToolsDirectory -Directory) {
            [void]$candidatePaths.Add("Tools/$($directory.Name)")
        }
    }

    foreach ($registeredPath in $registeredByPath.Keys) {
        [void]$candidatePaths.Add($registeredPath)
    }

    $removedAny = $false
    foreach ($submodulePath in $candidatePaths) {
        $repositoryName = Split-Path -Path $submodulePath -Leaf
        if ($allowedNames.Contains($repositoryName)) {
            continue
        }

        Write-Host "Removing unmanaged tool submodule $submodulePath..."

        if (Test-SubmoduleRegistered -Path $submodulePath) {
            $deinitOutput = & git submodule deinit -f -- $submodulePath 2>$null
            foreach ($line in @($deinitOutput)) {
                Write-Host ([string]$line)
            }

            if ($LASTEXITCODE -ne 0) {
                Write-Host "Submodule $submodulePath was not active in the Git index."
            }
        }

        $pathTracked = Test-GitPathTracked -Path $submodulePath
        if ($pathTracked) {
            Invoke-Git -Arguments @("rm", "-f", $submodulePath)
        }
        elseif (Test-Path -LiteralPath $submodulePath) {
            Remove-Item -LiteralPath $submodulePath -Recurse -Force
        }

        if ((-not $pathTracked) -and $registeredByPath.ContainsKey($submodulePath)) {
            Invoke-Git -Arguments @("config", "--file", ".gitmodules", "--remove-section", $registeredByPath[$submodulePath].Section)
        }

        $submoduleGitDirectory = Join-Path (Join-Path (Join-Path (Get-Location) ".git") "modules") $submodulePath
        if (Test-Path -LiteralPath $submoduleGitDirectory) {
            Remove-Item -LiteralPath $submoduleGitDirectory -Recurse -Force
        }

        $removedAny = $true
    }

    if (-not $removedAny) {
        return
    }

    $gitModulesPath = Resolve-FullPath -Path ".gitmodules"
    if (Test-Path -LiteralPath $gitModulesPath -PathType Leaf) {
        $gitModulesContent = Get-Content -LiteralPath $gitModulesPath -Raw
        if ([string]::IsNullOrWhiteSpace($gitModulesContent)) {
            if (Test-GitPathTracked -Path ".gitmodules") {
                Invoke-Git -Arguments @("rm", "-f", ".gitmodules")
            }
            else {
                Remove-Item -LiteralPath $gitModulesPath -Force
            }
        }
        else {
            Invoke-Git -Arguments @("add", $gitModulesPath)
        }
    }

    if ((Test-StagedChanges -Path "Tools") -or (Test-StagedChanges -Path ".gitmodules")) {
        Invoke-Git -Arguments @("commit", "-m", "Remove unmanaged tools")
    }
}

function Ensure-ProjectTool {
    param(
        [Parameter(Mandatory = $true)]
        [object]$Tool
    )

    $repositoryUrl = [string]$Tool.RepositoryUrl
    $repositoryName = [string]$Tool.RepositoryName
    $tag = [string]$Tool.Tag

    if ([string]::IsNullOrWhiteSpace($repositoryUrl)) {
        throw "Every ProjectTools entry must define RepositoryUrl."
    }

    if ([string]::IsNullOrWhiteSpace($repositoryName)) {
        throw "Every ProjectTools entry must define RepositoryName."
    }

    $submodulePath = "Tools/$repositoryName"
    $submoduleAdded = $false

    if (-not (Test-SubmoduleRegistered -Path $submodulePath)) {
        Write-Host "Adding submodule $repositoryName from $repositoryUrl..."
        Invoke-Git -Arguments @("submodule", "add", $repositoryUrl, $submodulePath)
        $submoduleAdded = $true
    }
    else {
        Write-Host "Submodule $repositoryName already exists."
    }

    Invoke-Git -Arguments @("submodule", "sync", "--recursive", $submodulePath)
    Invoke-Git -Arguments @("submodule", "update", "--init", "--recursive", $submodulePath)

    $headBefore = Get-CurrentSubmoduleHead -Path $submodulePath

    Invoke-Git -Arguments @("-C", $submodulePath, "fetch", "origin", "--tags")
    $checkoutReference = Resolve-SubmoduleCheckoutReference -RepositoryPath $submodulePath -Tag $tag
    Invoke-Git -Arguments @("-C", $submodulePath, "checkout", $checkoutReference)

    $headAfter = Get-CurrentSubmoduleHead -Path $submodulePath
    if ($submoduleAdded -or $headBefore -ne $headAfter) {
        $targetName = if ([string]::IsNullOrWhiteSpace($tag)) { "latest" } else { $tag }
        Write-Host "Submodule $repositoryName changed to $targetName."
        if ($submoduleAdded) {
            Invoke-Git -Arguments @("add", ".gitmodules")
        }
        Invoke-Git -Arguments @("add", $submodulePath)
        Invoke-Git -Arguments @("commit", "-m", "Use $repositoryName $targetName")
    }
    else {
        $targetName = if ([string]::IsNullOrWhiteSpace($tag)) { "latest" } else { $tag }
        Write-Host "Submodule $repositoryName is already at $targetName."
    }
}

function Use-ProjectTools {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $configuration = Get-ProjectConfiguration -Path $Path
    $toolsDirectory = Join-Path (Get-Location) "Tools"

    if (-not (Test-Path -LiteralPath $toolsDirectory -PathType Container)) {
        New-Item -ItemType Directory -Path $toolsDirectory | Out-Null
    }

    $allowedRepositoryNames = @($configuration.ProjectTools | ForEach-Object { [string]$_.RepositoryName })
    Remove-UnlistedToolDirectories -ToolsDirectory $toolsDirectory -AllowedRepositoryNames $allowedRepositoryNames

    foreach ($tool in @($configuration.ProjectTools)) {
        Ensure-ProjectTool -Tool $tool
    }
}

function Get-ProjectCommandValue {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $configuration = Get-ProjectConfiguration -Path $Path
    $project = $configuration.Project

    if ($null -eq $project) {
        Write-Host "Project is null."
        Write-Output "null"
        return
    }

    $projectValue = [string]$project
    if ([string]::IsNullOrWhiteSpace($projectValue)) {
        Write-Host "Project is empty."
        Write-Output (ConvertTo-Json -InputObject $projectValue)
        return
    }

    Write-Output (ConvertTo-Json -InputObject $projectValue)
}

function Set-ProjectCommandValue {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [AllowNull()]
        [AllowEmptyString()]
        [object]$Value
    )

    $resolvedPath = Resolve-FullPath -Path $Path
    $configuration = Get-ProjectConfiguration -Path $resolvedPath

    if ($null -eq $Value) {
        $configuration.Project = $null
    }
    else {
        $configuration.Project = [string]$Value
    }

    Save-ProjectConfiguration -Configuration $configuration -Path $resolvedPath
    Get-ProjectCommandValue -Path $resolvedPath
}

if ($Help -or (-not ($PSBoundParameters.ContainsKey("Tools") -or $PSBoundParameters.ContainsKey("Project") -or $PSBoundParameters.ContainsKey("SelfUpdate") -or $PSBoundParameters.ContainsKey("SubModules") -or $PSBoundParameters.ContainsKey("Init") -or $PSBoundParameters.ContainsKey("Edit") -or $PSBoundParameters.ContainsKey("List") -or $PSBoundParameters.ContainsKey("Version")))) {
    Show-Usage
    return
}

if ($Version) {
    Write-Output (ConvertTo-Json -InputObject $ProjectManagerVersion)
    return
}

$projectConfigurationWasRepaired = Initialize-ProjectConfiguration -Path $ProjectConfigurationPath

if ($projectConfigurationWasRepaired) {
    Use-ProjectTools -Path $ProjectConfigurationPath
}

if ($Init) {
    return
}

if ($List) {
    Show-ProjectConfiguration -Path $ProjectConfigurationPath
    return
}

if ($Edit) {
    Edit-ProjectConfiguration -Path $ProjectConfigurationPath -Editor $Editor
    return
}

if ($SubModules) {
    if (-not $projectConfigurationWasRepaired) {
        Use-ProjectTools -Path $ProjectConfigurationPath
    }

    Show-GitSubModules -Path $ProjectConfigurationPath
    return
}

if ($Tools -eq "Update") {
    if ($PSBoundParameters.ContainsKey("RepositoryName")) {
        if ((Test-ProjectToolListed -Path $ProjectConfigurationPath -RepositoryName $RepositoryName) -and (-not $projectConfigurationWasRepaired)) {
            Use-ProjectTools -Path $ProjectConfigurationPath
        }

        Update-ProjectTool `
            -Path $ProjectConfigurationPath `
            -RepositoryName $RepositoryName `
            -RepositoryUrl $RepositoryUrl `
            -Tag $Tag `
            -RepositoryUrlWasProvided $PSBoundParameters.ContainsKey("RepositoryUrl") `
            -TagWasProvided $PSBoundParameters.ContainsKey("Tag")
        return
    }

    if ($PSBoundParameters.ContainsKey("RepositoryUrl") -or $PSBoundParameters.ContainsKey("Tag")) {
        throw "RepositoryName is required when updating RepositoryUrl or Tag."
    }

    Use-ProjectTools -Path $ProjectConfigurationPath
    return
}

if ($Tools -eq "Add") {
    if (-not $projectConfigurationWasRepaired) {
        Use-ProjectTools -Path $ProjectConfigurationPath
    }

    Add-ProjectTool -Path $ProjectConfigurationPath -RepositoryName $RepositoryName -RepositoryUrl $RepositoryUrl -Tag $Tag
    return
}

if ($Tools -eq "List") {
    Show-ProjectTools -Path $ProjectConfigurationPath
    return
}

if ($Tools -eq "Remove") {
    if ((Test-ProjectToolListed -Path $ProjectConfigurationPath -RepositoryName $RepositoryName) -and (-not $projectConfigurationWasRepaired)) {
        Use-ProjectTools -Path $ProjectConfigurationPath
    }

    Remove-ProjectTool -Path $ProjectConfigurationPath -RepositoryName $RepositoryName
    return
}

if ($Project) {
    if ($Project -eq "Get") {
        Get-ProjectCommandValue -Path $ProjectConfigurationPath
        return
    }

    if ($Project -eq "Set") {
        if (-not $PSBoundParameters.ContainsKey("Name")) {
            throw "Name is required when using -Project Set."
        }

        Set-ProjectCommandValue -Path $ProjectConfigurationPath -Value $Name
        return
    }

    return
}

if ($SelfUpdate) {
    Update-ProjectManagerScript
    return
}
