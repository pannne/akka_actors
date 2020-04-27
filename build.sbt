name := "akka_actors"

version := "0.1"

scalaVersion := "2.12.7"

lazy val akkaVersion = "2.6.4"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.apache.commons" % "commons-imaging" % "1.0-alpha1",
  "io.humble" % "humble-video-all" % "0.3.0",
  "commons-io" % "commons-io" % "2.6"
)