package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.polygon.Calculator2D.directionBetweenNeighborPoints

//import org.shapelogic.sc.logic.LetterTaskFactory
//import org.shapelogic.sc.logic.LetterTaskLegacyFactory
import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.polygon.CircleInterval
import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.util.LineType

import spire.implicits._
import org.shapelogic.sc.util.LineType.LineType
import org.shapelogic.sc.image.BufferImage

object ShortLineBasedVectorizer {
  val MAX_NUMBER_OF_POINTS_IN_SHORT_LINE: Int = 5
  val ANGLE_DIFFERENCE_LIMIT: Double = Math.PI * 15.0 / 180.0

}

/**
 * Base class for vectorizers that are using a local short line,
 * to determine when to set point on multi line.
 *
 * This approach was not very successful.
 *
 * @author Sami Badawi
 *
 */
abstract class ShortLineBasedVectorizer(imageIn: BufferImage[Byte]) extends BaseVectorizer(imageIn) {
  import ShortLineBasedVectorizer._

  var _angleLimit: Double = ANGLE_DIFFERENCE_LIMIT
  var _currentAngle: Double = 0
  var _pointsInCurrentShortLine: Int = 0
  var _maxPointsInShortLine: Int = MAX_NUMBER_OF_POINTS_IN_SHORT_LINE
  var _secondUsedDirection: Byte = Constants.DIRECTION_NOT_USED
  /** Try to do short lines and */
  var _startOfShortLinePoint: CPointInt = new CPointInt(null)
  /** Short to current point */
  var _currentVectorDirection: CPointInt = null
  val _pixelTypeCalculatorNextPoint: PixelTypeCalculator = new PixelTypeCalculator()
  var _currentCircleInterval: CircleInterval = new CircleInterval()
  /** Start of current line, this is also the last point saved in the multi line */
  var _firstPointInLine: CPointInt = null
  var _firstUsedDirection: Byte = Constants.DIRECTION_NOT_USED

  var _currentLineType: LineType = LineType.UNKNOWN

  /**
   * Take point off _unfinishedPoints try to start line from that, if
   * nothing is found the remove point.
   */
  def findMultiLine(): Unit = {
    if (!findMultiLinePreProcess())
      return
    while (findNextLinePoint()) {
      if (!multiLineHasGlobalFitness())
        makeNewPoint()
    }
    findMultiLinePostProcess()
  }

  /**
   * Additional check for if new new point to be created.
   *
   * @return true if a new point should be created
   */
  def multiLineHasGlobalFitness(): Boolean

  override def findMultiLinePostProcess(): Unit = {
    makeNewPoint()
    super.findMultiLinePostProcess()
  }

  override def findMultiLinePreProcess(): Boolean = {
    val result: Boolean = super.findMultiLinePreProcess()
    _currentLineType = LineType.UNKNOWN
    if (result)
      makeNewPoint()
    result
  }

  /**
   * Find the maximum if there is more than one the add them all the unknown list.
   */
  def handleProblematicPoints(): Byte = {
    //Find and set the type of all the neighbor points
    val localPixelTypeCalculator = new PixelTypeCalculator()
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { (i: Int) =>
      val pixelIndexI = _currentPixelIndex + cyclePoints(i)
      var pixel: Byte = _pixels(pixelIndexI)
      if (pixel == PixelType.PIXEL_FOREGROUND_UNKNOWN.color) {
        findPointType(pixelIndexI, localPixelTypeCalculator)
        pixel = localPixelTypeCalculator.getPixelType().color
        _pixels(pixelIndexI) = pixel
      }
    }
    findPointType(_currentPixelIndex, _pixelTypeCalculator)
    if (_pixelTypeCalculator.highestRankedUnusedIsUnique || _currentPoint.equals(_firstPointInMultiLine))
      return _pixelTypeCalculator.highestRankedUnusedNeighbor
    else {
      _unfinishedPoints.append(_currentPoint)
    }
    Constants.DIRECTION_NOT_USED
  }

  /**
   * Find the maximum if there is more than one the add them all the unknown list.
   */
  def handleProblematicPointsAddNeighborPoints(): Byte = {
    //Find and set the type of all the neighbor points
    val localPixelTypeCalculator = new PixelTypeCalculator()
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { (i: Int) =>
      localPixelTypeCalculator.setup()
      var pixelIndexI: Int = _currentPixelIndex + cyclePoints(i)
      var pixel: Byte = _pixels(pixelIndexI)
      if (pixel == PixelType.PIXEL_FOREGROUND_UNKNOWN.color) {
        findPointType(pixelIndexI, localPixelTypeCalculator)
        pixel = localPixelTypeCalculator.getPixelType().color
        _pixels(pixelIndexI) = pixel
      }
    }
    findPointType(_currentPixelIndex, _pixelTypeCalculator)
    if (_pixelTypeCalculator.highestRankedUnusedIsUnique)
      return _pixelTypeCalculator.highestRankedUnusedNeighbor
    else {
      cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { (i: Int) =>
        var pixelIndexI: Int = _currentPixelIndex + cyclePoints(i)
        var pixel: Byte = _pixels(pixelIndexI)
        if ((Constants.BYTE_MASK & pixel) == _pixelTypeCalculator.highestRankedUnusedPixelTypeColor)
          _unfinishedPoints.append(pixelIndexToPoint(pixelIndexI))
      }
      _pixels(_currentPixelIndex) = PixelType.toUsed(_pixels(_currentPixelIndex))
    }
    Constants.DIRECTION_NOT_USED
  }

  /** Hook for creating new short line. */
  def newShortLine(): Unit = {
    _currentVectorDirection = _currentPoint.copy().minus(_startOfShortLinePoint).asInstanceOf[CPointInt]
    _currentAngle = _currentVectorDirection.angle()
    if (!_currentAngle.isNaN)
      _currentCircleInterval.addClosestAngle(_currentAngle)
    resetShortLine()
  }

  def resetShortLine(): Unit = {
    _pointsInCurrentShortLine = 0
    _startOfShortLinePoint.setLocation(_currentPoint)
  }

  def getMaxSinceLast(): Int = {
    _maxPointsInShortLine
  }

  def setMaxSinceLast(maxSinceLast: Int): Unit = {
    this._maxPointsInShortLine = maxSinceLast
  }

  def setAngleLimit(angleLimit: Double): Unit = {
    this._angleLimit = angleLimit
  }

  /** Change to be an up front check. */
  def lastPixelOk(newDirection: Byte): Boolean = {
    if (newDirection == _firstUsedDirection) true
    else if (newDirection == _secondUsedDirection || _secondUsedDirection == Constants.DIRECTION_NOT_USED) true
    else false
  }

  def getAngleLimit(): Double = {
    _angleLimit
  }

  override def moveCurrentPointForwards(newDirection: Byte): Unit = {
    super.moveCurrentPointForwards(newDirection)
    if (_firstUsedDirection == Constants.DIRECTION_NOT_USED) {
      _firstUsedDirection = newDirection
    }
    _pointsInCurrentShortLine += 1
  }

  def makeNewPoint(): Unit = {
    getPolygon().addAfterEnd(_currentPoint.copy())
    _firstPointInLine = pixelIndexToPoint(_currentPixelIndex) //If the line has been moved 1 back
    if (_pixels(_currentPixelIndex) == PixelType.BACKGROUND_POINT.color)
      _errorMessage = "Setting point at background point: " + _firstPointInLine
    makeNewPointPostProcess()
    _currentPoint = pixelIndexToPoint(_currentPixelIndex) //If the line has been moved 1 back
    resetShortLine()
  }

  def makeNewPointPostProcess(): Unit = {
    _firstUsedDirection = Constants.DIRECTION_NOT_USED
    _currentDirection = Constants.DIRECTION_NOT_USED
    _currentLineType = LineType.UNKNOWN
    _secondUsedDirection = Constants.DIRECTION_NOT_USED
    _pointsInCurrentShortLine = 0
    _currentCircleInterval = new CircleInterval()
  }

  /** All the objects that needs special version should be created here. */
  def internalFactory(): Unit = {
    _pixelTypeFinder = new PriorityBasedPixelTypeFinder(imageIn)
    //Only use the simple rules, not all the annotations are set in these branch of vectorizers
    //    _rulesArrayForLetterMatching = LetterTaskLegacyFactory.getSimpleNumericRuleForAllStraightLetters(LetterTaskFactory.POLYGON)
  }

  /**
   * Get the next point to investigate from _currentPoint.
   *
   * This also contains check if this should cause a new new point to be created.
   *
   * if there is more than one point to chose from add the point to:
   * _unfinishedPoints that is a list of points that need to be revisited.
   * assumes that _pixelTypeCalculator is set to current point
   *
   * @return true if there are more points
   */
  def findNextLinePoint(): Boolean = {
    //If there is only one direction to go in then do it
    var newDirection: Byte = Constants.DIRECTION_NOT_USED
    var backToStart = false
    findPointType(_currentPixelIndex, _pixelTypeCalculator)
    if (_pixelTypeCalculator.unusedNeighbors == 1) {
      newDirection = _pixelTypeCalculator.firstUnusedNeighbor
      findPointType(_currentPixelIndex + cyclePoints(newDirection), _pixelTypeCalculatorNextPoint)
    } else if (_pixelTypeCalculator.unusedNeighbors == 0) {
      newDirection = directionBetweenNeighborPoints(_currentPoint, _firstPointInMultiLine)
      if (newDirection != Constants.DIRECTION_NOT_USED)
        backToStart = true
    } else
      newDirection = handleProblematicPoints()
    if (newDirection == Constants.DIRECTION_NOT_USED)
      return false
    if (lastPixelOk(newDirection)) {
      _currentDirection = newDirection
      moveCurrentPointForwards(_currentDirection)
    } else {
      makeNewPoint()
      _currentDirection = newDirection
      moveCurrentPointForwards(_currentDirection)
    }
    if (backToStart)
      makeNewPoint()
    return true
  }

  /**
   * Insert point between current _firstPointInLine and newFirstPoint.
   *
   * There are currently several things that I am not sure how to handle
   * look in makeNewPoint()
   */
  def splitLine(splitPoint: CPointInt): Unit = {
    if (_firstPointInLine != null)
      getPolygon().getCurrentMultiLine().addAfterEnd(splitPoint)
  }
}
