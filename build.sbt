name := """nxt-messageviewer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  //javaJdbc,
  javaEbean,
  cache,
  javaWs,
  //"org.json" % "json" % "20140107",
  "com.h2database" % "h2" % "1.4.178",
  "org.springframework" % "spring-web" % "4.0.5.RELEASE",
  "com.google.code.gson" % "gson" % "2.2.4"
)

