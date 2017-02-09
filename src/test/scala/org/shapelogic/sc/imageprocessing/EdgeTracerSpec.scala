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

  val useJavaFXImage = false

  def loadImage(filename: String): BufferImage[Byte] = {
    if (useJavaFXImage)
      LoadJFxImage.loadBufferImage(filename)
    else
      org.shapelogic.sc.io.LoadImage.loadBufferImage(filename).get
  }

  def getInstance(
    filename: String,
    referenceColor: Array[Byte],
    maxDistance: Double,
    traceCloseToColor: Boolean): IEdgeTracer = {
    val image = loadImage(filename)
    val edgeTracer = new EdgeTracer(image, maxDistance, traceCloseToColor)
    edgeTracer.setReferencePointArray(referenceColor)
    edgeTracer
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

  ignore("Redbox") {
    val filename = "redbox";
    val image: BufferImage[Byte] = loadImage(filePath(filename, ".png"));
    val foregroundColorInt: Int = 0xff0000;
    val foregroundColor: Array[Byte] = Array(0, 0, -1)
    val backgroundColor: Array[Byte] = Array(-1, -1, -1)
    val foregroundColorClose: Array[Byte] = Array(0, 0, 254.toByte, -1) // 0xfe0000;
    assert(image != null);
    //  		assert(!image.isEmpty());
    assertResult(10) { image.width }
    assertResult(10) { image.height }
    //    assert(image.isRgb())
    assertResult(backgroundColor.toSeq) { image.getPixel(0, 0).toSeq } //Background unmasked
    //    assertResult(0xffffff, image.getPixel(0, 0) & 0xffffff); //Background
    assertResult(foregroundColor.toSeq) { image.getPixel(2, 2) } //Foreground
    val edgeTracer: IEdgeTracer = EdgeTracer.fromBufferImage(image, foregroundColorClose, 10, true)
    val cch: Polygon = edgeTracer.autoOutline(5, 2)
    //if 5,5 was used as start point a soft point would have been found
    assertResult(boxPerimeter) { cch.getPerimeter() }
    //    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER).size() }
    //    assertResult(4) { cch.getAnnotatedShape().getShapesForAnnotation(LineType.STRAIGHT).size() }
    //    printAnnotaions(cch.getAnnotatedShape());
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