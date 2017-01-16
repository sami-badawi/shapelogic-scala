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

/**
 * Belongs in Util, but JavaFX dependency is still experimental
 */
object JFXHelper {

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

  def loadImage(canvas: Canvas, url: String): Image = {
    val image = new Image(url)
    val gc: GraphicsContext = canvas.getGraphicsContext2D()
    gc.clearRect(0, 0, 800, 600) // XXX need to be set dynamically
    gc.drawImage(image, 10, 20)
    image
  }

  def getBufferImage(lastImage: Image): Option[BufferImage[Byte]] = {
    val bufferedImage = SwingFXUtils.fromFXImage(lastImage, null)
    BufferedImageConverter.awtBufferedImage2BufferImage(bufferedImage)
  }

  def inverseCurrent(lastImage: Image, canvas: Canvas): Image = {
    try {
      val bufferImage1: BufferImage[Byte] = getBufferImage(lastImage).get
      val bufferImage2 = Transforms.makeInverseTransformByte(bufferImage1).result
      val bufferedImage2 = BufferedImageConverter.bufferImage2AwtBufferedImage(bufferImage2).get
      val gc: GraphicsContext = canvas.getGraphicsContext2D()
      val image2 = SwingFXUtils.toFXImage(bufferedImage2, null)
      println("Inverted image, start drawing it")
      gc.drawImage(image2, 10, 20)
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