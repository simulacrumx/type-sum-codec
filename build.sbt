import xerial.sbt.Sonatype._

scalaVersion := "2.13.12"

name := "type-sum-codec"

inThisBuild(
  List(
    organization := "io.github.simulacrumx",
    homepage := Some(url("https://github.com/simulacrumx/type-sum-codec")),
    // Alternatively License.Apache2 see https://github.com/sbt/librarymanagement/blob/develop/core/src/main/scala/sbt/librarymanagement/License.scala
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "simulacrumx",
        "Kirill Sitnikov",
        "kirillxsitnikov@gmail.com",
        url("https://github.com/simulacrumx")
      )
    )
  )
)

val actualVersion =
  (if (releaseVersion == null) "0.1.0-SNAPSHOT" else releaseVersion)

ThisBuild / organization := "io.github.simulacrumx"
ThisBuild / version := actualVersion
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / developers := List(
  Developer(
    id = "kirillxsitnikov",
    name = "Kirill Sitnikov",
    email = "kirillxsitnikov@gmail.com",
    url = url("https://github.com/simulacrumx")
  )
)

ThisBuild / licenses := Seq(
  "APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
)
ThisBuild / homepage := Some(
  url("https://github.com/simulacrumx/type-sum-codec")
)

ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

ThisBuild / credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "s01.oss.sonatype.org",
  System.getenv("SONATYPE_USERID"),
  System.getenv("SONATYPE_TOKEN")
)

ThisBuild / publishMavenStyle := true

scalacOptions ++= Seq(
  "-Ymacro-annotations"
)

val circeVersion = "0.14.1"

val circeDependencies = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-literal",
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

licenses := Seq(
  "APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
)
