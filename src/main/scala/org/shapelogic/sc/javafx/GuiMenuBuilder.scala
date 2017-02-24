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
import org.shapelogic.sc.util.ImageInfo
import javafx.embed.swing.SwingFXUtils
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.image.HasBufferImage
import org.shapelogic.sc.operation.Transforms
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import org.shapelogic.sc.operation.ThresholdOperation
import org.shapelogic.sc.numeric.PrimitiveNumberPromoters

import spire.math.Numeric
import spire.implicits._
import scala.util.Try
import org.shapelogic.sc.operation.implement.Color2GrayOperation
import org.shapelogic.sc.operation.ChannelChoserOperation.ChannelChoserOperationByte
import org.shapelogic.sc.operation.implement.ImageOperationBandSwap
import scala.collection.mutable.ArrayBuffer
import org.shapelogic.sc.image.ImageTransformWithNameT
import org.shapelogic.sc.image.ImageTransformDialog
import org.shapelogic.sc.image.ImageTransformDialogT
import org.shapelogic.sc.operation.ChannelChoserOperation
import org.shapelogic.sc.util.Constants

/**
 * First thought was that this was just for creation of the menu
 * But maybe this can be a class that sticks around
 */
class GuiMenuBuilder(stage: Stage, root: BorderPane, drawImage: Image => Image) {
  val verboseLogging: Boolean = false

  val imageTransformWithNameRegistration: ArrayBuffer[ImageTransformWithNameT[Byte]] = new ArrayBuffer[ImageTransformWithNameT[Byte]]()

  var lastImageAndFilename: ImageAndFilename = null
  var previousImageAndFilename: ImageAndFilename = null

  // ============================= Util =============================

  def backup(bufferImage: BufferImage[_], image: Image, filename: String): Unit = {
    previousImageAndFilename = lastImageAndFilename
    lastImageAndFilename = ImageAndFilename(bufferImage = bufferImage, image = image, url = filename)
  }

  def backupImageAndFilename(imageAndFilename: ImageAndFilename): Unit = {
    previousImageAndFilename = lastImageAndFilename
    lastImageAndFilename = imageAndFilename
  }

  /**
   * This is slower than calcAndBackup but with same signature
   * Keep around if there are problems with calcAndBackup
   */
  def transformAndBackup(trans: BufferImage[Byte] => BufferImage[Byte], lastOperation: String): Unit = {
    try {
      println(s"lastOperation for ${lastImageAndFilename.url}")
      val (image1, buffer1) = JFXHelper.transformImage2(lastImageAndFilename.image, trans)
      backup(buffer1, drawImage(image1), lastImageAndFilename.url)
    } catch {
      case ex: Throwable => {
        println(s"transformAndBackup ${ex.getMessage}")
        ex.printStackTrace()
      }
    }
  }

  def calcAndBackup(
    transform: BufferImage[Byte] => BufferImage[Byte],
    lastOperation: String): Unit =
    {
      try {
        println(s"lastOperation for ${lastImageAndFilename.url}")
        val imageAndFilename1 = lastImageAndFilename.getWithBufferImage()
        val buffer2 = transform(imageAndFilename1.bufferImage.asInstanceOf[BufferImage[Byte]])
        val imageAndFilename2 = ImageAndFilename(bufferImage = buffer2, image = null, imageAndFilename1.url)
        val imageAndFilename3 = imageAndFilename2.getWithImage
        val image2 = drawImage(imageAndFilename3.image)
        val imageAndFilename4 = imageAndFilename3.copy(image = image2)
        if (verboseLogging)
          println(s"imageAndFilename4.url: ${imageAndFilename4.url}")
        backupImageAndFilename(imageAndFilename4)
      } catch {
        case ex: Throwable => {
          println(s"transformAndBackup ${ex.getMessage}")
          ex.printStackTrace()
        }
      }
    }

  def calcAndBackupWithParameters(
    transform: (BufferImage[Byte], String) => BufferImage[Byte],
    parameter: String,
    lastOperation: String): Unit =
    {
      try {
        println(s"lastOperation for ${lastImageAndFilename.url}")
        val imageAndFilename1 = lastImageAndFilename.getWithBufferImage()
        val buffer2 = transform(imageAndFilename1.bufferImage.asInstanceOf[BufferImage[Byte]], parameter)
        val imageAndFilename2 = ImageAndFilename(bufferImage = buffer2, image = null, imageAndFilename1.url)
        val imageAndFilename3 = imageAndFilename2.getWithImage
        val image2 = drawImage(imageAndFilename3.image)
        val imageAndFilename4 = imageAndFilename3.copy(image = image2)
        if (verboseLogging)
          println(s"imageAndFilename4.url: ${imageAndFilename4.url}")
        backupImageAndFilename(imageAndFilename4)
      } catch {
        case ex: Throwable => {
          println(s"transformAndBackup ${ex.getMessage}")
          ex.printStackTrace()
        }
      }
    }
  // ============================= Util =============================

  val menuBar: MenuBar = new MenuBar()
  menuBar.setStyle("-fx-padding: 5 10 8 10;");

  menuBar.prefWidthProperty().bind(stage.widthProperty())
  root.setTop(menuBar)

  // --- Menu File
  val menuFile: Menu = new Menu("File")

  val menuEdit = new Menu("Edit")

  val menuMorphology = new Menu("Binary")

  val menuImage = new Menu("Image")

  val menuHelp = new Menu("Help")

  // ============================= File menu =============================

  val urlDefault = "https://upload.wikimedia.org/wikipedia/en/thumb/2/24/Lenna.png/440px-Lenna.png"
  val openItem: MenuItem = new MenuItem("Open")
  openItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      val fileOrNull = JFXHelper.fileChoser(stage)
      if (fileOrNull != null) {
        val url = s"file:$fileOrNull"
        val image = new Image(url)
        backup(null, drawImage(image), url)
      }
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
        LoadJFxImage.imageSaveAs(lastImageAndFilename.image, fileOrNull)
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

  // ============================= Image operation menu =============================

  def addImageTransformDialog(imageTransformDialog: ImageTransformDialogT, menu: Menu): Unit = {
    if (verboseLogging)
      println(s"Add menue item: ${imageTransformDialog.name}")
    val menuItem = new MenuItem(imageTransformDialog.name)
    menuItem.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        val parameter = JFXHelper.queryDialog(question = imageTransformDialog.dialog, defaultText = imageTransformDialog.defaultValue)
        if (parameter == null || parameter == "")
          println("No input cancel: ${imageTransformDialog.name}")
        else
          calcAndBackupWithParameters(imageTransformDialog.transform, parameter, imageTransformDialog.name)
      }
    })
    menu.getItems().add(menuItem)
  }

  imageTransformWithNameRegistration.++=(Transforms.makeImageTransformWithNameSeq)

  def addImageTransformWithName(
    imageTransformWithName: ImageTransformWithNameT[Byte],
    menu: Menu): Unit = {
    if (verboseLogging)
      println(s"Add menue item: ${imageTransformWithName.name}")
    val menuItem = new MenuItem(imageTransformWithName.name)
    menuItem.setOnAction(new EventHandler[ActionEvent]() {
      def handle(t: ActionEvent): Unit = {
        calcAndBackup(imageTransformWithName.transform, imageTransformWithName.name)
      }
    })
    menu.getItems().add(menuItem)
  }

  def addAllImageTransformWithName(): Unit = {
    imageTransformWithNameRegistration.foreach(imageTransformWithName => addImageTransformWithName(imageTransformWithName, menuImage))
  }

  def addAllImageTransformWithNameMorphology(): Unit = {
    val all = Transforms.makeImageTransformWithNameMorphologySeq()
    all.foreach(imageTransformWithName => addImageTransformWithName(imageTransformWithName, menuMorphology))
  }

  // ============================= Edit and Help =============================

  val undoItem = new MenuItem("Undo")
  undoItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      if (previousImageAndFilename != null) {
        val previousImageTemp = lastImageAndFilename
        lastImageAndFilename = previousImageAndFilename
        previousImageAndFilename = previousImageTemp
        drawImage(lastImageAndFilename.image)
      } else {
        println(s"Warning: Undo previousImage == null do nothing")
      }
    }
  })

  val imageInfoItem: MenuItem = new MenuItem("Image Info")
  imageInfoItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      val releaseVersion = Constants.releaseVersion
      val alert: Alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("ShapeLogic Image Info");
      alert.setHeaderText(s"ShapeLogic version ${releaseVersion}");
      val message = if (lastImageAndFilename.bufferImage == null)
        ImageInfo.javaFXImageImageInfo.info(lastImageAndFilename.image, lastImageAndFilename.url)
      else
        ImageInfo.bufferImageImageInfo.info(lastImageAndFilename.bufferImage, lastImageAndFilename.url)
      alert.setContentText(message);
      alert.show();
    }
  })

  val aboutItem: MenuItem = new MenuItem("About")
  aboutItem.setOnAction(new EventHandler[ActionEvent]() {
    def handle(t: ActionEvent): Unit = {
      val releaseVersion = Constants.releaseVersion
      val alert: Alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("ShapeLogic About");
      alert.setHeaderText(s"ShapeLogic version ${releaseVersion}");
      val message =
        """Scala generic image processing / conputer vision 
https://github.com/sami-badawi/shapelogic-scala """
      alert.setContentText(message);
      alert.show();
    }
  })

  // ============================= Insert menu items =============================

  menuFile.getItems().addAll(openItem, saveAsItem, exitItem)
  menuEdit.getItems().addAll(undoItem, imageInfoItem)
  menuMorphology.getItems()
  menuImage.getItems().addAll()
  menuHelp.getItems().addAll(aboutItem)

  addAllImageTransformWithName()
  addAllImageTransformWithNameMorphology()
  //Add your operations here
  val imageTransformDialogSeq = Transforms.makeImageTransformDialogSeq()

  imageTransformDialogSeq.foreach(imageTransformDialog => addImageTransformDialog(imageTransformDialog, menuImage))

  Transforms.makeImageTransformDialogMorphologySeq.foreach(
    imageTransformDialog => addImageTransformDialog(imageTransformDialog, menuMorphology))

  menuBar.getMenus().addAll(menuFile, menuEdit, menuMorphology, menuImage, menuHelp)
}