package com.aroniasoft

import akka.actor.{ActorSystem, Props}

object Main extends App {

  val srcDirPath: String = "D:\\sve_slike\\"
  val destDirPath: String = "e:\\media\\"
  val extensionFilterList: List[String] = List("3gp")

  val actorSystem = ActorSystem("FileProcessorActorSystem")
  val rootActor = actorSystem.actorOf(Props[RootActor], "rootActor")

  rootActor ! StartActorSystem(srcDirPath, destDirPath, extensionFilterList)
}
