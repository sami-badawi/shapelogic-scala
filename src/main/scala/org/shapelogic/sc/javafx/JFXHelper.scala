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
  
}