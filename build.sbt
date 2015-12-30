// lazy val dottyVersion = dottyLatestNightlyBuild.get
lazy val dottyVersion = "0.11.0-bin-SNAPSHOT"

lazy val root = project
  .in(file("."))
  .enablePlugins(JmhPlugin)
  .settings(
    name := "dotty-benchmark",
    scalaVersion := dottyVersion
  )
