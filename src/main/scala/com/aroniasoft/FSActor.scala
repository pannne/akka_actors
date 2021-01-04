package com.aroniasoft

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import java.io.{File, FilenameFilter}

class FSActor extends Actor with ActorLogging {
  import FSActor._

  private var numberOfFiles: Int = 0
  private var numberOfProcessedFiles: Int = 0
  private var fileSchedulerActor = Actor.noSender
  private var singleThreadActor = Actor.noSender

  def receive = {
    case InitMsg(scheduler, processor) =>
      fileSchedulerActor = scheduler
      singleThreadActor = processor
    case StartFolderScan(path, extList) =>
      val files: Array[File] = getFilteredList(new File(path), extList)
      numberOfFiles = files.length
      log.info(s"Number of matched files: ${numberOfFiles}")
      for(f <- files) {
        val fileProcessingActor = context.actorOf(Props[FileProcessingActor])
        fileProcessingActor ! FileProcessingActor.InitMsg(fileProcessingActor, singleThreadActor)
        fileProcessingActor ! ProcessFileMsg(f.getAbsolutePath)
      }

    case FileProcessed =>
      numberOfProcessedFiles = numberOfProcessedFiles + 1
      log.info(s"PROCESSED: ${numberOfProcessedFiles}")
      if(numberOfProcessedFiles == numberOfFiles) {
        log.info("All files processed, shutting down...")
        context.system.terminate
      }

  }
}

object FSActor {

  case class InitMsg(scheduler: ActorRef, processor: ActorRef)

  def createFilter(extList: List[String]) = {
    extList match {
      case Nil => Option.empty
      case _ => Option(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = {
          println(name)
          println(dir.isDirectory)
          println(extList.map(name.toLowerCase.endsWith(_)).reduceLeft(_ || _))
          /*dir.isDirectory || */extList.map(name.toLowerCase.endsWith(_)).reduceLeft(_ || _)
        }
      })
    }
  }

  private def getListOfFiles(dir: File): Array[File] = {
    val files = dir.listFiles
    files ++ files.filter(_.isDirectory).flatMap(getListOfFiles(_))
  }

  def getFilteredList(dir: File, extList: List[String]) = {
    getListOfFiles(dir).filter(f => !f.isDirectory && extList.map(f.getName.toLowerCase.endsWith(_)).reduceLeft(_ || _) )
  }
}