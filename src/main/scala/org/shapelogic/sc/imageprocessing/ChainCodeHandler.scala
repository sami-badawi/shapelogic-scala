package org.shapelogic.sc.imageprocessing

import java.awt.Point
import java.util.ArrayList

import org.shapelogic.sc.calculation.CalcInvoke
import org.shapelogic.sc.polygon.AnnotatedShapeImplementation
import org.shapelogic.sc.polygon.BBox
import org.shapelogic.sc.polygon.BaseAnnotatedShape
import org.shapelogic.sc.polygon.CLine
import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.polygon.Calculator2D
import org.shapelogic.sc.polygon.IPoint2D
import org.shapelogic.sc.polygon.MultiLine
import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.util.DoubleCalculations
import org.shapelogic.sc.util.LineType
//import org.shapelogic.sc.util.PointType

import org.shapelogic.sc.polygon.Calculator2D.oppositeDirection
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ArrayBuffer
import org.shapelogic.sc.util.PointType

import spire.implicits._
import org.shapelogic.sc.util.PointType._
import org.shapelogic.sc.polygon.GeometricShape2D

/**
 * Chain Code For MultiLine.
 * This only works for one MultiLine so more than one is needed for MultiLinePolygon
 *
 * Assume that there is no intersection.
 *
 * @author Sami Badawi
 */
class ChainCodeHandler(annotatedShape: AnnotatedShapeImplementation) extends BaseAnnotatedShape(annotatedShape) with CalcInvoke[MultiLine] {
  val SQRT_2: Double = Math.sqrt(2)
  val CHAIN_CODE_FOR_MULTI_LINE_MAX_LENGTH: Int = 10000
  val SHORT_LINE_LENGTH: Int = 3
  val LIMIT_FOR_HARD_CORNER: Double = 60 * Math.PI / 180 //30 degrees

  var _chainCodeForMultiLine = new Array[Byte](CHAIN_CODE_FOR_MULTI_LINE_MAX_LENGTH)
  var _lastChain: Int = -1
  val _firstPoint = new CPointInt(-1, -1)
  var _lastPoint: CPointInt = null
  var _multiLine: MultiLine = null
  var _dirty: Boolean = true
  var _bBox: BBox = null
  val _pointMap: Map[Integer, CPointInt] = new HashMap[Integer, CPointInt]()
  var _accumulatedDirectionChange: Int = 0
  var _accumulatedAbsoluteDirectionChange: Int = 0
  val _diagonalElementCount: Int = 0

  /**
   * What should happen to the last point if the first and the last point is
   * the same.
   *
   * I think that there should be 2 points, in the list of points, but as far as
   * PointProperties I think that there should only be one, maybe I could just
   * use the same.
   *
   * How would that work?
   * I could have a map
   *
   * Another thing is if I count the number of point in a closed multi line
   * it will be one too many.
   */
  val _pointPropertiesList: ArrayBuffer[PointProperties] = new ArrayBuffer[PointProperties]()

  /**
   * Line number N should have end point on point number N.
   *
   * The first line should be null if the multi line is open
   * and from the last to the first if closed
   *
   * I could possibly also have the first LineProperties have an extra reference at the end
   */
  val _linePropertiesList: ArrayBuffer[LineProperties] = new ArrayBuffer()
  var _perimeter: Double = 0

  /**
   * This reset all the transient values, but not the startPoint.
   *
   * Needs to be called manually
   * So startPoint always need to be reset, if it is set in the first place.
   */
  override def setup(): Unit = {
    _lastChain = Constants.BEFORE_START_INDEX
    _lastPoint = new CPointInt(null)
    _multiLine = new MultiLine(null) //XXX should be synchronized with polygon
    _pointMap.clear()
    _dirty = true
    _bBox = new BBox(null)
    _accumulatedDirectionChange = 0
    _accumulatedAbsoluteDirectionChange = 0
    _perimeter = 0
    _pointPropertiesList.clear()
    _linePropertiesList.clear()
  }

  override def invoke(): MultiLine = {
    _pointMap.put(Constants.BEFORE_START_INDEX, _firstPoint)
    addPropertiesForNewPoint(Constants.BEFORE_START_INDEX, _firstPoint, null)
    _pointMap.put(_lastChain, _lastPoint.copy().asInstanceOf[CPointInt])
    handleInterval(Constants.BEFORE_START_INDEX, _lastChain)
    findAccumulatedDirectionChange()
    addPointsToMultiLine()
    handleClosedMultiLines()
    postProcessing()
    _multiLine.invoke()
    _dirty = false
    _multiLine
  }

  override def getValue(): MultiLine = {
    if (isDirty())
      invoke()
    _multiLine
  }

  /**
   * Extra passes to find more properties.
   *
   * This differs depending on whether the multi line is closed or open
   */
  def postProcessing(): Unit = {
    findChangeOfDirectionForLines()
    findConcaveArches()
    findCornerPoints()
    annotatePointsAndLines() //XXX put back
  }

  //find method part

  /**
   * Change of direction in multi line.
   *
   * The current line end at the current point
   * So you can only determine the direction change of the last point
   * And to determine the direction change sign difference you also need to
   * look at previous point, this will be set on the last line.
   *
   * maybe you have to go past the start point
   */
  def findChangeOfDirectionForLines(): Unit = {
    val points = _multiLine.getPoints() //List<? extends IPoint2D>
    val numberOfPoints = points.size
    val firstPointNumber = 1
    val closed = isClosed()
    var lastPointProperties: PointProperties = null
    var previousPointProperties: PointProperties = null
    var lastLineProperties: LineProperties = null
    lastPointProperties = _pointPropertiesList(0) //First point
    if (closed) {
      lastLineProperties = _linePropertiesList(0) //Line ending in first point
    }
    cfor(firstPointNumber)(_ < numberOfPoints, _ + 1) { i =>
      var currentLineProperties: LineProperties = null
      var currentPointProperties: PointProperties = null // Only used to pass on
      currentLineProperties = _linePropertiesList(i) //XXX outside range
      currentPointProperties = _pointPropertiesList(i)
      if (currentLineProperties != null && lastLineProperties != null) {
        lastPointProperties.directionChange = Calculator2D.angleBetweenLines(lastLineProperties.angle, currentLineProperties.angle)
      }
      if (previousPointProperties != null) {
        if (DoubleCalculations.oppositeSign(previousPointProperties.directionChange, lastPointProperties.directionChange))
          lastLineProperties.inflectionPoint = true
      }
      lastLineProperties = currentLineProperties
      previousPointProperties = lastPointProperties
      lastPointProperties = currentPointProperties
    }
    if (closed && 2 < numberOfPoints) {
      val firstPointProperties: PointProperties = _pointPropertiesList(0)
      val returnPointProperties: PointProperties = _pointPropertiesList(numberOfPoints - 2)
      val returnLineProperties: LineProperties = _linePropertiesList(0)
      if (!DoubleCalculations.sameSign(firstPointProperties.directionChange, returnPointProperties.directionChange))
        returnLineProperties.inflectionPoint = true
    }
  }

  /**
   * Find the accumulated direction change, the sum of all turns.
   */
  def findAccumulatedDirectionChange(): Unit = {
    _accumulatedDirectionChange = 0
    _accumulatedAbsoluteDirectionChange = 0
    if (_lastChain < 0) //Empty list
      return
    var lastDirection: Byte = _chainCodeForMultiLine(_lastChain)
    var lastDirectionChange: Int = 0
    var lastDiagonal = false
    cfor(0)(_ <= _lastChain, _ + 1) { i =>
      var direction: Byte = _chainCodeForMultiLine(i)
      if ((direction & 1) != 0) //Odd number
        _perimeter += SQRT_2
      else
        _perimeter += 1
      var directionChange = direction - lastDirection
      if (4 < directionChange)
        directionChange -= 8
      else if (directionChange <= -4)
        directionChange += 8
      //-4 and 4 are both correct, so you have to look at the last direction
      if (4 == directionChange && 0 < lastDirectionChange)
        directionChange -= 8
      // To prevent over counting perimiter 
      val diagonal: Boolean = ((direction & 1) == 1) && (directionChange == 2 || directionChange == -2)
      if (diagonal && !lastDiagonal) {
        _perimeter += SQRT_2 - 2
        lastDiagonal = true
      } else
        lastDiagonal = false
      lastDirectionChange = directionChange
      _accumulatedDirectionChange += directionChange
      _accumulatedAbsoluteDirectionChange += Math.abs(directionChange)
      lastDirection = direction
    }
    if (isClosed()) {
      _multiLine.setClosedLineClockWise(_accumulatedDirectionChange > 0)
      if (Math.abs(_accumulatedDirectionChange) != 8 && _accumulatedDirectionChange != 0) { //XXX not sure if 0 is OK
        val message = "Closed curve should have direction change -8 or 8, found: " + _accumulatedDirectionChange
        if (false)
          throw new RuntimeException(message)
        else
          System.out.println(message)
      }
    } else
      _multiLine.setClosedLineClockWise(false) //XXX was null
  }

  /** Concave arch, based on both neighbors. */
  def findConcaveArches(): Unit = {
    val points = _multiLine.getPoints() //List<? extends IPoint2D>
    var lastPointNumber = points.size
    var firstPointNumber: Int = 0
    var lastPoint: CPointInt = null
    var nextPoint: CPointInt = null
    if (isClosed()) {
      lastPointNumber = lastPointNumber - 1
      if (lastPointNumber < 1)
        return
      lastPoint = points(lastPointNumber - 1).asInstanceOf[CPointInt]
    }
    cfor(firstPointNumber)(_ < lastPointNumber, _ + 1) { i =>
      var lineProperties: LineProperties = null
      if (i < _linePropertiesList.size)
        lineProperties = _linePropertiesList(i)
      if (lineProperties != null) {
        val lastDist: Double = lineProperties.distanceToPoint(lastPoint)
        val nextDist: Double = lineProperties.distanceToPoint(nextPoint)
        lineProperties.lastDist = lastDist
        lineProperties.nextDist = nextDist
      }
    }
  }

  /**
   * It is a corner point if you go some pixels away and see how the
   * distance or direction changes sharply.
   */
  def findCornerPoints(): Unit = {
    _pointMap.foreach { entity =>
      var currentPointIndex: Integer = entity._1
      var currentPoint: CPointInt = entity._2
      val pointType: PointType = findCornerPoint(currentPointIndex)
      if (!PointType.UNKNOWN.equals(pointType)) {
        getAnnotatedShape().putAnnotation(currentPoint, pointType)
      }
    }
    if (isClosed()) {
      var hardCorner = false
      val inVector: CPointInt = findIntervalVector(_lastChain - SHORT_LINE_LENGTH, _lastChain)
      val outVector: CPointInt = findIntervalVector(0, 0 + SHORT_LINE_LENGTH)
      val angle: Double = Calculator2D.angleBetweenLines(inVector.angle(), outVector.angle())
      hardCorner = LIMIT_FOR_HARD_CORNER <= Math.abs(angle)
      if (hardCorner)
        getAnnotatedShape().putAnnotation(_firstPoint, PointType.HARD_CORNER)
      else
        getAnnotatedShape().putAnnotation(_firstPoint, PointType.SOFT_POINT)
    }
  }

  /**
   * Distinguish between soft and hard corner point.
   *
   * This might not work well for very short lines.
   *
   * @param index in chain code
   * @return true if this is a hard corner point
   */
  def findCornerPoint(index: Int): PointType = {
    if (SHORT_LINE_LENGTH < index && index < _lastChain - SHORT_LINE_LENGTH) {
      val inVector: CPointInt = findIntervalVector(index - SHORT_LINE_LENGTH, index)
      val outVector: CPointInt = findIntervalVector(index, index + SHORT_LINE_LENGTH)
      val angle: Double = Calculator2D.angleBetweenLines(inVector.angle(), outVector.angle())
      if (Math.abs(angle) < LIMIT_FOR_HARD_CORNER)
        return PointType.SOFT_POINT
      else
        return PointType.HARD_CORNER
    }
    PointType.UNKNOWN
  }

  /** Find the vector from the start index to the last index. */
  def findIntervalVector(startIndex: Integer, endIndex: Integer): CPointInt = {
    val currentPoint = new CPointInt(null)
    cfor(startIndex + 1)(_ <= endIndex, _ + 1) { i =>
      val direction: Byte = _chainCodeForMultiLine(i)
      currentPoint.x += Constants.CYCLE_POINTS_X(direction)
      currentPoint.y += Constants.CYCLE_POINTS_Y(direction)
    }
    currentPoint
  }

  def addPointsToMultiLine(): Unit = {
    var pointsInMultiLine: Int = Constants.BEFORE_START_INDEX
    _pointMap.foreach(entity => {
      pointsInMultiLine += 1
      val currentPoint: CPointInt = entity._2
      _multiLine.addAfterEnd(currentPoint)
    })
  }

  /**
   * For closed there the start and the end point is the same.
   *
   * So the first and the last PointProperties and LineProperties should also me the same
   */
  def handleClosedMultiLines(): Unit = {
    if (!isClosed())
      return
    val points = _multiLine.getPoints() //List<? extends IPoint2D>
    var numberOfPoints: Int = points.size
    numberOfPoints -= 1 //Start point also end point
    if (numberOfPoints < 1)
      return
    _linePropertiesList(Constants.ZERO) = _linePropertiesList(numberOfPoints)
    _pointPropertiesList(Constants.ZERO) = _pointPropertiesList(numberOfPoints)
  }

  /**
   * When information have been collected use them for annotations.
   *
   * Just run through all the lines and calculate the annotations and set
   * them in the annotation structure for the polygon
   */
  def annotatePointsAndLines(): Unit = {
    try {
      var lastPoint: CPointInt = null
      val points = _multiLine.getPoints() //List<? extends IPoint2D>
      var numberOfPoints: Int = points.size
      if (numberOfPoints == 0)
        return
      var firstPointNumber: Int = 0
      if (isClosed()) {
        numberOfPoints -= 1 //Start point also end point
        if (numberOfPoints == 0)
          return
        lastPoint = points(numberOfPoints - 1).asInstanceOf[CPointInt]
      }
      cfor(firstPointNumber)(_ < numberOfPoints, _ + 1) { i =>
        val currentPoint: CPointInt = points(i).asInstanceOf[CPointInt]
        if (lastPoint != null) {
          var currentLine = CLine.makeUnordered(currentPoint, lastPoint)
          var currentLineProperties: LineProperties = _linePropertiesList(i)
          if (currentLineProperties != null) {
            val lineTypes = currentLineProperties.getValue() //Set < LineType >
            getAnnotatedShape().putAllAnnotation(currentLine, lineTypes)
          }
        }
        lastPoint = currentPoint
      }
    } catch {
      case ex: Throwable => {
        println(s"annotatePointsAndLines() error: ${ex.getMessage}")
      }
    }
  }

  /**
   * All lengths are unnormalized.
   *
   * For the orthogonal vector I use the hat operator: (x,y) -> (y,-x)
   */
  def handleInterval(startIndex: Integer, endIndex: Integer): Integer = {
    // Do not split anything that is less than 5, unless it is the first
    var doNotSplit = false
    if (endIndex - startIndex < 5 &&
      !startIndex.equals(Constants.BEFORE_START_INDEX) && !endIndex.equals(_lastChain))
      doNotSplit = true
    val startPoint: CPointInt = _pointMap(startIndex)
    val endPoint: CPointInt = _pointMap(endIndex)
    val relativeVector: CPointInt = endPoint.copy().minus(startPoint).asInstanceOf[CPointInt]
    val xCoordinate: Int = endPoint.x - startPoint.x
    val yCoordinate: Int = endPoint.y - startPoint.y
    //		Int unnormalizedDistanceCoordinate = yCoordinate * startPoint.x - xCoordinate * startPoint.y 
    var currentDist = 0
    val lineProperties = new LineProperties()
    lineProperties.angle = relativeVector.angle()
    lineProperties.relativeVector = relativeVector
    lineProperties.startPoint = startPoint
    var splitPoint: CPointInt = null
    var splitPointIndex: Integer = null
    if (!doNotSplit) {
      var maxNegativePoint: CPointInt = new CPointInt(null)
      var currentPoint = startPoint.copy().asInstanceOf[CPointInt]
      var distanceDifference: Array[Int] = null
      var lengthOfDistanceUnit: Double = relativeVector.distanceFromOrigin()
      if (xCoordinate == 0 && yCoordinate == 0) {
        lengthOfDistanceUnit = 1
      } else {
        distanceDifference = new Array[Int](Constants.DIRECTIONS_AROUND_POINT)
        cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { i =>
          distanceDifference(i) = Constants.CYCLE_POINTS_X(i) * yCoordinate -
            Constants.CYCLE_POINTS_Y(i) * xCoordinate
        }
      }
      lineProperties.lengthOfDistanceUnit = lengthOfDistanceUnit
      var unnormalizedDistanceInt: Int = Math.ceil(lengthOfDistanceUnit).toInt
      lengthOfDistanceUnit *= getDistLimit(endIndex + 1 - startIndex)
      cfor(startIndex + 1)(_ <= endIndex, _ + 1) { i =>
        val direction: Byte = _chainCodeForMultiLine(i)
        currentPoint.x += Constants.CYCLE_POINTS_X(direction)
        currentPoint.y += Constants.CYCLE_POINTS_Y(direction)
        if (distanceDifference != null)
          currentDist += distanceDifference(direction)
        else
          currentDist = ((startPoint).distance(currentPoint) * unnormalizedDistanceInt).toInt
        if (Math.abs(currentDist) < unnormalizedDistanceInt) {
          lineProperties.pixelsWithAlmostZeroDistance += 1
        } else if (currentDist < 0) {
          lineProperties.pixelsWithNegativeDistance += 1
          lineProperties.areaNegativeDistance -= currentDist
          if (currentDist < lineProperties.maxNegativeDist) {
            lineProperties.maxNegativeDist = currentDist
            maxNegativePoint.setLocation(currentPoint)
            lineProperties.maxNegativeIndex = i
          }
        } else {
          lineProperties.pixelsWithPositiveDistance += 1
          lineProperties.areaPositiveDistance += currentDist
          if (lineProperties.maxPositiveDist < currentDist) {
            lineProperties.maxPositiveDist = currentDist
            lineProperties.maxPositivePoint.setLocation(currentPoint)
            lineProperties.maxPositiveIndex = i
          }
        }
      }
      if (-lineProperties.maxNegativeDist < lineProperties.maxPositiveDist) {
        if (lengthOfDistanceUnit < lineProperties.maxPositiveDist) {
          splitPoint = lineProperties.maxPositivePoint
          splitPointIndex = lineProperties.maxPositiveIndex
        }
      } else {
        if (lengthOfDistanceUnit < -lineProperties.maxNegativeDist) {
          splitPoint = maxNegativePoint
          splitPointIndex = lineProperties.maxNegativeIndex
        }

      }
      //			assert endPoint.equals(currentPoint)
    }
    if (splitPointIndex != null) {
      _pointMap.put(splitPointIndex, splitPoint)
      handleInterval(startIndex, splitPointIndex)
      handleInterval(splitPointIndex, endIndex)
    } else {
      addPropertiesForNewPoint(endIndex, endPoint, lineProperties)
    }
    splitPointIndex
  }

  def addPropertiesForNewPoint(endPointIndex: Integer,
    endPoint: CPointInt, lineProperties: LineProperties): Unit = {
    _pointMap.put(endPointIndex, endPoint.copy().asInstanceOf[CPointInt])
    _linePropertiesList.append(lineProperties)
    val pointProperties = new PointProperties()
    _pointPropertiesList.append(pointProperties)
  }

  def swap(i: Int): Unit = {
    val swapHolder: Byte = _chainCodeForMultiLine(i)
    val otherIndex: Int = _lastChain - i
    _chainCodeForMultiLine(i) = oppositeDirection(_chainCodeForMultiLine(otherIndex))
    if (otherIndex != i)
      _chainCodeForMultiLine(otherIndex) = oppositeDirection(swapHolder)
  }

  //Simple interaction part

  def addChainCode(chainCode: Byte): Boolean = {
    _lastChain += 1
    if (_lastChain == _chainCodeForMultiLine.length) //XXX should probably expand the array
    {
      if (false) {
        val maxPoints: Int = _chainCodeForMultiLine.length
        val xtemp = new Array[Byte](maxPoints * 2)
        System.arraycopy(_chainCodeForMultiLine, 0, xtemp, 0, maxPoints)
        _chainCodeForMultiLine = xtemp
      } else {
        _lastChain -= 1
        return false
      }
    }
    _chainCodeForMultiLine(_lastChain) = chainCode
    _lastPoint.translate(Constants.CYCLE_POINTS_X(chainCode), Constants.CYCLE_POINTS_Y(chainCode))
    _bBox.addPoint(_lastPoint)
    true
  }

  /**
   * Opposite Direction.
   */
  def swapChainCodeInOppositeDirection(): Unit = {
    cfor(0)(_ <= _lastChain / 2, _ + 1) { i =>
      swap(i)
    }
    val swapHolder: CPointInt = _firstPoint.copy().asInstanceOf[CPointInt]
    _firstPoint.setLocation(_lastPoint)
    _lastPoint.setLocation(swapHolder)
    _accumulatedDirectionChange = -_accumulatedDirectionChange
  }

  //Getter and setters part

  override def isDirty(): Boolean = {
    _dirty
  }

  /**
   * DistLimit is half based on the diameter of the polygon half on the length
   * of the line.
   */
  def getDistLimit(pixelCountInCurrentLineInterval: Int): Double = {
    val distLimit: Double = (_lastChain + pixelCountInCurrentLineInterval) / 20.0
    Math.max(distLimit, 1.0)
  }

  def setFirstPoint(firstPoint: IPoint2D): Unit =
    {
      _firstPoint.setLocation(firstPoint.getX(), firstPoint.getY())
      _lastPoint.setLocation(_firstPoint)
      _bBox.addPoint(_firstPoint)
    }

  def getFirstPoint(): CPointInt =
    {
      _firstPoint
    }

  def getChainCodeForMultiLine(): Array[Byte] = {
    _chainCodeForMultiLine
  }

  def setMultiLine(line: MultiLine): Unit = {
    _multiLine = line
  }

  /** Last point is a running point so this will not always work. */
  def isClosed(): Boolean = {
    _lastChain > 2 && _firstPoint.equals(_lastPoint)
  }

  def getLastChain(): Int = {
    _lastChain
  }

  def getLastPoint(): CPointInt = {
    _lastPoint
  }

  def getPerimeter(): Double = {
    _perimeter
  }

  def setPerimeter(perimeter: Double): Unit = {
    _perimeter = perimeter
  }
}
