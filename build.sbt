name := "nxt-messageviewer"

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  //javaJdbc,
  javaEbean,
  cache,
  javaWs,
  //"org.json" % "json" % "20140107",
  "com.h2database" % "h2" % "1.4.178",
  "org.springframework" % "spring-web" % "4.0.5.RELEASE",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"
)

