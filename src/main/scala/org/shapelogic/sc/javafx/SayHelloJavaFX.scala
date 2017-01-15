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

/**
 * Class has to be separate from object for JavaFX to work
 */
class SayHelloJavaFX extends Application {

  override def start(stage: Stage): Unit = {
    val canvas = new Canvas(800, 600);
    val filename = "image/output.png"
    val image = new Image(s"file:$filename");
    val gc: GraphicsContext = canvas.getGraphicsContext2D();
    gc.drawImage(image, 10, 10)

    val root = new Pane();
    // Set the Style-properties of the Pane
    root.setStyle("-fx-padding: 10;" +
      "-fx-border-style: solid inside;" +
      "-fx-border-width: 2;" +
      "-fx-border-insets: 5;" +
      "-fx-border-radius: 5;" +
      "-fx-border-color: blue;");

    root.getChildren().add(canvas);
    val scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("ShapeLogic Scala");
    stage.show();
  }
}

object SayHelloJavaFX {
  def main(args: Array[String]): Unit = {
    val klass = classOf[SayHelloJavaFX]
    Application.launch(klass, args: _*)
  }
}