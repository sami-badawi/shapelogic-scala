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
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType

/**
 * First thought was that this was just for creation of the menu
 * But maybe this can be a class that sticks around
 */
class GuiMenuBuilder(stage: Stage, root: BorderPane, drawImage: Image => Image) {
  var lastImage: Image = null
  var previousImage: Image = null

  def backup(image: Image): Unit = {
    previousImage = lastImage
    lastImage = image
  }

  val menuBar: MenuBar = new MenuBar()
  menuBar.setStyle("-fx-padding: 5 10 8 10;");

  menuBar.prefWidthProperty().bind(stage.widthProperty())
  root.setTop(menuBar)

  // --- Menu File
  val menuFile: Menu = new Menu("File")

  val menuEdit = new Menu("Edit")

  val menuImage = new Menu("Image")

  val menuHelp = new Menu("Help")

  val undoItem = new MenuItem("Undo")
  undoItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      if (previousImage != null) {
        val previousImageTemp = lastImage
        lastImage = previousImage
        previousImage = previousImageTemp
        drawImage(lastImage)
      } else {
        println(s"Warning: Undo previousImage == null do nothing")
      }
    }
  })

  val urlDefault = "https://upload.wikimedia.org/wikipedia/en/thumb/2/24/Lenna.png/440px-Lenna.png"
  val openItem: MenuItem = new MenuItem("Open")
  openItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      val fileOrNull = JFXHelper.fileChoser(stage)
      val url = if (fileOrNull == null) urlDefault else s"file:$fileOrNull"
      val image = new Image(url)
      backup(drawImage(image))
    }
  })

  val saveAsItem: MenuItem = new MenuItem("Save as")
  saveAsItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      val fileOrNull = JFXHelper.saveDialog(stage)
      if (fileOrNull == null) {
        println("Warning: Save As: fileOrNull == null do nothing")
      } else {
        println(s"Save file to $fileOrNull")
        LoadJFxImage.imageSaveAs(lastImage, fileOrNull)
      }
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
      backup(drawImage(JFXHelper.transformImage(lastImage, Transforms.inverseTransformByte)))
    }
  })

  val blackItem: MenuItem = new MenuItem("Black")
  blackItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      println("Make image black")
      backup(drawImage(JFXHelper.transformImage(lastImage, Transforms.blackTransformByte)))
    }
  })

  val whiteItem: MenuItem = new MenuItem("White")
  whiteItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      println("Make image white")
      backup(drawImage(JFXHelper.transformImage(lastImage, Transforms.whiteTransformByte)))
    }
  })

  val aboutItem: MenuItem = new MenuItem("About")
  aboutItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      val alert: Alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("ShapeLogic About");
      alert.setHeaderText("ShapeLogic version 0.4");
      val message =
        """Scala generic image processing / conputer vision 
https://github.com/sami-badawi/shapelogic-scala """
      alert.setContentText(message);
      alert.show();
    }
  })

  menuFile.getItems().addAll(openItem, saveAsItem, exitItem)
  menuEdit.getItems().addAll(undoItem)
  menuImage.getItems().addAll(inverseItem, blackItem, whiteItem)
  menuHelp.getItems().addAll(aboutItem)

  menuBar.getMenus().addAll(menuFile, menuEdit, menuImage, menuHelp)
}