param ([string]$RepoRoot = "D:\Users\genlong.wang\.m2\repository")
$deps = @(
@{ Group = "org\springframework\boot"; Artifact = "spring-boot-starter-web"; Version = "2.7.18" },
@{ Group = "com\baomidou"; Artifact = "mybatis-plus-boot-starter"; Version = "3.5.3" },
@{ Group = "com\alibaba"; Artifact = "druid-spring-boot-starter"; Version = "1.2.23" },
@{ Group = "com\mysql"; Artifact = "mysql-connector-j"; Version = "8.4.0" }
)
foreach ($dep in $deps) {
  $jarPath = Join-Path $RepoRoot "$($dep.Group)\$($dep.Artifact)\$($dep.Version)\$($dep.Artifact)-$($dep.Version).jar"
  if (Test-Path $jarPath) { Write-Output "FOUND $jarPath" } else { Write-Output "MISSING $jarPath" }
}
