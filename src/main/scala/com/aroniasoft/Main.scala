package com.aroniasoft

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

class RootActor extends Actor {
  def receive = {
    case StartActorSystem(src, dst, extFilter) => {
      println("start FS Actor")
      FileProcessorActorSystem.fileSchedulerActor ! SetDestinationDir(dst)
      val fsActor = context.actorOf(Props[FSActor], "fsActor")
      fsActor ! StartFolderScan(src, extFilter)
    }
    case _ => context.system.terminate()
  }
}

object FileProcessorActorSystem {

  def actorSystem = ActorSystem("FileProcessorActorSystem")

  def rootActor = actorSystem.actorOf(Props[RootActor], "rootActor")

  def fileSchedulerActor = actorSystem.actorOf(Props[FileSchedulerActor], "fileSchedulerActor")
}

object Main extends App {

  val srcDirPath: String = "C:\\Users\\nesovic\\Desktop\\nokia-3-3-20\\"
  val destDirPath: String = "e:\\media\\"
  val extensionFilterList: List[String] = List("jpg", "jpeg")

  val system = FileProcessorActorSystem

  system.rootActor ! StartActorSystem(srcDirPath, destDirPath, extensionFilterList)

}
