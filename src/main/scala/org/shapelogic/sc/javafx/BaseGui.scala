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
import javafx.scene.image.ImageView
import javafx.scene.image.ImageView

/**
 * Class has to be separate from object for JavaFX to work
 */
abstract class BaseGui extends Application {

  var drawImage: Image => Image = null

  def findUrl(arguments: Args): String = {
    val filename: String = if (arguments.input == null || arguments.input.isEmpty) "image/440px-Lenna.png" else arguments.input
    if (filename.startsWith("http"))
      filename
    else
      s"file:$filename"
  }

  var mainStage: Stage = null
  var guiMenuBuilder: GuiMenuBuilder = null

  def setupImageArea(stage: Stage, root: BorderPane): Unit

  def setupStage(stage: Stage): Unit = {
    mainStage = stage

    val root = new BorderPane()
    // Set the Style-properties of the Pane
    root.setStyle("-fx-padding: 20;" +
      "-fx-border-style: solid inside;" +
      "-fx-border-width: 2;" +
      "-fx-border-insets: 5;" +
      "-fx-border-radius: 5;" +
      "-fx-border-color: blue;")

    //    root.getChildren().add(canvas)

    val scene = new Scene(root, 800, 600)
    setupImageArea(stage, root)
    guiMenuBuilder = new GuiMenuBuilder(stage, root, drawImage)
    stage.setScene(scene)
    //    root.getChildren().addAll(canvas)
    stage.setTitle("ShapeLogic Scala")
    stage.show()
  }

  override def start(stage: Stage): Unit = {
    setupStage(stage)
    loadStartImage()
  }

  def loadStartImage(): Unit = {
    try {
      val parameters = getParameters()
      val arguments = JFXHelper.getParsedArgs(this)
      val url = findUrl(arguments)
      val image = new Image(url)
      guiMenuBuilder.lastImage = drawImage(image)
      guiMenuBuilder.lastFilename = url
    } catch {
      case ex: Throwable => {
        println(s"loadStartImage() error: ${ex.getMessage}")
        ex.printStackTrace()
      }
    }
  }
}
