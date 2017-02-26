package org.shapelogic.sc.imageprocessing

import java.util.Collection

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.polygon.CLine
import org.shapelogic.sc.polygon.IPoint2D

import org.scalatest._
import org.shapelogic.sc.io.BufferImageFactory
import org.shapelogic.sc.factory.ImageLoad

//import org.shapelogic.sc.imageutil.ImageUtil._

/**
 *
 * @author Sami Badawi
 *
 */
class LineVectorizerSpec extends FunSuite with BeforeAndAfterEach {

  val _fileFormat: String = ".gif"
  lazy val imageLoad = new ImageLoad(
    baseDir = "./src/test/resources/data/images",
    imageDir = "smallThinShapes",
    _fileFormat = ".gif",
    useJavaFXImage = false)

  def runPluginFilterOnImage(filepathIn: String): (BufferImage[Byte], LineVectorizer) = {
    val bufferImage: BufferImage[Byte] = imageLoad.loadBufferImage(filepathIn)
    val lineVectorizer = new LineVectorizer(bufferImage)
    (bufferImage, lineVectorizer)
  }

  test("ShortVertical") {
    val fileName = "vertical"
    val fullFilePath = imageLoad.filePath(fileName, ".bmp")
    var (bp, lineVectorizer) = runPluginFilterOnImage(fullFilePath)
    assertResult(20) { bp.width }
    assertResult(1) { bp.numBands }
    val pixelB = bp.getPixel(0, 0)
    val background: Array[Byte] = Array(0)
    assertResult(background.toSeq) { pixelB.toSeq }
    val pixelF = bp.getPixel(1, 1)
    val foreground: Array[Byte] = Array(-1)
    assertResult(foreground.toSeq) { pixelF.toSeq }
    lineVectorizer.internalFactory() //XXX not sure if this is the right one
    lineVectorizer.findAllLines() //XXX not sure if this is the right one
    val points = lineVectorizer.getPoints()
    assertResult(2) { points.size }
    val lines = lineVectorizer.getPolygon().getLines()
    assertResult(1) { lines.size }
  }

  test("ShortVerticalAndHorizontal") {
    val fileName = "verticalAndHorizontal"
    val fullFilePath = imageLoad.filePath(fileName, ".bmp")
    var (bp, lineVectorizer) = runPluginFilterOnImage(fullFilePath)
    assertResult(20) { bp.width }
    val pixel = bp.getPixel(0, 0)
    val background: Array[Byte] = Array(0)
    assertResult(background.toSeq) { pixel.toSeq }
    lineVectorizer.findAllLines()
    val points = lineVectorizer.getPoints()
    assertResult(3) { points.size }
    val lines = lineVectorizer.getPolygon().getLines()
    assertResult(2) { lines.size }
  }

  test("ShortRotatedTThin") {
    val fileName = "rotatedT"
    val fullFilePath = imageLoad.filePath(fileName, ".bmp")
    var (bp, lineVectorizer) = runPluginFilterOnImage(fullFilePath)
    assertResult(20) { bp.width }
    val pixelB = bp.getPixel(0, 0)
    val background: Array[Byte] = Array(0)
    assertResult(background.toSeq) { pixelB.toSeq }
    lineVectorizer.findAllLines()
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
    val fullFilePath = imageLoad.filePath(fileName, ".bmp")
    var (bp, lineVectorizer) = runPluginFilterOnImage(fullFilePath)
    assertResult(20) { bp.width }
    val pixelB = bp.getPixel(0, 0)
    val background: Array[Byte] = Array(0)
    assertResult(background.toSeq) { pixelB.toSeq }
    lineVectorizer.findAllLines()
    val points = lineVectorizer.getPoints()
    assertResult(3) { points.size }
    val lines = lineVectorizer.getPolygon().getLines()
    assertResult(2) { lines.size }
    //		assertResult("L",lineVectorizer.getMatchingOH())
  }

}
