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
 * Class has to be separate from object for JavaFX to work
 */
class ViewGui extends Application {

  def findUrl(arguments: Args): String = {
    val filename: String = if (arguments.input == null || arguments.input.isEmpty) "image/440px-Lenna.png" else arguments.input
    if (filename.startsWith("http"))
      filename
    else
      s"file:$filename"
  }

  var canvas: Canvas = null
  var mainStage: Stage = null
  var guiMenuBuilder: GuiMenuBuilder = null

  override def start(stage: Stage): Unit = {
    mainStage = stage
    val parameters = getParameters()
    val arguments = JFXHelper.getParsedArgs(this)
    canvas = new Canvas(800, 600)

    val root = new BorderPane()
    // Set the Style-properties of the Pane
    root.setStyle("-fx-padding: 20;" +
      "-fx-border-style: solid inside;" +
      "-fx-border-width: 2;" +
      "-fx-border-insets: 5;" +
      "-fx-border-radius: 5;" +
      "-fx-border-color: blue;")

    //    root.getChildren().add(canvas)

    val scene = new Scene(root)
    val drawImage: Image => Image = (img: Image) => JFXHelper.drawImage(img, canvas)
    guiMenuBuilder = new GuiMenuBuilder(stage, root, drawImage)
    val url = findUrl(arguments)
    val image = new Image(url)
    guiMenuBuilder.lastImage = drawImage(image)
    stage.setScene(scene)
    root.setCenter(canvas)
    //    root.getChildren().addAll(canvas)
    stage.setTitle("ShapeLogic Scala")
    stage.show()
  }
}

object ViewGui {
  def main(args: Array[String]): Unit = {
    val klass = classOf[ViewGui]
    Application.launch(klass, args: _*)
  }
}