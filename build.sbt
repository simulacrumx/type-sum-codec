scalaVersion := "2.13.12"

name := "type-sum-codec"

val releaseVersion: String = System.getenv("RELEASE_VERSION")

ThisBuild / organization := "com.github.simulacrumx"
ThisBuild / version := (if (releaseVersion == null) "0.1.0-SNAPSHOT" else releaseVersion)
ThisBuild / versionScheme := Some("early-semver")


scalacOptions ++= Seq(
  "-Ymacro-annotations",
)

val circeVersion = "0.14.1"

val circeDependencies = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion) 

val scalaTestDeps = Seq(
  "org.scalactic" %% "scalactic" % "3.2.18",
  "org.scalatest" %% "scalatest" % "3.2.18" % "test"
)

libraryDependencies ++= 
  circeDependencies ++
  scalaTestDeps ++
  Seq(
   "org.scala-lang" % "scala-reflect" % scalaVersion.value
)


publishTo := Some("GitHub simulacrumx Apache Maven Packages" at "https://maven.pkg.github.com/simulacrumx/type-sum-codec")
publishMavenStyle := true
credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "simulacrumx",
  System.getenv("GITHUB_TOKEN")
)
