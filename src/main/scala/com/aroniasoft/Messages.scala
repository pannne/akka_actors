package com.aroniasoft

import java.io.File

case class StartActorSystem(src: String, dst: String, extFilter: List[String])

case class StartFolderScan(path: String, extensionFilter: List[String] = List.empty)

case class ProcessFileMsg(filePath: String)

case object FileProcessed

trait FileInfo {
  def path: String
  def fileName: String
}

case class FileInfoReturned(path: String, fileName: String, creationDate: String, length: Long) extends FileInfo

case class FileInfoRequested(path: String, fileName: String) extends FileInfo

case class FileDestinationInfo(dstDir: String, fileInfo: FileInfo)

