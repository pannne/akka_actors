package com.aroniasoft.tests

import com.aroniasoft.FileProcessor
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import scala.util.Success

import java.io.File

class FileProcessorTests extends AnyFlatSpec {

  val movProcessor = FileProcessor("a.MOV")
  val mp4Processor = FileProcessor("a.MP4")
  val _3gpProcessor = FileProcessor("a.3GP")
  val jpgProcessor = FileProcessor("a.JPG")
  val jpegProcessor = FileProcessor("a.JPEG")
  val dummyProcessor = FileProcessor("a.zzz")

  behavior of "FileProcessor"

  it should "produce VideoProcessor for mov files" in {
    movProcessor.getClass.getName shouldBe "com.aroniasoft.FileProcessor$VideoProcessor"
  }

  it should "produce VideoProcessor for mp4 files" in {
    mp4Processor.getClass.getName shouldBe "com.aroniasoft.FileProcessor$VideoProcessor"
  }

  it should "produce VideoProcessor for 3gp files" in {
    _3gpProcessor.getClass.getName shouldBe "com.aroniasoft.FileProcessor$VideoProcessor"
  }

  it should "produce ImageProcessor for jpg files" in {
    jpgProcessor.getClass.getName shouldBe "com.aroniasoft.FileProcessor$ImageProcessor"
  }

  it should "produce ImageProcessor for jpeg files" in {
    jpegProcessor.getClass.getName shouldBe "com.aroniasoft.FileProcessor$ImageProcessor"
  }

  it should "produce VoidProcessor for other file types" in {
    dummyProcessor.getClass.getName shouldBe "com.aroniasoft.FileProcessor$VoidProcessor"
  }

  it should "return mov video creation date as string" in {
    val movFile = new File("D:\\sve_slike\\Iphone\\IMG_0812.MOV")
    val dateCreated = movProcessor.getCreationDate(movFile)
    println("MOV date created: " + dateCreated)
    dateCreated shouldBe a[Success[String]]
  }

  it should "return mp4 video creation date as string" in {
    val movFile = new File("D:\\sve_slike\\Aparat\\SAM_0015.MP4")
    val dateCreated = movProcessor.getCreationDate(movFile)
    println("MP4 date created: " + dateCreated)
    dateCreated shouldBe a[Success[String]]
  }

  it should "return 3gp video creation date as string" in {
    val movFile = new File("D:\\sve_slike\\natasa-tel\\DCIM\\Camera\\VID_20170409_204649.3gp")
    val dateCreated = movProcessor.getCreationDate(movFile)
    println("3gp date created: " + dateCreated)
    dateCreated shouldBe a[Success[String]]
  }

  it should "return jpg image creation date as string" in {
    val jpgFile = new File("C:\\Users\\nesovic\\Desktop\\nokia-3-3-20\\IMG_20190521_210850.jpg")
    val dateCreated = jpgProcessor.getCreationDate(jpgFile)
    println("jpg date created: " + dateCreated)
    dateCreated shouldBe a[Success[String]]
  }

  it should "return jpeg image creation date as string" in {
    val jpgFile = new File("D:\\sve_slike\\DARKO I NATASA\\IMG_30392404106228.jpeg")
    val dateCreated = jpgProcessor.getCreationDate(jpgFile)
    println("jpg date created: " + dateCreated)
    dateCreated shouldBe a[Success[String]]
  }
}
