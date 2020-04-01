package com.aroniasoft

import akka.actor.{Actor, ActorLogging, Props}
import java.io.{File, FilenameFilter}

class FSActor extends Actor with ActorLogging {
  import FSActor._

  private var numberOfFiles: Int = 0
  private var numberOfProcessedFiles: Int = 0

  def receive = {
    case StartFolderScan(path, extList) =>
      log.info(s"Start folder: ${path} scan with ${extList.mkString(", ")}")
      val files: Array[File] = getListOfFiles(new File(path), createFilter(extList))
      numberOfFiles = files.length
      log.info(s"Number of matched files: ${numberOfFiles}")
      for(f <- files) {
        val fileProcessingActor = context.actorOf(Props[FileProcessingActor])
        fileProcessingActor ! ProcessFileMsg(f.getAbsolutePath)
      }

    case FileProcessed =>
      numberOfProcessedFiles = numberOfProcessedFiles + 1
      if(numberOfProcessedFiles == numberOfFiles) {
        log.info("All files processed, shutting down...")
        context.system.terminate
      }

  }
}

object FSActor {

  def createFilter(extList: List[String]) = {
    extList match {
      case Nil => Option.empty
      case _ => Option(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = extList.map(name.toLowerCase.endsWith(_)).reduceLeft(_ || _)
      })
    }
  }

  def getListOfFiles(dir: File, filenameFilter: Option[FilenameFilter] = Option.empty): Array[File] = {
    val files = if(filenameFilter.isEmpty) dir.listFiles else dir.listFiles(filenameFilter.get)
    files ++ files.filter(_.isDirectory).flatMap(getListOfFiles(_))
  }
}