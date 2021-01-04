package com.aroniasoft.tests

import com.aroniasoft.FSActor
import org.scalatest.flatspec.AnyFlatSpec
import java.io.File
import org.apache.commons.io.FilenameUtils

class FSActorTests extends AnyFlatSpec {

  behavior of "FSActor"

  it should "filter just jpg files" in {
    val filterList: List[String] = List("jpg")
    val filesArray = FSActor.getFilteredList(new File("d:\\sve_slike\\"), filterList).filter(f => !FilenameUtils.getExtension(f.getAbsolutePath).equalsIgnoreCase("jpg"))
    assert(filesArray.length === 0)
  }
}
