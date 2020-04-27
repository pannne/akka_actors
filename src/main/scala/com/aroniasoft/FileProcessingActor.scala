package com.aroniasoft

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class FileProcessingActor extends Actor with ActorLogging {

  import FileProcessingActor._

  private var fileSchedulerActor = Actor.noSender
  private var singleThreadActor = Actor.noSender

  override def receive = {
    case InitMsg(scheduler, processor) =>
      fileSchedulerActor = scheduler
      singleThreadActor = processor
    case ProcessFileMsg(s) => {
      val processor = FileProcessor(s)
      if(processor.isSingleThreaded) {
        singleThreadActor ! SingleThreadActors.RequestProcessMsg(processor, s)
      } else {
        val fileInfo: FileInfo = processFileMsgAction(processor, s)
        fileSchedulerActor ! fileInfo
      }
    }
    case FileProcessed => context.parent ! FileProcessed
  }
}

object FileProcessingActor {
  import org.apache.commons.io.FilenameUtils
  import java.io.File

  case class InitMsg(scheduler: ActorRef, processor: ActorRef)

  def processFileMsgAction(processor: FileProcessor, filepath: String): FileInfo = {

      raw"(20\d{6})".r.findFirstIn(filepath) match {
        case Some(date) => FileInfoReturned(filepath, FilenameUtils.getName(filepath), date, new File(filepath).length)
        case None => processor.getFileInfo(filepath)
      }
  }
}

