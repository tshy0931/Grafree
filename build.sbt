name := "grafree"

version := "0.1"

scalaVersion := "2.12.4"

lazy val catsV = "1.0.0-MF"
lazy val monocleV = "1.4.0"

libraryDependencies := Seq(
  "org.typelevel" %% "cats-core" % catsV,
  "org.typelevel" %% "cats-macros" % catsV,
  "com.chuusai" %% "shapeless" % "2.3.2",
  "com.github.julien-truffaut" %% "monocle-core" % monocleV,
  "com.github.julien-truffaut" %% "monocle-macro" % monocleV,
  "com.github.julien-truffaut" %% "monocle-generic" % monocleV,
  "org.scalatest" %% "scalatest" % "3.0.4" % Test

)