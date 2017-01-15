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

  override def start(stage: Stage): Unit = {
    val parameters = getParameters()
    val arguments = getParsedArgs()
    val canvas = new Canvas(800, 600)
    val filename = if (arguments.input == null || arguments.input.isEmpty) "image/output.png" else arguments.input
    val image = new Image(s"file:$filename")
    val gc: GraphicsContext = canvas.getGraphicsContext2D()
    gc.drawImage(image, 10, 20)

    val root = new BorderPane()
    // Set the Style-properties of the Pane
    //    root.setStyle("-fx-padding: 10;" +
    //      "-fx-border-style: solid inside;" +
    //      "-fx-border-width: 2;" +
    //      "-fx-border-insets: 5;" +
    //      "-fx-border-radius: 5;" +
    //      "-fx-border-color: blue;")

    //    root.getChildren().add(canvas)

    val menuBar: MenuBar = new MenuBar()
    
    menuBar.prefWidthProperty().bind(stage.widthProperty())
    root.setTop(menuBar)

    // --- Menu File
    val menuFile: Menu = new Menu("File")

    val menuEdit = new Menu("Edit")

    val exit: MenuItem = new MenuItem("Exit")
    exit.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        System.exit(0)
      }
    })
    menuFile.getItems().addAll(exit)

    menuBar.getMenus().addAll(menuFile, menuEdit)

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