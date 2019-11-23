name := "SMORK"
organization := "com.iresium.ml"
version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.1"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.3.1"




//libraryDependencies += "org.scala-lang" %% "scala-compiler" % "2.13.1"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"