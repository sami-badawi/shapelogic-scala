package org.shapelogic.sc.javafx

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

/**
 * Class has to be separate from object for JavaFX to work
 */
class SayHelloJavaFX extends Application {

  override def start(primaryStage: Stage): Unit = {
    val btn = new Button()
    btn.setText("Say 'Hello World'")
    btn.setOnAction(new EventHandler[ActionEvent]() {

      override def handle(event: ActionEvent): Unit = {
        println("Hello World!")
      }
    })

    val root = new StackPane()
    root.getChildren().add(btn)

    val scene = new Scene(root, 300, 250)

    primaryStage.setTitle("ShapeLogic Scala")
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}

object SayHelloJavaFX {
  def main(args: Array[String]): Unit = {
    val klass = classOf[SayHelloJavaFX]
    Application.launch(klass, args: _*)
  }
}