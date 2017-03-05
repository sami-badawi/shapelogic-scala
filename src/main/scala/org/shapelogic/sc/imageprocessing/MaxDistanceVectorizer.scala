package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.polygon.Calculator2D._
//import org.shapelogic.sc.logic.LetterTaskFactory
import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.polygon.Calculator2D
import org.shapelogic.sc.util.Constants
import spire.implicits._
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.polygon.AnnotatedShapeImplementation

/**
 * Vectorizer that is splitting lines based on max distance to line between end points.
 * <br />
 * <p>
 * The main idea is that this will read a whole multi line at a time.
 * Then later it will split it according to max distance of pixels to the line
 * between start and end point of the multi line.
 * </p> <p>
 * Maybe this could be completely abstracted out, maybe but at that point I
 * will just take most of this class and turn it into a base class.
 * </p> <p>
 * Always stop on junctions, if there is one junction point use that, but stop after.
 * N points are chosen last.
 * Never go from one N point to another,
 * unless that the N point is the first point, to handle an X, where you have 4
 * N points in the center.
 * If you are at a start point then just chose one direction.
 * Can I delegate this to a different object. I always need to find all the
 * neighbors first.
 * I might have to know how many N points there are if there are more just
 * add all to _unfinishedPoints.
 * </p> <p>
 * Treatment of different points:
 * Junction: add to new point, move to first junction.
 * N points: count, keep track of first.
 * Other: count, keep track of first.
 * Unused: count, keep track of first. I think that is already done.
 * Used: count, keep track of first.
 * </p> <p>
 * For each junction add to unfinished. Go to first junction.
 * If other points are available take first and go to it.
 * If only N point is available, if current point an N and not the first point
 * stop else go to that.
 * </p> <p>
 * When coming to a new point check if it is a junction if stop if not on
 * first point. It does not matter if the start point is used or not.
 * I think that at the end check to see if you can go to either a junction
 * point or to the start point.
 * Also stop if you do not know what to do, at the end of handleProblematicPoints().
 * </p>
 * @author Sami Badawi
 *
 */
class MaxDistanceVectorizer(imageIn: BufferImage[Byte]) extends BaseVectorizer(imageIn) {

  override def verboseLogging = false

  //Top level so create annotation here
  lazy val annotatedShapeImplementation = new AnnotatedShapeImplementation(null)

  //This is problematic since ChainCodeHandler only handles one polygon not the multi polygon
  var _chainCodeHandler: ChainCodeHandler = null //new ChainCodeHandler(annotatedShapeImplementation)

  /**
   * Take point off _unfinishedPoints try to start line from that, if nothing is found the remove point
   */
  override def findMultiLine(): Unit = {
    findFirstLinePoint(process = true) //XXX maybe move
    do {
      val done = !findMultiLinePreProcess()
      if (done)
        return
      var stop1 = false
      cfor(0)(_ => !stop1, _ + 1) { i =>
        if (!findNextLinePoint())
          stop1 = true
      }
      if (startedInTheMiddleTurnOpposite()) {
        var stop2 = false
        cfor(0)(_ => !stop2, _ + 1) { j =>
          if (!findNextLinePoint())
            stop2 = true
        }
      }
      findMultiLinePostProcess()
    } while (true)
  }

  /**
   * started In The Middle Turn Opposite
   * If there are 2 neighbors and 1 of them are unused
   * @return
   */
  def startedInTheMiddleTurnOpposite(): Boolean = {
    var firstPointInMultiLineIndex: Int = this.pointToPixelIndex(_firstPointInMultiLine)
    var startPixelTypeCalculator: PixelTypeCalculator = findPointType(firstPointInMultiLineIndex, null)
    var color: Byte = _pixels(firstPointInMultiLineIndex)
    //		if (PixelType.
    if (startPixelTypeCalculator.neighbors != 2 || startPixelTypeCalculator.unusedNeighbors != 1)
      return false
    _chainCodeHandler.swapChainCodeInOppositeDirection()
    var swapHolder: CPointInt = _firstPointInMultiLine.copy().asInstanceOf[CPointInt]
    _firstPointInMultiLine.setLocation(_currentPoint)
    _currentPoint.setLocation(swapHolder)
    _currentPixelIndex = firstPointInMultiLineIndex
    return true
  }

  /**
   * Get the next point to investigate from _currentPoint
   * This also contains check if this should cause a new new point to be created.
   *
   * if there is more than one point to chose from add the point to:
   * _unfinishedPoints that is a list of points that need to be revisited.
   *  assumes that _pixelTypeCalculator is set to current point
   * @return true if there are more points
   */
  def findNextLinePoint(): Boolean = {
    //If there is only one direction to go in then do it
    var newDirection: Byte = Constants.DIRECTION_NOT_USED
    findPointType(_currentPixelIndex, _pixelTypeCalculator)
    //Stop at any junction unless you are just starting
    if (PixelType.PIXEL_JUNCTION.equals(_pixelTypeCalculator.getPixelType())
      && _chainCodeHandler.getLastChain() > Constants.BEFORE_START_INDEX) {
      //      addToUnfinishedPoints(_currentPoint.copy().asInstanceOf[CPointInt])
      newDirection = handleJunction()
      return false
    } else if (_pixelTypeCalculator.unusedNeighbors == 1) {
      newDirection = _pixelTypeCalculator.firstUnusedNeighbor
    } else if (_pixelTypeCalculator.unusedNeighbors == 0) {
      newDirection = handleLastUnused()
    } else {
      newDirection = handleProblematicPoints()
      if (newDirection == Constants.DIRECTION_NOT_USED)
        addToUnfinishedPoints(_currentPoint.copy().asInstanceOf[CPointInt])
    }
    if (newDirection == Constants.DIRECTION_NOT_USED)
      return false
    _currentDirection = newDirection //XXX redundant
    moveCurrentPointForwards(_currentDirection)
    _chainCodeHandler.addChainCode(newDirection)
    return true
  }

  /**
   * Get here if there are no unused directions left
   *
   * Ways to go further:
   *
   * First point is 1 away
   * A junction is 1 away
   *
   * And you are not standing on the on the second pixel trying to go back
   */
  def handleLastUnused(): Byte = {
    var isEndPoint: Boolean = true
    var newDirection: Byte = Constants.DIRECTION_NOT_USED
    newDirection = directionBetweenNeighborPoints(_currentPoint, _firstPointInMultiLine)
    if (newDirection != Constants.DIRECTION_NOT_USED &&
      _chainCodeHandler.getLastChain() != Constants.START_INDEX)
      isEndPoint = false
    if (isEndPoint) {
      var pointHandle: NeighborChecker =
        new NeighborChecker(outputImage, _currentPixelIndex)
      pointHandle.checkNeighbors()
      //If you have taken more than 2 steps you can go back to any junction point
      //maybe this could be expanded 
      //or I could put a constraint in that it cannot go back to the start point
      if (0 < pointHandle.junction.countUsed &&
        Constants.START_INDEX < _chainCodeHandler.getLastChain()) {
        //        addToUnfinishedPoints(_currentPoint.copy().asInstanceOf[CPointInt])
        isEndPoint = false
        newDirection = pointHandle.junction.firstUsedDirection
      }
    }
    if (isEndPoint) {
      var endPoint: CPointInt = _currentPoint.copy().asInstanceOf[CPointInt]
      getPolygon().putAnnotation(endPoint, GeometricType.PIXEL_LINE_END)
    }
    return newDirection
  }

  def handleJunction(): Byte = {
    var pointHandle: NeighborChecker =
      new NeighborChecker(outputImage, _currentPixelIndex)
    pointHandle.checkNeighbors()
    if (pointHandle.falseJunction()) {
      //Too complicated set an extra point unless at start
      if (pointHandle.vCornerPoint.count != 1 && pointHandle.extraNeighborPoint.count != 2
        && Constants.BEFORE_START_INDEX < _chainCodeHandler.getLastChain())
        return Constants.DIRECTION_NOT_USED
      var directionBackToPrevious: Byte = Calculator2D.oppositeDirection(_currentDirection)
      var comesFromVPoint: Boolean = pointHandle.vCornerPoint.firstDirection == directionBackToPrevious
      if (!comesFromVPoint)
        return pointHandle.vCornerPoint.firstDirection
      //Coming from the V point select the unused point with biggest 
      //distance from V or distance that is not 90 degrees
      val directionToVPoint: Byte = pointHandle.vCornerPoint.firstDirection
      cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { iInt =>
        val i: Byte = iInt.toByte
        var pixelIndexI: Int = _currentPixelIndex + cyclePoints(i)
        var pixel: Byte = _pixels(pixelIndexI)
        if (PixelType.isUnused(pixel)) {
          if (2 != Math.abs(Calculator2D.directionDifference(directionToVPoint, i))) {
            return i
          }
        }
      }
    }
    val junctionPoint: CPointInt = _currentPoint.copy().asInstanceOf[CPointInt]
    addToUnfinishedPoints(junctionPoint)
    getPolygon().putAnnotation(junctionPoint, GeometricType.PIXEL_JUNCTION)
    return Constants.DIRECTION_NOT_USED
  }

  /**
   * Junction: add to new point, move to first junction.
   * N points: count, keep track of first.
   * Other: count, keep track of first.
   * Used: count, keep track of first.
   *
   * Unused: count, keep track of first. This is done in the point finder.
   */
  override def handleProblematicPoints(): Byte = {
    var pointHandle: NeighborChecker =
      new NeighborChecker(outputImage, _currentPixelIndex)
    pointHandle.checkNeighbors()
    //XXX problematic with 2 points next to each other
    if (pointHandle.junction.count > 0 && Constants.START_INDEX != _chainCodeHandler.getLastChain() &&
      _currentDirection != BaseVectorizer.oppesiteDirection(pointHandle.junction.firstDirection)) {
      if (pointHandle.falseJunction())
        return pointHandle.vCornerPoint.firstDirection
      val pixelIndexI: Int = _currentPixelIndex + cyclePoints(pointHandle.junction.firstDirection)
      val pixel: Byte = _pixels(pixelIndexI)
      if (PixelType.isUnused(pixel))
        return pointHandle.junction.firstDirection
    }
    if (pointHandle.vCornerPoint.count > 0)
      return pointHandle.vCornerPoint.firstDirection
    else if (pointHandle.other.count > 0)
      return pointHandle.other.firstDirection
    else if (0 < pointHandle.extraNeighborPoint.count &&
      (_chainCodeHandler.getLastChain() <= 0 ||
        !PixelType.PIXEL_EXTRA_NEIGHBOR.equals(_pixelTypeCalculator.getPixelType())))
      return pointHandle.extraNeighborPoint.firstDirection
    else if (pointHandle.used.countUsed > 0)
      //Only works if at end of closed curve
      return directionBetweenNeighborPoints(_currentPoint, _firstPointInMultiLine)
    return Constants.DIRECTION_NOT_USED
  }

  /** Everything is always OK. Stop only on junctions and end points. */
  override def lastPixelOk(newDirection: Byte): Boolean = {
    return true
  }

  override def internalFactory(): Unit = {
    _pixelTypeFinder = new SimplePixelTypeFinder(outputImage)
    //    _rulesArrayForLetterMatching = LetterTaskFactory.getSimpleNumericRuleForAllLetters(LetterTaskFactory.POLYGON)
  }

  override def findMultiLinePreProcess(): Boolean = {
    var result: Boolean = super.findMultiLinePreProcess()
    if (!result)
      return result
    _chainCodeHandler = new ChainCodeHandler(getPolygon().getAnnotatedShape())
    _chainCodeHandler.setup()
    _chainCodeHandler.setMultiLine(this.getPolygon().getCurrentMultiLine())
    _chainCodeHandler.setFirstPoint(_firstPointInMultiLine)
    return result
  }

  override def findMultiLinePostProcess(): Unit = {
    _chainCodeHandler.getValue()
    super.findMultiLinePostProcess()
  }

  /**
   * XXX Not sure about this
   */
  def init(): Unit = {
    //    super.init()
    _chainCodeHandler = new ChainCodeHandler(getPolygon().getAnnotatedShape())
  }
}

object MaxDistanceVectorizer {

  def transform(image: BufferImage[Byte]): BufferImage[Byte] = {
    if (image.numBands != 1) {
      println(s"Can only handle images with 1 channel run treshold first")
      return image
    }
    val maxDistanceVectorizer = new MaxDistanceVectorizer(
      image)
    maxDistanceVectorizer.findMultiLine()
    val points = maxDistanceVectorizer.getPoints()
    println(s"MaxDistanceVectorizer: points: $points")
    val polygon = maxDistanceVectorizer.getPolygon()
    println(s"MaxDistanceVectorizer: polygon: $polygon")
    maxDistanceVectorizer.result
  }
}