package com.aroniasoft

case object StartActorSystem

case class StartFolderScan(path: String, extensionFilter: List[String] = List.empty)

case class ProcessFileMsg(filePath: String)

case object FileProcessed

