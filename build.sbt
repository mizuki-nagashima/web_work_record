name := """web_work_record"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "javax.persistence" % "persistence-api" % "1.0.2",
  "org.avaje.ebean" % "ebean" % "8.1.1",
  "com.h2database"       %  "h2"                       % "1.4.+",
  "mysql"                %  "mysql-connector-java"     % "5.1.+",
  "it.innove" % "play2-pdf" % "1.5.1",
  javaJpa,
  javaJdbc,
  cache,
  javaWs
)
