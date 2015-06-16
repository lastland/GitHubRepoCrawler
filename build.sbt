name := "GitHubRepoMiner"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "0.1.1",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.h2database" % "h2" % "1.3.166",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)