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

object LoadJFxImage {

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
    val w = image.width
    val h = image.height
    val numBands = 3
    val outputImage: WritableImage = new WritableImage(w, h)

    val pixels = image.data

    val pixelWriter: PixelWriter = outputImage.getPixelWriter();

    pixelWriter.setPixels(0, 0,
      w, h,
      PixelFormat.getByteRgbInstance(),
      pixels, 0,
      w * numBands);
    outputImage
  }
}