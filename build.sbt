scalaVersion := "2.13.12"

name := "type-sum-codec"


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

