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

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

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
