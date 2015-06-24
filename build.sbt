name := "GitHubRepoMiner"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "0.1.1",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.h2database" % "h2" % "1.3.166",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3"
)