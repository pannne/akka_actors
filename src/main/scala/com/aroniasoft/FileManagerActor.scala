package com.aroniasoft

import akka.actor.{Actor, ActorLogging}
import java.io.File
import java.nio.file.{Files, Paths}
import java.time.LocalDate

import scala.util.{Failure, Success, Try}


class FileManagerActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case FileDestinationInfo(dst, FileInfoReturned(path, name, creationDate, length)) =>
      val dstDir = new File(FileManagerActor.formDirectoryStructure(dst, creationDate))
      Try(dstDir.mkdirs()) match {
        case Success(_) =>
          FileManagerActor.copyFile(path, dstDir.getAbsolutePath + File.separator + name)
          context.parent ! FileProcessed
        case Failure(err) =>
          println(err)
          context.parent ! FileProcessed
      }
  }
}

object FileManagerActor {
  import java.time.format.DateTimeFormatter
  import scala.util.{Try, Success, Failure}

  def formatDate(date: String) = {
    val parser = DateTimeFormatter.ofPattern(Commons.inDatePattern)
    val formatter = DateTimeFormatter.ofPattern(Commons.outDatePattern)
    Try(LocalDate.parse(date, parser).atStartOfDay()) match {
      case Success(value) => value.format(formatter)
      case Failure(err) =>
        println(err)
        Commons.unsorted
    }
  }

  def formDirectoryStructure(root: String, date: String): String = {
    val pattern = raw"(\d{4})-(\w{3})-(\d{2})".r
    formatDate(date) match {
      case Commons.unsorted => root + File.separator + Commons.unsorted
      case pattern(y, m, d) => root + File.separator + y + File.separator + m + File.separator + d
      case _ => root + File.separator + Commons.unsorted
    }
  }

  def copyFile(src: String, dst: String) = {
    Try(Files.copy(Paths.get(src), Paths.get(dst))) match {
      case Success(_) => true
      case Failure(err) =>
        println(err)
        false
    }
  }
}
