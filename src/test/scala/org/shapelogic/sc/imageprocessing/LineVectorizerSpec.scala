package org.shapelogic.sc.imageprocessing

import java.util.Collection

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.polygon.CLine
import org.shapelogic.sc.polygon.IPoint2D

import org.scalatest._
import org.shapelogic.sc.io.BufferImageFactory

//import org.shapelogic.sc.imageutil.ImageUtil._

/**
 *
 * @author Sami Badawi
 *
 */
class LineVectorizerSpec extends FunSuite with BeforeAndAfterEach {
  //extends AbstractImageProcessingTests {

  val _dirURL = "./src/test/resources/images/smallThinShapes"
  val _fileFormat = ".gif"

  lazy val bufferImageFactory: BufferImageFactory[Byte] = EdgeTracerSpec.choseBufferImageFactory(useJavaFXImage = false)

  //	override
  //	protected void setUp() throws Exception {
  //		super.setUp()
  //		_dirURL = "./src/test/resources/images/smallThinShapes"
  //		_fileFormat = ".gif"
  //	}

  def runPluginFilterOnImage(filepath: String): (BufferImage[Byte], LineVectorizer) = {
    val bufferImage: BufferImage[Byte] = bufferImageFactory.loadBufferImage(filepath)
    val lineVectorizer = new LineVectorizer(bufferImage)
    (bufferImage, lineVectorizer)
  }

  def filePath(fileName: String): String = {
    _dirURL + "/" + fileName + _fileFormat
  }

  test("ShortVertical") {
    val fileName = "vertical"
    var (bp, lineVectorizer) = runPluginFilterOnImage(filePath(fileName))
    assertResult(20) { bp.width }
    val pixel = bp.getPixel(0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = lineVectorizer.getPoints()
    assertResult(2) { points.size }
    val lines = lineVectorizer.getPolygon().getLines()
    assertResult(1) { lines.size }
  }

  test("ShortVerticalAndHorizontal") {
    val fileName = "verticalAndHorizontal"
    var (bp, lineVectorizer) = runPluginFilterOnImage(filePath(fileName))
    assertResult(20) { bp.width }
    val pixel = bp.getPixel(0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = lineVectorizer.getPoints()
    assertResult(3) { points.size }
    val lines = lineVectorizer.getPolygon().getLines()
    assertResult(2) { lines.size }
  }

  test("ShortRotatedTThin") {
    val fileName = "rotatedT"
    var (bp, lineVectorizer) = runPluginFilterOnImage(filePath(fileName))
    assertResult(20) { bp.width }
    val pixel = bp.getPixel(0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    assertResult(2) { lineVectorizer.countRegionCrossingsAroundPoint(lineVectorizer.pointToPixelIndex(1, 1)) }
    assertResult(4) { lineVectorizer.countRegionCrossingsAroundPoint(lineVectorizer.pointToPixelIndex(1, 2)) }
    assertResult(4) { lineVectorizer.countRegionCrossingsAroundPoint(lineVectorizer.pointToPixelIndex(1, 7)) }
    //T-junction
    assertResult(6) { lineVectorizer.countRegionCrossingsAroundPoint(lineVectorizer.pointToPixelIndex(1, 8)) }
    val points = lineVectorizer.getPoints()
    assertResult(4) { points.size }
    val lines = lineVectorizer.getPolygon().getLines()
    assertResult(3) { lines.size }
  }

  test("ThinProblematicL") {
    val fileName = "problematicL"
    var (bp, lineVectorizer) = runPluginFilterOnImage(filePath(fileName))
    assertResult(20) { bp.width }
    val pixel = bp.getPixel(0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = lineVectorizer.getPoints()
    assertResult(3) { points.size }
    val lines = lineVectorizer.getPolygon().getLines()
    assertResult(2) { lines.size }
    //		assertResult("L",lineVectorizer.getMatchingOH())
  }

}
