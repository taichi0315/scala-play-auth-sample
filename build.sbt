name := """scala-play-auth-sample"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

libraryDependencies ++= Seq(
  "com.typesafe.play"      %% "play-slick" % "5.0.0",
  "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
  "mysql"                   % "mysql-connector-java"  % "5.1.48",
  "org.typelevel"          %% "cats-core" % "2.0.0",
  "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  caffeine,
  guice
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
