package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.polygon.AnnotatedShape
import org.shapelogic.sc.polygon.GeometricShape2D
import org.shapelogic.sc.polygon.Polygon
import org.shapelogic.sc.util.LineType
import org.shapelogic.sc.util.PointType

import org.scalatest._
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.javafx.LoadJFxImage
import javafx.scene.image.Image
import org.shapelogic.sc.javafx.LoadJFxImage
import org.shapelogic.sc.javafx.LoadJFxImage
import org.shapelogic.sc.io.BufferImageFactory
import org.shapelogic.sc.io.LoadImage
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import org.shapelogic.sc.factory.ImageLoad

object EdgeTracerSpec {

  lazy val imageLoad = new ImageLoad(
    baseDir = "./src/test/resources/data/images",
    imageDir = "particles",
    _fileFormat = ".gif",
    useJavaFXImage = false)

  def getInstance(
    filename: String,
    referenceColor: Array[Byte],
    maxDistance: Int,
    traceCloseToColor: Boolean): IEdgeTracer = {
    val imageTry = imageLoad.loadBufferImageTry(filename)
    imageTry match {
      case Success(image) => {
        val edgeTracer = EdgeTracer(image, maxDistance, traceCloseToColor)
        edgeTracer.setReferencePointArray(referenceColor)
        edgeTracer
      }
      case Failure(ex) => {
        throw ex
      }
    }
  }

}
/**
 * Test of EdgeTracer.<br />
 *
 * @author Sami Badawi
 *
 */
class EdgeTracerSpec extends FunSuite with BeforeAndAfterEach {
  import EdgeTracerSpec._

  val boxPerimeter: Double = 20
  val wholeBobPerimiter: Double = 36.0
  val iPerimeter: Double = 54.0

  test("Redbox") {
    val filename = "redbox"
    val imageTry: Try[BufferImage[Byte]] = imageLoad.loadBufferImageTry(imageLoad.filePath(filename, ".png"))
    assert(imageTry.isSuccess)
    val image: BufferImage[Byte] = imageTry.get
    val foregroundColorInt: Int = 0xff0000
    val foregroundColor: Array[Byte] = Array(0, 0, -1)
    val backgroundColor: Array[Byte] = Array(-1, -1, -1)
    val foregroundColorClose: Array[Byte] = Array(0, 0, 254.toByte) // 0xfe0000
    assert(image != null)
    assertResult(10) { image.width }
    assertResult(10) { image.height }
    assertResult(3) { image.numBands }
    assertResult(backgroundColor.toSeq) { image.getPixel(0, 0).toSeq } //Background unmasked
    assertResult(foregroundColor.toSeq) { image.getPixel(2, 2) } //Foreground
    println(s"start edgeTracer")
    val edgeTracer: IEdgeTracer = EdgeTracer.fromBufferImage(image, foregroundColorClose, 10, true)
    println(s"end edgeTracer")
    val cch: Polygon = edgeTracer.autoOutline(5, 2)
    assertResult(boxPerimeter) { cch.getPerimeter() }
    assertResult(true) { cch.getAnnotatedShape() != null }
    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER).size }
    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(LineType.STRAIGHT).size }
    printAnnotaions(cch.getAnnotatedShape())
    println(s"end of unit test")
  }

  test("RedboxInverse") {
    val filename = "redbox"
    val imageTry: Try[BufferImage[Byte]] = imageLoad.loadBufferImageTry(imageLoad.filePath(filename, ".png"))
    assert(imageTry.isSuccess)
    val image: BufferImage[Byte] = imageTry.get
    val foregroundColorInt: Int = 0xff0000
    val foregroundColor: Array[Byte] = Array(0, 0, -1)
    val backgroundColor: Array[Byte] = Array(-1, -1, -1)
    val foregroundColorClose: Array[Byte] = Array(0, 0, 254.toByte) // 0xfe0000

    assert(image != null)
    assertResult(10) { image.width }
    assertResult(10) { image.height }
    assertResult(3) { image.numBands }
    assertResult(backgroundColor.toSeq) { image.getPixel(0, 0).toSeq } //Background unmasked
    assertResult(foregroundColor.toSeq) { image.getPixel(2, 2).toSeq } //Foreground
    val edgeTracer: IEdgeTracer = EdgeTracer.fromBufferImage(image, foregroundColorClose, 10, false)
    val cch: Polygon = edgeTracer.autoOutline(1, 1)
    assertResult(wholeBobPerimiter) { cch.getPerimeter() }
    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER).size }
    //    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(LineType.STRAIGHT).size } // XXX enable again
    printAnnotaions(cch.getAnnotatedShape())
  }

  test("Blackbox") {
    val filename = "blackbox"
    val imageTry: Try[BufferImage[Byte]] = imageLoad.loadBufferImageTry(imageLoad.filePath(filename, ".png"))
    if (imageTry.isFailure) {
      imageTry.failed.get.printStackTrace()
    }
    assert(imageTry.isSuccess)
    val image: BufferImage[Byte] = imageTry.get
    assert(image != null)
    assertResult(10) { image.width }
    assertResult(10) { image.height }
    assertResult(1) { image.numBands }
    val foregroundColor: Array[Byte] = Array(-1) // XXX Black is 255
    val backgroundColor: Array[Byte] = Array(0) // XXX White is 0
    assertResult(backgroundColor.toSeq) { image.getPixel(0, 0) } //Background
    assertResult(foregroundColor.toSeq) { image.getPixel(2, 2).toSeq } //Foreground
    val edgeTracer: IEdgeTracer = EdgeTracer.fromBufferImage(image, foregroundColor, 10, true)
    val cch: Polygon = edgeTracer.autoOutline(5, 5)
    //if 5,5 was used as start point a soft point would have been found
    assertResult(boxPerimeter) { cch.getPerimeter() }
  }

  test("I") {
    val filename = "I"
    val imageTry: Try[BufferImage[Byte]] = imageLoad.loadBufferImageTry(s"./src/test/resources/data/images/smallThinLetters/$filename.png")
    if (imageTry.isFailure) {
      imageTry.failed.get.printStackTrace()
    }
    val image: BufferImage[Byte] = imageTry.get
    assert(image != null)
    assertResult(30) { image.width }
    assertResult(30) { image.height }
    //XXX this version of png is a RGB some are index byte
    assertResult(3) { image.numBands }
    val foregroundColor: Array[Byte] = Array(0, 0, 0)
    val backgroundColor: Array[Byte] = Array(-1, -1, -1)
    assertResult(backgroundColor.toSeq) { image.getPixel(0, 0) } //Background
    assertResult(foregroundColor.toSeq) { image.getPixel(14, 2).toSeq } //Foreground
    val edgeTracer: IEdgeTracer = EdgeTracer.fromBufferImage(image, foregroundColor, 10, true)
    val cch: Polygon = edgeTracer.autoOutline(14, 2)
    assertResult(iPerimeter) { cch.getPerimeter() }
  }

  def printAnnotaions(annotatedShape: AnnotatedShape): Unit = {
    println("Print annotations:")
    val map = annotatedShape.getMap() //Map<Object, Set<GeometricShape2D>>
    map.foreach { (entry) =>
      println(entry._1 + ":\n" + entry._2)
    }
  }

  test("NegativeModulus") {
    assertResult(-4) { -4 % 8 }
  }

  def assertClose(expected: Double, actual: Double): Unit = {
    val absolute: Double = 1
    val difference: Double = Math.abs(expected - actual)
    val far: Boolean = absolute < difference
    if (far) {
      println("Expected: " + expected + " not close to actual: " + actual)
      assert(false)
    }
  }
}