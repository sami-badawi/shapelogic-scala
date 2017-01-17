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

/**
 * Belongs in Util, but JavaFX dependency is still experimental
 */
object JFXHelper {
  import ImageInfo.ops

  val verboseLogging = true

  /**
   * Fish out the usual Java / Scala args: Array[String]
   */
  def getMainArgs(application: Application): Array[String] = {
    val parameters = application.getParameters()
    val unNamed = parameters.getUnnamed
    val seq = unNamed.toSeq
    seq.toArray
  }

  /**
   * parse the command line arguments to class Args
   */
  def getParsedArgs(application: Application): Args = {
    val args = getMainArgs(application)
    val paramOpt = Args.parser.parse(args, Args())
    paramOpt match {
      case Some(param) => {
        param
      }
      case None => {
        null
      }
    }
  }

  def fileChoser(stage: Stage): String = {
    val fileChooser: FileChooser = new FileChooser()
    fileChooser.setTitle("Open Image File")
    //    fileChooser.getExtensionFilters().addAll(
    //      new ExtensionFilter("Image Files", "*.png", "*.jpg","*.jpeg", "*.gif"),
    //      new ExtensionFilter("All Files", "*.*"));
    val selectedFile: File = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      selectedFile.getAbsolutePath
    } else {
      println("========== No file was found using default")
      null
    }
  }

  def drawImage(image: Image, canvas: Canvas): Image = {
    val gc: GraphicsContext = canvas.getGraphicsContext2D()
    gc.clearRect(0, 0, 800, 600) // XXX need to be set dynamically
    gc.drawImage(image, 10, 20)
    image
  }

  def getBufferImage(lastImage: Image): Option[BufferImage[Byte]] = {
    val bufferedImage = SwingFXUtils.fromFXImage(lastImage, null)
    if (bufferedImage == null)
      println("getBufferImage: bufferedImage == null")
    val bufferImageOpt = BufferedImageConverter.awtBufferedImage2BufferImage(bufferedImage)
    if (bufferImageOpt.isEmpty && bufferedImage != null) {
      val infoBufferedImage = ImageInfo.bufferedImageImageInfo.info(bufferedImage, "")
      println(s"getBufferImage problems. infoBufferedImage: " + infoBufferedImage)
    }
    bufferImageOpt
  }

  def transformImageViaBufferImage(lastImage: Image, trans: BufferImage[Byte] => BufferImage[Byte]): Image = {
    try {
      if (lastImage == null)
        println("transformImage: no input image")
      val bufferImage1: BufferImage[Byte] = getBufferImage(lastImage).get
      val bufferImage2 = trans(bufferImage1)
      val bufferedImage2 = BufferedImageConverter.bufferImage2AwtBufferedImage(bufferImage2).get
      val image2 = SwingFXUtils.toFXImage(bufferedImage2, null)
      println("Inverted image, start drawing it")
      image2
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
        ex.printStackTrace()
        null
      }
    }
  }

  def showBytes(byteArray: Array[Byte], count: Int): Unit = {
    try {
      for (i <- Range(0, count)) {
        val asInt: Int = byteArray(i).toInt
        println(s"$i: " + asInt.toString)
      }
    } catch {
      case ex: Throwable => {
        println("Error in showBytes" + ex.getMessage)
      }
    }
  }

  def transformImage(lastImage: Image, trans: BufferImage[Byte] => BufferImage[Byte]): Image = {
    try {
      if (lastImage == null)
        println("transformImage: no input image")
      val bufferImage1: BufferImage[Byte] = LoadJFxImage.jFxImage2BufferImage(lastImage)
      val bufferImage2 = trans(bufferImage1)
      if (verboseLogging) {
        val infoBufferedImage1 = ImageInfo.bufferImageImageInfo.info(bufferImage1, "")
        println(s"$infoBufferedImage1\nFirst 4 bytes bufferImage1")
        showBytes(bufferImage1.data, 4)
        val infoBufferedImage2 = ImageInfo.bufferImageImageInfo.info(bufferImage2, "")
        try {
          println(infoBufferedImage2)
        } catch {
          case ex2: Throwable => {
            println("Error in showBytes" + ex2.getMessage)
          }
        }
        println("First 4 bytes bufferImage2")
        showBytes(bufferImage1.data, 4)
      }
      val image2 = LoadJFxImage.bufferImage2jFxImage(bufferImage2)
      println("Inverted image, start drawing it")
      image2
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
        ex.printStackTrace()
        null
      }
    }
  }
}