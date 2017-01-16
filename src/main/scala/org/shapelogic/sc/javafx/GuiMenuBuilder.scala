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
 * First thought was that this was just for creation of the menu
 * But maybe this can be a class that sticks around
 */
class GuiMenuBuilder(stage: Stage, root: BorderPane, drawImage: Image => Image) {
  var lastImage: Image = null

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
      val image = new Image(url)
      lastImage = drawImage(image)
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
      lastImage = JFXHelper.transformImage(lastImage, Transforms.inverseTransformByte)
      drawImage(lastImage)
    }
  })

  val blackItem: MenuItem = new MenuItem("Black")
  blackItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      println("Make image black")
      lastImage = JFXHelper.transformImage(lastImage, Transforms.blackTransformByte)
      drawImage(lastImage)
    }
  })

  menuFile.getItems().addAll(openItem, exitItem)
  menuEdit.getItems().addAll(undoItem)
  menuImage.getItems().addAll(inverseItem, blackItem)

  menuBar.getMenus().addAll(menuFile, menuEdit, menuImage)
}