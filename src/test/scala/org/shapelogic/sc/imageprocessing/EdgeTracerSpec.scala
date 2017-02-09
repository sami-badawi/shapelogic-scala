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

object EdgeTracerSpec {

  val _dirURL = "./src/test/resources/data/images/particles/";
  val _fileFormat = ".gif";

  def filePath(fileName: String): String = {
    _dirURL + "/" + fileName + _fileFormat;
  }

  def filePath(fileName: String, fileFormat: String): String = {
    return _dirURL + "/" + fileName + fileFormat;
  }

  def filePath(dir: String, fileName: String, fileFormat: String): String = {
    return dir + "/" + fileName + fileFormat;
  }

  /**
   * Should maybe be moved so it can be used globally
   */
  def choseBufferImageFactory(useJavaFXImage: Boolean): BufferImageFactory[Byte] = {
    if (useJavaFXImage)
      LoadJFxImage
    else
      LoadImage
  }

  lazy val bufferImageFactory: BufferImageFactory[Byte] = choseBufferImageFactory(useJavaFXImage = false)

  def loadImageTry(filename: String): Try[BufferImage[Byte]] = {
    bufferImageFactory.loadBufferImageTry(filename)
  }

  def getInstance(
    filename: String,
    referenceColor: Array[Byte],
    maxDistance: Double,
    traceCloseToColor: Boolean): IEdgeTracer = {
    val imageTry = loadImageTry(filename)
    imageTry match {
      case Success(image) => {
        val edgeTracer = new EdgeTracer(image, maxDistance, traceCloseToColor)
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

  val boxPerimeter: Double = 17.656854249492383; //
  val iPerimeter: Double = 54.0

  test("Redbox") {
    val filename = "redbox";
    println(s"start imageTry")
    val imageTry: Try[BufferImage[Byte]] = loadImageTry(filePath(filename, ".png"))
    println(s"end imageTry")
    assert(imageTry.isSuccess)
    val image: BufferImage[Byte] = imageTry.get
    val foregroundColorInt: Int = 0xff0000;
    val foregroundColor: Array[Byte] = Array(0, 0, -1)
    val backgroundColor: Array[Byte] = Array(-1, -1, -1)
    val foregroundColorClose: Array[Byte] = Array(0, 0, 254.toByte) // 0xfe0000;
    assert(image != null)
    assertResult(10) { image.width }
    assertResult(10) { image.height }
    //    assert(image.isRgb())
    assertResult(backgroundColor.toSeq) { image.getPixel(0, 0).toSeq } //Background unmasked
    //    assertResult(0xffffff, image.getPixel(0, 0) & 0xffffff); //Background
    assertResult(foregroundColor.toSeq) { image.getPixel(2, 2) } //Foreground
    println(s"start edgeTracer")
    val edgeTracer: IEdgeTracer = EdgeTracer.fromBufferImage(image, foregroundColorClose, 10, true)
    println(s"end edgeTracer")
    val cch: Polygon = edgeTracer.autoOutline(5, 2)
    //if 5,5 was used as start point a soft point would have been found
    assertResult(boxPerimeter) { cch.getPerimeter() }
    //    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER).size() }
    //    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(LineType.STRAIGHT).size() }
    //    printAnnotaions(cch.getAnnotatedShape());
    println(s"end of unit test")
  }

  //  test("RedboxInverse") {
  //    String filename = "redbox";
  //    val image: BufferImage[Byte] = null //loadImage(filePath(filename,".png"));
  //    int foregroundColor = 0xff0000;
  //    int backgroundColorClose = 0xfffeff;
  //    assertNotNull(image);
  //    assertFalse(image.isEmpty());
  //    assertResult(10, image.getWidth());
  //    assertResult(10, image.getHeight());
  //    assert(image.isRgb());
  //    assertResult(-1) { image.getPixel(0, 0) } //Background unmasked
  //    assertResult(0xffffff) { image.getPixel(0, 0) } //Background
  //    assertResult(foregroundColor) { image.getPixel(2, 2) } //Foreground
  //    IEdgeTracer edgeTracer = getInstance(image, backgroundColorClose, 10, false);
  //    Polygon cch = edgeTracer.autoOutline(5, 2);
  //    //if 5,5 was used as start point a soft point would have been found
  //    assertResult(boxPerimeter, cch.getPerimeter());
  //    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER).size() }
  //    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(LineType.STRAIGHT).size() }
  //    printAnnotaions(cch.getAnnotatedShape());
  //  }
  //
  //  test("Blackbox") {
  //    val filename = "blackbox";
  //    val image: BufferImage[Byte] = null // = loadImage(filePath(filename));
  //    int foregroundColor = 255;
  //    assertNotNull(image);
  //    assertFalse(image.isEmpty());
  //    assertResult(10, image.getWidth());
  //    assertResult(10, image.getHeight());
  //    assert(image.isGray());
  //    assertResult(0, image.getPixel(0, 0)); //Background
  //    assertResult(foregroundColor, image.getPixel(2, 2)); //Foreground
  //    IEdgeTracer edgeTracer = getInstance(image, foregroundColor, 10, true);
  //    Polygon cch = edgeTracer.autoOutline(5, 5);
  //    assertClose(boxPerimeter, cch.getPerimeter());
  //  }
  //
  //  public void testI() {
  //    String filename = "I";
  //    val image: BufferImage[Byte] = null // loadImage(filePath("./src/test/resources/images/smallThinLetters", filename, ".gif"));
  //    int foregroundColor = 255;
  //    assertNotNull(image);
  //    assertFalse(image.isEmpty());
  //    assertResult(30, image.getWidth());
  //    assertResult(30, image.getHeight());
  //    assert(image.isGray());
  //    assertResult(0, image.getPixel(0, 0)); //Background
  //    assertResult(foregroundColor, image.getPixel(14, 2)); //Foreground
  //    IEdgeTracer edgeTracer = getInstance(image, foregroundColor, 10, true);
  //    Polygon cch = edgeTracer.autoOutline(14, 2);
  //    assertResult(iPerimeter, cch.getPerimeter());
  //  }
  //
  //  def printAnnotaions(annotatedShape: AnnotatedShape): Unit = {
  //    println("Print annotations:");
  //    val map = annotatedShape.getMap(); //Map<Object, Set<GeometricShape2D>>
  //    map.foreach { (entry) =>
  //      System.out.println(entry.getKey() + ":\n" + entry.getValue());
  //    }
  //  }
  //
  //  test("NegativeModulus") {
  //    assertResult(-4) { -4 % 8 }
  //  }
  //
  //  def assertClose(expected: Double, actual: Double): Unit = {
  //    val absolute: Double = 1;
  //    val difference: Double = Math.abs(expected - actual);
  //    val far: Boolean = absolute < difference;
  //    if (far) {
  //      println("Expected: " + expected + " not close to actual: " + actual)
  //      assert(false);
  //    }
  //  }
}