package org.shapelogic.sc.imageprocessing

import java.util.Collection

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.polygon.CLine
import org.shapelogic.sc.polygon.IPoint2D

import org.scalatest._
import org.shapelogic.sc.io.BufferImageFactory
import org.shapelogic.sc.polygon.MultiLinePolygon
import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.util.PointType
import org.shapelogic.sc.polygon.PolygonEndPointAdjuster
import org.shapelogic.sc.util.LineType
import org.shapelogic.sc.factory.ImageLoad

object MaxDistanceVectorizerSpec {
  lazy val imageLoad = new ImageLoad(
    baseDir = "./src/test/resources/data/images",
    imageDir = "smallThinShapes",
    _fileFormat = ".gif",
    useJavaFXImage = false)

  def runPluginFilterOnImage(filepath: String): (BufferImage[Byte], BaseMaxDistanceVectorizer) = {
    val image = imageLoad.loadBufferImage(filepath)
    val baseMaxDistanceVectorizer = new BaseMaxDistanceVectorizer(image)
    (image, baseMaxDistanceVectorizer)
  }

}

/**
 *
 * @author Sami Badawi
 *
 */
class MaxDistanceVectorizerSpec extends FunSuite with BeforeAndAfterEach {
  import MaxDistanceVectorizerSpec._
  import AbstractImageProcessingSpec._

  test("ShortVertical") {
    val fileName = "vertical"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    assertResult(20) { image.width }
    assertResult(1) { image.numBands }
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    maxDistanceVectorizer.findMultiLine()
    val points = maxDistanceVectorizer.getPoints()
    assertResult(2) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(1) { lines.size }
    val polygon: MultiLinePolygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(0) { polygon.getMultiLines().size }
    val line = lines.iterator.next
    polygon.getAnnotatedShape().getMap()
    val annotations = polygon.getAnnotatedShape().getAnnotationForShapes(line)
    assertResult(1) { annotations.size }
    assert(annotations.contains(LineType.STRAIGHT))
  }

  ignore("ShortVerticalArch") {
    val fileName = "verticalArch"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    assertResult(30) { image.width }
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assertResult(2) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(1) { lines.size }
    val polygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(0) { polygon.getMultiLines().size }
    val line = lines.iterator.next
    polygon.getAnnotatedShape().getMap()
    val annotations = polygon.getAnnotatedShape().getAnnotationForShapes(line)
    assertResult(1) { annotations.size }
    println(annotations)
    assert(annotations.contains(LineType.CURVE_ARCH))
  }

  ignore("ShortVerticalAndHorizontal") {
    val fileName = "verticalAndHorizontal"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    assertResult(20) { image.width }
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    val topPoint = new CPointInt(1, 1)
    val bottomPoint1 = new CPointInt(1, 17)
    val bottomPoint2 = new CPointInt(15, 17)
    assert(points.contains(topPoint))
    assert(points.contains(bottomPoint1))
    assert(points.contains(bottomPoint2))
    printPoints(maxDistanceVectorizer.getPolygon())
    assertResult(3) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(2) { lines.size }
    assertResult(3) { maxDistanceVectorizer.getPolygon().getEndPointsClusters().size }
    val polygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(1) { polygon.getMultiLines().size }
    assert(!polygon.getMultiLines()(0).isClosed())
    assertResult(null) { polygon.getMultiLines()(0).isClosedLineClockWise() }
    polygon.getAnnotatedShape().getMap()
    val annotations = polygon.getAnnotatedShape().getAnnotationForShapes(bottomPoint1)
    println(annotations)
    assertResult(1) { annotations.size }
    assert(annotations.contains(PointType.HARD_CORNER))
  }

  ignore("ShortRotatedTThin") {
    val fileName = "rotatedT"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    assertResult(20) { image.width }
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    printPolygon(maxDistanceVectorizer.getPolygon())
    assertResult(2) { maxDistanceVectorizer.countRegionCrossingsAroundPoint(maxDistanceVectorizer.pointToPixelIndex(1, 1)) }
    assertResult(4) { maxDistanceVectorizer.countRegionCrossingsAroundPoint(maxDistanceVectorizer.pointToPixelIndex(1, 2)) }
    assertResult(4) { maxDistanceVectorizer.countRegionCrossingsAroundPoint(maxDistanceVectorizer.pointToPixelIndex(1, 7)) }
    //T-junction
    assertResult(6) { maxDistanceVectorizer.countRegionCrossingsAroundPoint(maxDistanceVectorizer.pointToPixelIndex(1, 8)) }
    val points = maxDistanceVectorizer.getPoints()
    assertResult(4) { points.size }
    val topPoint = new CPointInt(1, 1)
    val middlePoint1 = new CPointInt(1, 8)
    val middlePoint2 = new CPointInt(16, 8)
    val bottomPoint = new CPointInt(1, 17)
    assert(points.contains(topPoint))
    assert(points.contains(middlePoint1))
    assert(points.contains(middlePoint2))
    assert(points.contains(bottomPoint))
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(3) { lines.size }
  }

  ignore("ThinProblematicL") {
    val fileName = "problematicL"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    assertResult(20) { image.width }
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assertResult(3) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(2) { lines.size }
    assertResult("L") { maxDistanceVectorizer.getMatchingOH() }
    val polygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(1) { polygon.getMultiLines().size }
    assertResult(false) { polygon.getMultiLines()(0).isClosed() }
    polygon.getAnnotatedShape().getMap()
    val shapes = polygon.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER)
    println(shapes)
    assertResult(1) { shapes.size }
  }

  ignore("ThinDiagonal") {
    val fileName = "diagonal"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assert(points != null)
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(1) { lines.size }
    val polygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(0) { polygon.getMultiLines().size }
    val line = lines.iterator.next
    polygon.getAnnotatedShape().getMap()
    val annotations = polygon.getAnnotatedShape().getAnnotationForShapes(line)
    assertResult(1) { annotations.size }
    println(annotations)
    assert(annotations.contains(LineType.STRAIGHT))
  }

  ignore("SmallThinTriangle") {
    val fileName = "triangle"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assert(null != points)
    printPoints(maxDistanceVectorizer.getPolygon())
    //		CPointInt topPoint1 = new CPointInt(1,3) 
    val topPoint2 = new CPointInt(2, 2)
    //		CPointInt bottomPoint1 = new CPointInt(1,27)  
    val bottomPoint2 = new CPointInt(2, 28)
    //		CPointInt bottomPoint3 = new CPointInt(26,28) 
    val bottomPoint4 = new CPointInt(27, 27)
    val crossingsForbottomPoint2 = maxDistanceVectorizer.countRegionCrossingsAroundPoint(
      maxDistanceVectorizer.pointToPixelIndex(bottomPoint2.x, bottomPoint2.y))
    assertResult(4) { crossingsForbottomPoint2 }
    //		assert(points.contains(topPoint1))
    assert(points.contains(topPoint2))
    //		assert(points.contains(bottomPoint1))
    assert(points.contains(bottomPoint2))
    //		assert(points.contains(bottomPoint3))
    assert(points.contains(bottomPoint4))
    assertResult(3) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(3) { lines.size }
    assertResult(3) { maxDistanceVectorizer.getPolygon().getEndPointsClusters().size }
    //		assertResult(3, maxDistanceVectorizer.getPolygon().getEndPointsMultiClusters().size)
    val polygon: MultiLinePolygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(1) { polygon.getMultiLines().size }
    assert(polygon.getMultiLines()(0).isClosed())
    val clusterAdjuster = new PolygonEndPointAdjuster(polygon)
    val clusteredPolygon = clusterAdjuster.getValue().asInstanceOf[MultiLinePolygon]
    //		assertNotSame(polygon, clusteredPolygon)
    assertResult(3) { clusteredPolygon.getPoints().size }
    assertResult(3) { clusteredPolygon.getLines().size }
    val adjustedTopPoint = new CPointInt(2, 2)
    val adjustedBottomPoint1 = new CPointInt(2, 28)
    val adjustedBottomPoint2 = new CPointInt(27, 27)
    val adjustedPoints = clusteredPolygon.getPoints()
    assert(adjustedPoints.contains(adjustedTopPoint))
    assert(adjustedPoints.contains(adjustedBottomPoint1))
    assert(adjustedPoints.contains(adjustedBottomPoint2))
    assert(polygon.getMultiLines()(0).isClosed())
    assert(null != polygon.getMultiLines())
    assert(polygon.getMultiLines()(0).isClosedLineClockWise())

    val line = lines.iterator.next
    polygon.getAnnotatedShape().getMap()
    val annotations = polygon.getAnnotatedShape().getAnnotationForShapes(line)
    println(annotations)
    assertResult(1) { annotations.size }
    assert(annotations.contains(LineType.STRAIGHT))

    polygon.getAnnotatedShape().getMap()
    val shapes = polygon.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER)
    println(shapes)
    assertResult(3) { shapes.size }
  }

  ignore("ThinLBracket") {
    val fileName = "LBracket"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assert(null != points)
    printPoints(maxDistanceVectorizer.getPolygon())
    val topPoint = new CPointInt(1, 1)
    val bottomPoint1 = new CPointInt(1, 27)
    val bottomPoint3 = new CPointInt(28, 28)
    assert(points.contains(topPoint))
    assert(points.contains(bottomPoint1))
    assert(points.contains(bottomPoint3))
    assertResult(3) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(2) { lines.size }
    val polygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(1) { polygon.getMultiLines().size }
    val clusterAdjuster = new PolygonEndPointAdjuster(polygon)
    val clusteredPolygon = clusterAdjuster.getValue().asInstanceOf[MultiLinePolygon]
    assertResult(3) { clusteredPolygon.getPoints().size }
    assertResult(2) { clusteredPolygon.getLines().size }
  }

  ignore("ElongatedX") {
    val fileName = "elongatedX"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assert(null != points)
    printPolygon(maxDistanceVectorizer.getPolygon())
    val topPoint1 = new CPointInt(1, 1)
    val bottomPoint1 = new CPointInt(1, 8)
    val topPoint2 = new CPointInt(7, 1)
    val bottomPoint2 = new CPointInt(7, 8)
    val midtTopPoint = new CPointInt(4, 4)
    val midtBottomPoint = new CPointInt(4, 5)
    assert(points.contains(topPoint1))
    assert(points.contains(midtTopPoint))
    assert(points.contains(bottomPoint1))
    assert(points.contains(topPoint2))
    assert(points.contains(bottomPoint2))
    assert(points.contains(midtBottomPoint))
    assertResult(6) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    assertResult(5) { lines.size }
  }

  ignore("ThinPlus") {
    val fileName = "plus"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assert(null != points)
    printPoints(maxDistanceVectorizer.getPolygon())
    val topPoint = new CPointInt(10, 1)
    val middlePoint = new CPointInt(10, 10)
    val leftPoint = new CPointInt(1, 10)
    val rightPoint = new CPointInt(18, 10)
    val bottomPoint = new CPointInt(10, 18)
    assert(points.contains(topPoint))
    assert(points.contains(bottomPoint))
    assert(points.contains(middlePoint))
    assert(points.contains(leftPoint))
    assert(points.contains(rightPoint))
    assertResult(5) { points.size }
    val lines = maxDistanceVectorizer.getPolygon().getLines()
    //		assertResult(3, lines.size)
  }

  /** This is an test for new images with problems, should only be used when there are problems*/
  ignore("BigCircle") {
    val fileName = "bigCircle"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assert(null != points)
    val polygon: MultiLinePolygon = maxDistanceVectorizer.getPolygon().asInstanceOf[MultiLinePolygon]
    assertResult(1) { polygon.getMultiLines().size }
    assert(polygon.getMultiLines()(0).isClosed())
    assert(polygon.getMultiLines()(0).isClosed())
    assert(null != polygon.getMultiLines())
    assert(polygon.getMultiLines()(0).isClosedLineClockWise())

    val lines = maxDistanceVectorizer.getPolygon().getLines()
    lines.foreach { (line: CLine) =>
      polygon.getAnnotatedShape().getMap()
      val annotations = polygon.getAnnotatedShape().getAnnotationForShapes(line)
      assert(annotations.contains(LineType.CURVE_ARCH))
    }

    polygon.getAnnotatedShape().getMap()
    val shapes = polygon.getAnnotatedShape().getShapesForAnnotation(PointType.HARD_CORNER)
    assert(shapes.isEmpty)
  }

  /** This is an test for new images with problems, should only be used when there are problems*/
  ignore("FailImage") {
    val fileName = "fail"
    val (image, maxDistanceVectorizer): (BufferImage[Byte], BaseMaxDistanceVectorizer) = runPluginFilterOnImage(imageLoad.filePath(fileName))
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val points = maxDistanceVectorizer.getPoints()
    assert(null != points)
    assertResult("N") { maxDistanceVectorizer.getMatchingOH() }
  }
}