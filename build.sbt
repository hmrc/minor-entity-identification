import uk.gov.hmrc.DefaultBuildSettings

val appName = "minor-entity-identification"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.6"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-Xfatal-warnings",
      "-Wconf:msg=Flag.*repeatedly:s",
      "-Wconf:src=routes/.*&msg=unused:silent"
    )
  )
  .settings(CodeCoverageSettings.settings *)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings(),
    scalacOptions ++= Seq("-Wconf:msg=Flag.*repeatedly:s")
  )