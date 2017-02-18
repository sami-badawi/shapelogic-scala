package org.shapelogic.sc.polygon

import org.shapelogic.sc.util.MapOperations.getPointWithDefault

import org.shapelogic.sc.calculation.CalcInvoke
import org.shapelogic.sc.util.LineType

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Set
import scala.collection.mutable.Map
import scala.collection.Seq
import org.shapelogic.sc.util.LineType

import spire.implicits._
import scala.reflect.internal.util.Collections

/**
 * A list of point on a continues line that does not have any intersections.
 *
 * But it can contain turns.
 *
 * Should this be mutable or immutable?
 * This is just a list of already existing point so immutable should be fine.
 * I can change this later.
 *
 * @author Sami Badawi
 *
 */
class MultiLine(annotatedShape: AnnotatedShapeImplementation) extends BaseAnnotatedShape(annotatedShape) with ILine2D with AnnotatedShape with CalcInvoke[MultiLine] with PointReplacable[MultiLine] {
  /**
   * UNKNOWN means not tested for round, but treat as a multi line
   * NOT_ROUND means that it is tested for any of the round categories and it is not, treat it as a multi line
   * STRAIGHT lines combined to a straight line, say for an F
   * CIRCLE_ARCH_FORWARDS, from the first point move in increasing angle to get to second point
   */
  protected var _points = new ArrayBuffer[IPoint2D]()
  /** Should be set if the multi line turns out to be a circle */
  var _centerForCircle: IPoint2D = null
  val _bBox = new BBox(null)
  var _dirty: Boolean = true
  protected var _lineType: LineType.LineType = LineType.UNKNOWN
  //I could make this lazy
  //	protected AnnotatedShape _annotatedShape
  protected var _closedLineClockWise: Boolean = false

  override def getEnd(): IPoint2D = {
    _points.lastOption.getOrElse(null)
  }

  override def getStart(): IPoint2D = {
    _points.headOption.getOrElse(null)
  }

  def addBeforeStart(newPoint: IPoint2D): Unit = {
    _points.+=:(newPoint)
  }

  def addAfterEnd(newPoint: IPoint2D): Unit = {
    if (!newPoint.equals(getEnd()))
      _points.+=(newPoint)
  }

  /**
   * Sorts all points alphabetically.
   *
   * This is not very reliable, could cause problems
   */
  override def compareTo(other: ILine2D): Int = {
    val that: MultiLine = other.asInstanceOf[MultiLine]
    val thisLenght = this._points.size
    val thatLenght = that._points.size
    val minPointLenght = Math.min(thisLenght, thatLenght)
    cfor(0)(_ < minPointLenght, _ + 1) { i =>
      val result = _points(i).compareTo(that.getPoints()(i))
      if (result != 0)
        return result
    }
    if (thisLenght > thatLenght)
      1
    else
      -1
  }

  /**
   * Overriden equals.
   * Problems with this method:
   * Change to be able to compare with CLine
   * hashcode() should also be changed
   */
  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[MultiLine])) {
      if (obj.isInstanceOf[CLine]) {
        val line = obj.asInstanceOf[CLine]
        return line.equals(toCLine())
      }
      return false
    }
    val that: MultiLine = obj.asInstanceOf[MultiLine]
    compareTo(that) == 0
  }

  def getPoints(): Seq[_ <: IPoint2D] = {
    _points
  }

  def setPoints(points: Seq[_ <: IPoint2D]): Unit = {
    if (points.isInstanceOf[ArrayBuffer[IPoint2D]])
      _points = points.asInstanceOf[ArrayBuffer[IPoint2D]]
    else {
      _points.clear()
      _points.++=(points)
    }

  }

  /**
   * Not sure what to return.
   * Maybe an array of CMultiLine
   *
   * @param splitPoint
   */
  def split(splitPoint: IPoint2D): Array[MultiLine] = {
    val splitIndex = _points.indexOf(splitPoint)
    split(splitIndex)
  }

  /**
   * Not sure what to return.
   * Maybe an array of CMultiLine
   *
   * @param splitIndex
   */
  def split(splitIndex: Int): Array[MultiLine] = {
    if (splitIndex < 0 || _points.size <= splitIndex)
      return null
    val firstMultiLine = new MultiLine(this.getAnnotatedShape())
    val secondMultiLine = new MultiLine(this.getAnnotatedShape())
    cfor(0)(_ <= splitIndex, _ + 1) { i =>
      firstMultiLine.addAfterEnd(_points(i))
    }
    cfor(splitIndex)(_ < _points.size, _ + 1) { i =>
      firstMultiLine.addAfterEnd(_points(i))
    }
    val result = Array[MultiLine](firstMultiLine, secondMultiLine)
    result
  }

  def getCenterForCircle(): IPoint2D = {
    _centerForCircle
  }

  def setCenterForCircle(forCircle: IPoint2D): Unit = {
    _centerForCircle = forCircle
  }

  override def isDirty(): Boolean = {
    _dirty
  }

  override def setup(): Unit = {
    if (_annotatedShape != null)
      _annotatedShape.setup()
  }

  override def invoke(): MultiLine = {
    _points.foreach(point => _bBox.addPoint(point))
    _dirty = false
    this
  }

  override def getValue(): MultiLine = {
    if (_dirty)
      invoke()
    this
  }

  def getBBox(): BBox = {
    getValue()
    _bBox
  }

  def toCLine(): CLine = {
    if (_points.size == 2)
      new CLine(_points(0), _points(1))
    else if (_points.size == 1)
      new CLine(_points(0), _points(0))
    else
      null
  }

  def getLineType(): LineType.LineType = {
    _lineType
  }

  def isClosed(): Boolean = {
    _points.size > 1 && getStart().equals(getEnd())
  }

  override def replacePointsInMap(
    pointReplacementMap: Map[IPoint2D, IPoint2D],
    annotatedShape: AnnotatedShapeImplementation): MultiLine = {
    val replacemetMultiLine = new MultiLine(this.getAnnotatedShape())
    var lastOldPoint: IPoint2D = null
    getPoints().foreach { (point: IPoint2D) =>
      //Annotate point
      val newPoint: IPoint2D = getPointWithDefault(pointReplacementMap, point)
      replacemetMultiLine.addAfterEnd(newPoint)
      //Annotate line
      if (lastOldPoint != null) {
        val oldLine: CLine = CLine.makeUnordered(lastOldPoint, point)
        oldLine.replacePointsInMap(pointReplacementMap, annotatedShape)
      }
      lastOldPoint = point
    }
    var annotationForOldMultiLine: Set[Object] = null
    if (annotatedShape != null)
      annotationForOldMultiLine = annotatedShape.getAnnotationForShapes(this)
    if (annotationForOldMultiLine != null) {
      annotatedShape.putAllAnnotation(replacemetMultiLine, annotationForOldMultiLine)
    }
    replacemetMultiLine
  }

  override def getCenter(): IPoint2D = {
    _bBox.getCenter()
  }

  override def getDiameter(): Double = {
    getBBox().getDiameter()
  }

  def isClosedLineClockWise(): Boolean = {
    _closedLineClockWise
  }

  def setClosedLineClockWise(lineClockWise: Boolean): Unit = {
    _closedLineClockWise = lineClockWise
  }

  def internalInfo(sb: StringBuffer): String = {
    sb.append("\nMultiLine:\n")
    _points.foreach { (point: IPoint2D) =>
      sb.append(point.toString()).append("\n")
    }
    sb.toString()
  }

}
