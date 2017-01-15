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

/**
 * Class has to be separate from object for JavaFX to work
 */
class JavaFXGui extends Application {

  /**
   * Fish out the usual Java / Scala args: Array[String]
   */
  def getMainArgs(): Array[String] = {
    val parameters = getParameters()
    val unNamed = parameters.getUnnamed
    val seq = unNamed.toSeq
    seq.toArray
  }

  /**
   * parse the command line arguments to class Args
   */
  def getParsedArgs(): Args = {
    val args = getMainArgs()
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

  def findUrl(arguments: Args): String = {
    val filename: String = if (arguments.input == null || arguments.input.isEmpty) "image/output.png" else arguments.input
    if (filename.startsWith("http"))
      filename
    else
      s"file:$filename"
  }

  def loadImage(url: String): Unit = {
    val image = new Image(url)
    val gc: GraphicsContext = canvas.getGraphicsContext2D()
    gc.drawImage(image, 10, 20)
  }

  var canvas: Canvas = null

  override def start(stage: Stage): Unit = {
    val parameters = getParameters()
    val arguments = getParsedArgs()
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

    val imageEdit = new Menu("Image")

    val undoItem = new MenuItem("Undo")

    val openItem: MenuItem = new MenuItem("Open")

    val exit: MenuItem = new MenuItem("Exit")
    exit.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        Platform.exit()
        System.exit(0)
      }
    })
    menuFile.getItems().addAll(openItem, exit)
    menuEdit.getItems().addAll(undoItem)

    menuBar.getMenus().addAll(menuFile, menuEdit, imageEdit)

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