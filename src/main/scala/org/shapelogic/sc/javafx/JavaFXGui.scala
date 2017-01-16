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
class JavaFXGui extends Application {

  def findUrl(arguments: Args): String = {
    val filename: String = if (arguments.input == null || arguments.input.isEmpty) "image/440px-Lenna.png" else arguments.input
    if (filename.startsWith("http"))
      filename
    else
      s"file:$filename"
  }

  var lastImage: Image = null

  def getBufferImage(): Option[BufferImage[Byte]] = {
    val bufferedImage = SwingFXUtils.fromFXImage(lastImage, null)
    BufferedImageConverter.awtBufferedImage2BufferImage(bufferedImage)
  }

  def inverseCurrent(): Unit = {
    try {
      val bufferImage1: BufferImage[Byte] = getBufferImage().get
      val bufferImage2 = Transforms.makeInverseTransformByte(bufferImage1).result
      val bufferedImage2 = BufferedImageConverter.bufferImage2AwtBufferedImage(bufferImage2).get
      val gc: GraphicsContext = canvas.getGraphicsContext2D()
      val image2 = SwingFXUtils.toFXImage(bufferedImage2, null)
      println("Inverted image, start drawing it")
      gc.drawImage(image2, 10, 20)
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
        ex.printStackTrace()
      }
    }
  }

  def loadImage(url: String): Unit = {
    val image = new Image(url)
    lastImage = image
    val gc: GraphicsContext = canvas.getGraphicsContext2D()
    gc.clearRect(0, 0, 800, 600) // XXX need to be set dynamically
    gc.drawImage(image, 10, 20)
  }

  var canvas: Canvas = null
  var mainStage: Stage = null

  override def start(stage: Stage): Unit = {
    mainStage = stage
    val parameters = getParameters()
    val arguments = JFXHelper.getParsedArgs(this)
    canvas = new Canvas(800, 600)
    val url = findUrl(arguments)
    loadImage(url)

    val root = new BorderPane()
    // Set the Style-properties of the Pane
    root.setStyle("-fx-padding: 20;" +
      "-fx-border-style: solid inside;" +
      "-fx-border-width: 2;" +
      "-fx-border-insets: 5;" +
      "-fx-border-radius: 5;" +
      "-fx-border-color: blue;")

    //    root.getChildren().add(canvas)

    val menuBar: MenuBar = new MenuBar()
    menuBar.setStyle("-fx-padding: 5 10 8 10;");

    menuBar.prefWidthProperty().bind(stage.widthProperty())
    root.setTop(menuBar)

    // --- Menu File
    val menuFile: Menu = new Menu("File")

    val menuEdit = new Menu("Edit")

    val menuImage = new Menu("Image")

    val undoItem = new MenuItem("Undo")

    val urlDefault = "https://upload.wikimedia.org/wikipedia/en/thumb/2/24/Lenna.png/440px-Lenna.png"
    val openItem: MenuItem = new MenuItem("Open")
    openItem.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        val fileOrNull = JFXHelper.fileChoser(stage)
        val url = if (fileOrNull == null) urlDefault else s"file:$fileOrNull"
        loadImage(url)
      }
    })

    val exitItem: MenuItem = new MenuItem("Exit")
    exitItem.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        Platform.exit()
        System.exit(0)
      }
    })

    val inverseItem: MenuItem = new MenuItem("Inverse")
    inverseItem.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        println("Inverse image")
        inverseCurrent()
      }
    })

    menuFile.getItems().addAll(openItem, exitItem)
    menuEdit.getItems().addAll(undoItem)
    menuImage.getItems().addAll(inverseItem)

    menuBar.getMenus().addAll(menuFile, menuEdit, menuImage)

    val scene = new Scene(root)
    stage.setScene(scene)
    root.setCenter(canvas)
    //    root.getChildren().addAll(canvas)
    stage.setTitle("ShapeLogic Scala")
    stage.show()
  }
}

object JavaFXGui {
  def main(args: Array[String]): Unit = {
    val klass = classOf[JavaFXGui]
    Application.launch(klass, args: _*)
  }
}