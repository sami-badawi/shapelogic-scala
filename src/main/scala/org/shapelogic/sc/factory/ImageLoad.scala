package org.shapelogic.sc.factory

import org.shapelogic.sc.javafx.LoadJFxImage
import org.shapelogic.sc.io.LoadImage
import org.shapelogic.sc.io.BufferImageFactory
import scala.util.Try
import org.shapelogic.sc.image.BufferImage

/**
 * Often there are several operations pertaining to images that you need to do
 * in order to load an image.
 */
class ImageLoad(baseDir: String = "./src/test/resources/data/images",
    imageDir: String = "",
    _fileFormat: String = "",
    useJavaFXImage: Boolean = false) {

  lazy val _dirURL = if (imageDir.isEmpty())
    s"$baseDir"
  else
    s"$baseDir/$imageDir"

  def filePath(fileName: String): String = {
    _dirURL + "/" + fileName + _fileFormat
  }

  def filePath(fileName: String, fileFormat: String): String = {
    return _dirURL + "/" + fileName + fileFormat
  }

  def filePath(dir: String, fileName: String, fileFormat: String): String = {
    return dir + "/" + fileName + fileFormat
  }

  /**
   * Should maybe be moved so it can be used globally
   */
  def choseBufferImageFactory(useJavaFXImageIn: Boolean): BufferImageFactory[Byte] = {
    if (useJavaFXImageIn)
      LoadJFxImage
    else
      LoadImage
  }

  lazy val bufferImageFactory: BufferImageFactory[Byte] = choseBufferImageFactory(useJavaFXImage)

  def loadImageTry(filename: String): Try[BufferImage[Byte]] = {
    bufferImageFactory.loadBufferImageTry(filename)
  }
}