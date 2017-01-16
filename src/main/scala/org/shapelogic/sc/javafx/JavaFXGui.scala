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
    val filename: String = if (arguments.input == null || arguments.input.isEmpty) "image/440px-Lenna.png" else arguments.input
    if (filename.startsWith("http"))
      filename
    else
      s"file:$filename"
  }

  def fileChoser(): String = {
    val fileChooser: FileChooser = new FileChooser()
    fileChooser.setTitle("Open Image File")
    //    fileChooser.getExtensionFilters().addAll(
    //      new ExtensionFilter("Image Files", "*.png", "*.jpg","*.jpeg", "*.gif"),
    //      new ExtensionFilter("All Files", "*.*"));
    val selectedFile: File = fileChooser.showOpenDialog(mainStage);
    if (selectedFile != null) {
      selectedFile.getAbsolutePath
    } else {
      println("========== No file was found using default")
      null
    }
  }

  def loadImage(url: String): Unit = {
    val image = new Image(url)
    val gc: GraphicsContext = canvas.getGraphicsContext2D()
    gc.clearRect(0, 0, 800, 600) // XXX need to be set dynamically
    gc.drawImage(image, 10, 20)
  }

  var canvas: Canvas = null
  var mainStage: Stage = null

  override def start(stage: Stage): Unit = {
    mainStage = stage
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

    val menuImage = new Menu("Image")

    val undoItem = new MenuItem("Undo")

    val urlDefault = "https://upload.wikimedia.org/wikipedia/en/thumb/2/24/Lenna.png/440px-Lenna.png"
    val openItem: MenuItem = new MenuItem("Open")
    openItem.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        val fileOrNull = fileChoser()
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