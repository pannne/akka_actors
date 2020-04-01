package com.aroniasoft

import akka.actor.{Actor, ActorLogging}

class FileProcessingActor extends Actor with ActorLogging {

  override def receive = {
    case ProcessFileMsg(s) =>
      log.info(s"file ${s} processed")
      context.parent ! FileProcessed
  }
}
