package com.aroniasoft

import scala.util.{Failure, Success, Try}
import java.text.SimpleDateFormat
import java.io.File

import scala.collection.JavaConverters._

trait FileProcessor {
  def getCreationDate(f: File): Try[String]
  def getFileInfo(s: String): FileInfo = {
    import org.apache.commons.io.FilenameUtils
    val f: File = new File(s)
    getCreationDate(f) match {
      case Success(date) => FileInfoReturned(s, FilenameUtils.getName(s), date, f.length)
      case Failure(_) => FileInfoReturned(s, FilenameUtils.getName(s), Commons.unsorted, f.length)
    }
  }
  def isSingleThreaded = false
}

object FileProcessor {

  import java.util.Date

  private val JPG = "jpg"
  private val JPEG = "jpeg"

  private val MOV = "mov"
  private val MP4 = "mp4"
  private val THREE_GP = "3gp"

  private def nonDeterminedDate: String = new SimpleDateFormat("yyyyMMdd").format(new Date())

  private class ImageProcessor extends FileProcessor {
    import org.apache.commons.imaging.Imaging
    import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
    import org.apache.commons.imaging.common.GenericImageMetadata.GenericImageMetadataItem

    override def getCreationDate(f: File): Try[String] = {
      Try(Imaging.getMetadata(f)) match {
        case Success(metadata) => {
          metadata match {
            case j: JpegImageMetadata => getCreationDateFromImg(j, f)
            case _ => new Failure[String](new IllegalArgumentException)
          }
        }
        case Failure(_) => getCreationDateFromFileAttributes(f)
      }
    }

    private def getCreationDateFromImg(img: JpegImageMetadata, f: File) = Try {
      var creationDate = nonDeterminedDate
      img.getItems.asScala.foreach {i =>
        i match {
          case g: GenericImageMetadataItem => {
            if(g.getKeyword.equalsIgnoreCase("DateTimeOriginal")) {
              creationDate = formatDate(g.getText, "''uuuu:MM:dd HH:mm:ss''", "uuuuMMdd")
            }
          }
        }
      }
      if(creationDate.equals(nonDeterminedDate))
        getCreationDateFromFileAttributes(f) match {
          case Success(value) => creationDate = value
        }

      creationDate
    }
  }

  private class VideoProcessor extends FileProcessor {
    import io.humble.video._

    override def isSingleThreaded = true
    override def getCreationDate(f: File): Try[String] = {
      val demuxer = Demuxer.make
      demuxer.open(f.getAbsolutePath, null, false, true, null, null)
      val metadata = demuxer.getMetaData
      demuxer.close
      if(metadata != null)
        getCreationDateFromVideo(metadata)
      else
        getCreationDateFromFileAttributes(f)
    }

    private def getCreationDateFromVideo(metadata: KeyValueBag): Try[String] = Try {

      var creationDate = nonDeterminedDate
      for (key <- metadata.getKeys.asScala) {
        if (key.equalsIgnoreCase("creation_time"))
          creationDate = formatDate(metadata.getValue(key), "uuuu-MM-dd HH:mm:ss", "uuuuMMdd")
      }
      creationDate
    }
  }

  private class VoidProcessor extends FileProcessor {
    override def getCreationDate(f:File): Try[String] = new Failure[String](new UnsupportedOperationException)
  }

  def apply(filePath: String): FileProcessor = {
    import org.apache.commons.io.FilenameUtils
    FilenameUtils.getExtension(filePath).toLowerCase match {
      case JPG | JPEG => new ImageProcessor
      case MOV | MP4 | THREE_GP => new VideoProcessor
      case _ => new VoidProcessor
    }
  }

  def getCreationDateFromFileAttributes(f: File) = Try {
    import java.nio.file.Files
    import java.nio.file.attribute.BasicFileAttributes
    val attr: BasicFileAttributes = Files.readAttributes(f.toPath, classOf[BasicFileAttributes])
    val creationTime = attr.creationTime
    val lastModifiedTime = attr.lastModifiedTime
    val older = if(creationTime.compareTo(lastModifiedTime) < 0) creationTime else lastModifiedTime
    val sdf = new SimpleDateFormat("yyyyMMdd")
    sdf.format(older.toMillis)
  }

  def formatDate(date: String, inputPattern: String, outputPattern: String): String = {
    import java.time.LocalDate
    import java.time.format.DateTimeFormatter

    LocalDate.parse(date, DateTimeFormatter.ofPattern(inputPattern)).format(DateTimeFormatter.ofPattern(outputPattern))
  }
}