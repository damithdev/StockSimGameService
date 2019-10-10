enablePlugins(JavaServerAppPackaging)

name := "stockmarket"

version := "0.1"

scalaVersion := "2.12.8"

organization := "com.stockmarket"

libraryDependencies ++= {
  val akkaVersion = "2.5.19"
  val akkaHttp = "10.1.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttp,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.play" %% "play-ws-standalone-json" % "1.1.8",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
//    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "de.heikoseeberger" %% "akka-http-play-json" % "1.17.0",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "commons-codec" % "commons-codec" % "1.9",
    "com.typesafe.slick" %% "slick" % "3.3.1",
    "org.slf4j" % "slf4j-nop" % "1.7.10",
    "com.h2database" % "h2" % "1.4.187",
    "com.google.code.gson" % "gson" % "2.3.1"


  )
}

libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.4.1"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.3.0"


enablePlugins(JavaServerAppPackaging,JDebPackaging,JDKPackagerPlugin)



//val circeVersion = "0.11.1"

//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)