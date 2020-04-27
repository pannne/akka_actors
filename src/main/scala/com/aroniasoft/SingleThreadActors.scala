package com.aroniasoft

import akka.actor.{Actor, ActorLogging, ActorRef}

class SingleThreadActors extends Actor with ActorLogging {
  import SingleThreadActors._

  private var fileSchedulerActor = Actor.noSender

  override def receive: Receive = {
    case InitMsg(actor) => fileSchedulerActor = actor
    case RequestProcessMsg(processor, path) =>
      val fileInfo: FileInfo = FileProcessingActor.processFileMsgAction(processor, path)
      fileSchedulerActor ! fileInfo
  }
}

object SingleThreadActors {
  case class InitMsg(fileSchedulerActor: ActorRef)
  case class RequestProcessMsg(processor: FileProcessor, path: String)
}
