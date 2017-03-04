package org.shapelogic.sc.imageprocessing

import java.awt.Rectangle
import java.util.ArrayList

import org.shapelogic.sc.polygon.CLine
import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.polygon.IPoint2D
import org.shapelogic.sc.polygon.MultiLinePolygon
import org.shapelogic.sc.polygon.Polygon
import org.shapelogic.sc.polygon.PolygonEndPointAdjuster
import org.shapelogic.sc.util.Constants
import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import scala.collection.mutable.ArrayBuffer
import org.shapelogic.sc.streams.LazyPlugInFilter
import org.shapelogic.sc.streams.ListStream
import scala.collection.mutable.Set
import org.shapelogic.sc.pixel.PixelSimilarity
import org.shapelogic.sc.pixel.PixelIdentity

/**
 * Input image needs to be binary, that is gray scale with inverted LUT.
 *
 * That the background is white, 0, and the foreground is black, 255.
 *
 * When it handles a point it will mark a point as used and what type it has.
 * I put the result in a polygon.
 *
 * How do I know where to start?
 * I will start taking the pixels until I meet the first black pixel,
 * and I will work out from there.
 *
 * Cycle starting at (1,0) to (1,1) to (0,1) so in what would normally be
 * counter clockwise, but since the coordinate system is turned upside down it
 * is clockwise
 *
 * Terminology:
 *
 * Current: the short line that is currently handled
 *
 * Short line: there are 2 pieces, a short line at the end and everything before that
 *
 * @author Sami Badawi
 *
 */
abstract class BaseVectorizer(val image: BufferImage[Byte])
    extends PixelFollowSimilarity[Byte](image, similarIsMatch = true)
    with IPixelTypeFinder
    with LazyPlugInFilter[Polygon]
    with Iterator[Polygon] {
  import BaseVectorizer._

  val MAX_DISTANCE_BETWEEN_CLUSTER_POINTS: Int = 2
  val STRAIGHT_LINE_COLOR: Byte = 127 //Draw color
  override def margin: Int = 0

  lazy val inputImage: BufferImage[Byte] = image

  /**
   * Since the algorithm is mutating the image outputImage is a good place to have this calculation
   */
  lazy val outputImage: BufferImage[Byte] = inputImage.copy()

  lazy val refColor: Byte = 0 //XXX not sure about this
  lazy val pixelDistance: PixelSimilarity = new PixelIdentity[Byte](image, refColor = refColor)

  //Image related
  lazy val _pixels: Array[Byte] = image.data

  //Half static
  /** What you need to add to the the index in the pixels array to get to the indexed point */

  /** last point where you are */
  var _currentPoint: CPointInt = null
  var _firstPointInMultiLine: CPointInt = null
  val _pixelTypeCalculator = new PixelTypeCalculator()
  var _unfinishedPoints = new ArrayBuffer[CPointInt]()
  var _polygon: Polygon = null
  var _cleanedupPolygon: Polygon = null
  /** this is the index into the _pixels where the current point is */
  var _currentPixelIndex: Int = 0

  var _currentDirection: Byte = Constants.DIRECTION_NOT_USED

  var _numberOfPointsInAllLines: Int = 0
  var _matchingOH: Object = null

  var _errorMessage: String = null

  var _endPointsClusters: ArrayBuffer[Set[IPoint2D]] = new ArrayBuffer[Set[IPoint2D]]()
  var _firstPointInLineIndex: Int = 0
  var _pixelTypeFinder: IPixelTypeFinder = new PriorityBasedPixelTypeFinder(image)
  //	var  _rulesArrayForLetterMatching: Array[NumericRule] = null

  var _stream: ListStream[Polygon] = null

  /** Really stream name but could be changed to _name. */
  var _streamName: String = null

  var _yForUnporcessedPixel: Int = 0
  var _nextCount: Int = 0
  var _displayInternalInfo: Boolean = false

  //  override 
  def run(): Unit = {
    next()
    matchLines()
  }

  def cleanPolygon(): Unit = {
    val adjuster: PolygonEndPointAdjuster = new PolygonEndPointAdjuster(getPolygon())
    _cleanedupPolygon = adjuster.getValue()
    _cleanedupPolygon = _cleanedupPolygon.improve()
  }

  /**
   * could be overridden to show in GUI
   */
  def showMessage(title: String, text: String): Unit = {
    println(title + "\n" + text + "\n")
  }

  /** This does really not belong in a vectorizer. */
  def matchLines(): Unit = {
  }

  def findAllLines(): Unit = {
    findFirstLinePoint(true)

    while (_unfinishedPoints.size > 0) {
      //Find whole line
      findMultiLine()
    }
  }

  def findMultiLine(): Unit

  def handleProblematicPoints(): Byte

  /** Not background and the used bit set to 0 */
  def isPixelUsed(pixelIndex: Int): Boolean = {
    val pixel: Byte = _pixels(pixelIndex)
    PixelType.isUsed(pixel)
  }

  def findPointType(pixelIndex: Int, reusedPixelTypeCalculator: PixelTypeCalculator): PixelTypeCalculator = {
    _pixelTypeFinder.findPointType(pixelIndex, reusedPixelTypeCalculator)
  }

  def moveCurrentPointForwards(newDirection: Byte): Unit = {
    val startPixelValue: Byte = _pixels(_currentPixelIndex)
    val oldVal = PixelType.getPixelType(_pixels(_currentPixelIndex))
    val newVal = PixelType.toUsed(startPixelValue) & 255
    _pixels(_currentPixelIndex) = newVal.toByte
    _currentPixelIndex += cyclePoints(newDirection)
    _currentPoint.x += Constants.CYCLE_POINTS_X(newDirection)
    _currentPoint.y += Constants.CYCLE_POINTS_Y(newDirection)
    _currentDirection = newDirection
    if (verboseLogging) {
      println(s"_currentPixelIndex: ${_currentPixelIndex} from ${oldVal.name} ${oldVal.colorInt} to $newVal _currentPoint: ${_currentPoint}")
    }
  }

  def lastPixelOk(newDirection: Byte): Boolean

  /** All the objects that needs special version should be created here. */
  def internalFactory(): Unit

  def pointToPixelIndex(x: Int, y: Int): Int = {
    image.getIndex(x, y)
  }

  def pointToPixelIndex(point: IPoint2D): Int = {
    image.getIndex(point.getX().toInt, point.getY().toInt)
  }

  def pixelIndexToPoint(pixelIndex: Int): CPointInt = {
    val y = pixelIndex / image.width
    val x = pixelIndex % image.width
    new CPointInt(x, y)
  }

  /**
   * Find first point that is not a unused foreground point.
   * <br />
   * XXX Currently start from the beginning if called multiple time, change that.
   */
  def findFirstLinePoint(process: Boolean): Boolean = {
    val pixelCount = image.pixelCount
    val startY: Int = Math.max(yMin, _yForUnporcessedPixel)
    cfor(startY)(_ <= yMax, _ + 1) { iY =>
      val lineOffset: Int = image.width * iY
      cfor(xMin)(_ <= xMax, _ + 1) { iX =>
        //        _currentPixelIndex = lineOffset + iX
        _currentPixelIndex = image.getIndex(iX, iY)
        if (verboseLogging && pixelCount <= _currentPixelIndex)
          println(s"Out of range: iX: $iX, iY: $iY, yMax: ${yMax}")
        if (PixelType.PIXEL_FOREGROUND_UNKNOWN.color == _pixels(_currentPixelIndex)) {
          _yForUnporcessedPixel = iY
          if (process) {
            _currentPoint = new CPointInt(iX, iY)
            if (verboseLogging)
              println(s"findFirstLinePoint: $iX, $iY")
            addToUnfinishedPoints(_currentPoint.copy().asInstanceOf[CPointInt])
          }
          return true
        }
      }
    }
    false
  }

  /**
   * A normal line has a crossing index of 4.
   */
  def countRegionCrossingsAroundPoint(pixelIndex: Int): Int = {
    var countRegionCrossings: Int = 0
    var isBackground: Boolean = false
    var wasBackground: Boolean = PixelType.BACKGROUND_POINT.color == _pixels(pixelIndex + cyclePoints(Constants.DIRECTIONS_AROUND_POINT - 1))
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { i =>
      isBackground = PixelType.BACKGROUND_POINT.color == _pixels(pixelIndex + cyclePoints(i))
      if (wasBackground != isBackground) {
        countRegionCrossings += 1
      }
      wasBackground = isBackground
    }
    countRegionCrossings
  }

  /** To be overridden. If I want to do more matching at the end. */
  def findMultiLinePostProcess(): Unit = {
    _pixels(_currentPixelIndex) = PixelType.toUsed(_pixels(_currentPixelIndex)) //Last point
    getPolygon().endMultiLine()
  }

  /**
   * @return true = continue; false = done
   */
  def findMultiLinePreProcess(): Boolean = {
    while (!_unfinishedPoints.isEmpty) {
      if (_unfinishedPoints.isEmpty) {
        if (verboseLogging)
          println("findMultiLinePreProcess() _unfinishedPoints.isEmpty no more points")
        false
      }
      _currentDirection = Constants.DIRECTION_NOT_USED
      getPolygon().startMultiLine()
      //    findFirstLinePoint(process = true) //XXX
      _currentPoint = _unfinishedPoints(_unfinishedPoints.size - 1)
      _currentPoint = _currentPoint.copy().asInstanceOf[CPointInt]
      _firstPointInMultiLine = _currentPoint.copy().asInstanceOf[CPointInt]
      _currentPixelIndex = pointToPixelIndex(_currentPoint)
      findPointType(_currentPixelIndex, _pixelTypeCalculator)
      _pixelTypeCalculator.getValue()
      val firstPointDone: Boolean = (_pixelTypeCalculator.unusedNeighbors == 0) //Take first step so you can set the first point to unused
      if (firstPointDone) {
        _unfinishedPoints.-=(_currentPoint)
        if (verboseLogging)
          println(s"Remove ${_currentPoint}, left: ${_unfinishedPoints}")
      } else {
        if (verboseLogging)
          println(s"Use ${_currentPoint}")
        return true
      }
    }
    false
  }

  def addToUnfinishedPoints(newPoint: CPointInt): Unit = {
    if (_unfinishedPoints.indexOf(newPoint) == -1) {
      if (verboseLogging)
        println(s"Add to _unfinishedPoints: $newPoint")
      _unfinishedPoints.append(newPoint)
    }
  }

  def getPoints(): Set[IPoint2D] = { //Collection<IPoint2D> 
    getPolygon().getValue()
    getPolygon().getPoints()
  }

  def drawLine(line: CLine): Unit = {
  }

  def polygonFactory(): Polygon = {
    new MultiLinePolygon(null) //XXX
  }

  def getPolygon(): Polygon = {
    if (_polygon == null)
      _polygon = polygonFactory()
    _polygon
  }

  override def getStream(): ListStream[Polygon] = {
    if (_stream == null)
      _stream = null //StreamFactory.createListStream(this)
    _stream
  }

  override def hasNext(): Boolean = {
    findFirstLinePoint(false)
  }

  /** Currently returns the cleaned up polygons. */
  override def next(): Polygon = {
    _nextCount += 1
    _polygon = null //Cause lazy creation of a new polygon
    findAllLines()
    if (_currentPoint != null) {
      if (_nextCount == 1 && !_displayInternalInfo) //XXX maybe make a better logging system or take out
        showMessage(getClass().getSimpleName(),
          "Last line point is: " + _currentPoint + "\n" +
            "_numberOfPointsInLine: " + _numberOfPointsInAllLines + "\n" +
            "Points count: " + getPoints().size)
    } else
      showMessage(getClass().getSimpleName(), "No line point found.")
    cleanPolygon()
    getCleanedupPolygon()
  }

  def getMatchingOH(): Object = {
    _matchingOH
  }

  def getErrorMessage(): String = {
    _errorMessage
  }

  def getCleanedupPolygon(): Polygon = {
    _cleanedupPolygon
  }

  /** Really stream name but could be changed to _name. */
  override def getStreamName(): String = {
    _streamName
  }

  /** Really stream name but could be changed to _name. */
  override def setStreamName(name: String): Unit = {
    _streamName = name
  }
}

object BaseVectorizer {
  def oppesiteDirection(direction: Byte): Byte = {
    val newDirection: Byte = (direction + Constants.DIRECTIONS_AROUND_POINT / 2).toByte
    (newDirection % Constants.DIRECTIONS_AROUND_POINT).toByte
  }
}