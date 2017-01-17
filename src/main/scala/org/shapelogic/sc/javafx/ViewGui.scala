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
class ViewGui extends BaseGui {

  var canvas: ImageView = null

  def setupImageArea(stage: Stage, root: BorderPane): Unit = {
    canvas = new ImageView()
    drawImage = (img: Image) => { canvas.setImage(img); img }
    root.setCenter(canvas)
  }

}

object ViewGui {
  def main(args: Array[String]): Unit = {
    val klass = classOf[ViewGui]
    Application.launch(klass, args: _*)
  }
}