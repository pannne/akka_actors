package com.aroniasoft

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class FileSchedulerActor extends Actor with ActorLogging {
  import FileSchedulerActor._
  import scala.collection.mutable.Map

  private var destDir = ""
  private var fsActor = Actor.noSender
  private val actorMap = Map[String, ActorRef]()

  override def receive: Receive = {
    case InitMsg(fsActorRef, dst) =>
      fsActor = fsActorRef
      destDir = dst
    case FileInfoReturned(path, name, date, length) =>
      val actor = if(actorMap.contains(date)) actorMap(date) else context.actorOf(Props[FileManagerActor], date)
      if(!actorMap.contains(date)) actorMap += (date -> actor)
      actor ! FileDestinationInfo(destDir, FileInfoReturned(path, name, date, length))

    case FileProcessed => fsActor ! FileProcessed
  }
}

object FileSchedulerActor {
  case class InitMsg(fsActorRef: ActorRef, dst: String)
}
