package org.shapelogic.sc.javafx

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.image.Image

import scala.collection.JavaConversions._
import org.shapelogic.sc.util.Args
import javafx.scene.control.MenuBar
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.application.Platform
import javafx.stage.FileChooser
import java.io.File
import javafx.stage.FileChooser.ExtensionFilter
import org.shapelogic.sc.io.BufferedImageConverter
import javafx.embed.swing.SwingFXUtils
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.operation.Transforms
import org.shapelogic.sc.util.ImageInfo
import scala.specialized
import scala.reflect.ClassTag
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelFormat
import org.shapelogic.sc.image._
import javafx.scene.image.WritableImage
import javafx.scene.image.PixelWriter
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.ByteBuffer

object LoadJFxImage {

  def loadBufferImage(urlOrFile: String): BufferImage[Byte] = {
    if (urlOrFile != null) {
      val url: String = if (urlOrFile.startsWith("http")) urlOrFile else s"file:$urlOrFile"
      val image = new Image(url)
      jFxImage2BufferImage(image)
    } else {
      throw new Exception(s"LoadJFxImage.loadBufferImage: urlOrFile == null")
    }
  }

  def jFxImage2BufferImage(image: Image): BufferImage[Byte] = {
    println(s"jFxImage2BufferImage")
    val numBands = 4
    val pixelReader: PixelReader = image.getPixelReader()
    val width = image.getWidth().toInt
    val height = image.getHeight().toInt
    val buffer: Array[Byte] = Array.ofDim[Byte](width * height * numBands)
    pixelReader.getPixels(
      0,
      0,
      width,
      height,
      PixelFormat.getByteBgraInstance(), // XXX Tried to use RGB format but that was ambiguous
      buffer,
      0,
      width * numBands)
    val res = new BufferImage[Byte](width,
      height,
      numBands,
      buffer,
      rgbOffsetsOpt = Some(bgraRGBOffsets))
    res
  }
  /*
  // overloaded method value getPixels with alternatives
  def jFxImage2BufferImage3(image: Image): BufferImage[Byte] = {
    val numBands = 4
    val pixelReader: PixelReader = image.getPixelReader()
    val width = image.getWidth().toInt
    val height = image.getHeight().toInt
    val buffer: Array[Byte] = new Array[Byte](width * height * numBands)
    pixelReader.getPixels(
      0,
      0,
      width,
      height,
      PixelFormat.getByteRgbInstance(), // XXX Tried to use RGB format but that was ambiguous
      buffer: Array[Byte],
      0,
      width * numBands)
    val res = new BufferImage[Byte](width,
      height,
      numBands,
      buffer,
      rgbOffsetsOpt = Some(rgbRGBOffsets))
    res
  }
*/

  def bufferImage2jFxImage(image: BufferImage[Byte]): Image = {
    val width = image.width
    val height = image.height
    val numBands = image.numBands

    if (numBands == 1) {
      return GrayImageHelper.grayBuffer2Image(bytearray = image.data, width = width, height = height)
    }

    val outputImage: WritableImage = new WritableImage(width, height)

    val pixels = image.data

    val pixelWriter: PixelWriter = outputImage.getPixelWriter();

    val pixelFormat: PixelFormat[ByteBuffer] =
      if (numBands == 1) {
        println(s"numBands: $numBands not supported")
        null
      } else if (numBands == 3)
        PixelFormat.getByteRgbInstance()
      else if (numBands == 4)
        PixelFormat.getByteBgraInstance()
      else {
        println(s"numBands: $numBands not supported")
        null
      }

    pixelWriter.setPixels(0, 0,
      width, height,
      pixelFormat,
      pixels, 0,
      width * numBands);
    outputImage
  }

  def imageSaveAs(image: Image, filename: String): Unit = {
    val outputFile: File = new File(filename)
    val bImage: BufferedImage = SwingFXUtils.fromFXImage(image, null);
    try {
      ImageIO.write(bImage, "png", outputFile);
    } catch {
      case ex: Throwable => {
        println(s"imageSaveAs to $filename failed \n ${ex.getMessage}")
      }
    }
  }
}