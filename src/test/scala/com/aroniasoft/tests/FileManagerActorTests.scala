package com.aroniasoft.tests

import com.aroniasoft.FileManagerActor
import org.scalatest.flatspec.AnyFlatSpec

class FileManagerActorTests extends AnyFlatSpec {

  behavior of "FileManagerActor"

  it should "return full folder path" in {
    val fullPath = FileManagerActor.formDirectoryStructure("C:\\Users\\nesovic\\Desktop\\tmp", "20160625")
    println(fullPath)
    assert(fullPath.length > 0)
  }
}
