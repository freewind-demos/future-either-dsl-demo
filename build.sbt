name := "Future Either DSL Demo"

version := "0.1"

organization := "org.my"

scalaVersion := "2.12.4"

sbtVersion := "1.0.4"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)