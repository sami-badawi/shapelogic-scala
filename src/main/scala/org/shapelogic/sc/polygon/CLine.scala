package org.shapelogic.sc.polygon

import scala.collection.mutable.Buffer
import java.util.Collection
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Set
import collection.JavaConverters._
import org.shapelogic.sc.util.DoubleCalculations._
import org.shapelogic.sc.util.MapOperations._

import scala.collection.mutable.Map
import scala.collection.Map

/**
 * Class for line.
 *
 * Works for both Double and int based points.
 * Can be used for both undirected and directed lines
 *
 * I think this is made to be immutable, but points are not.
 * But in order to have the sort work for lines I have to assume that the
 * points in it are not moving.
 *
 * @author Sami Badawi
 *
 */

object CLine {
  val HORIZONTAL_VERTICAL_RELATION: Int = 10

  def makeUnordered(p1: IPoint2D, p2: IPoint2D): CLine = {
    if (p1.compareTo(p2) < 1) {
      new CLine(p1, p2)
    } else {
      new CLine(p2, p1)
    }
  }

  def filterHorizontal(inputLines: Seq[CLine]): Seq[CLine] = {
    inputLines.toSeq.filter(_.isHorizontal)
  }

  def filterVertical(inputLines: Seq[CLine]): Seq[CLine] = {
    inputLines.toSeq.filter(_.isVertical)
  }

}

class CLine(val _start: IPoint2D, val _end: IPoint2D) extends ILine2D with PointReplacable[CLine] {
  import CLine._

  override def getEnd(): IPoint2D = {
    _end
  }

  override def getStart(): IPoint2D = {
    _start
  }

  override def compareTo(that: ILine2D): Int = {
    val result = getStart().compareTo(that.getStart())
    if (result != 0)
      result
    else
      getEnd().compareTo(that.getEnd())
  }

  override def hashCode(): Int = {
    var result = 0
    if (_start != null)
      result = _start.hashCode()
    if (_end != null)
      result += _end.hashCode() * 17
    result
  }

  override def equals(that: Any): Boolean = {
    if (that == null)
      return false
    if (!(that.isInstanceOf[CLine]))
      return false
    val thatCLine = that.asInstanceOf[CLine]
    _start.equals(thatCLine.getStart()) && _end.equals(thatCLine.getEnd())
  }

  def relativePoint(): IPoint2D = {
    _end.copy().minus(_start)
  }

  /**
   * Should return numbers in the range 0 to PI.
   *
   * @return the angle in redians
   */
  def angle(): Double = {
    val relativePointV: IPoint2D = relativePoint()
    relativePointV.angle()
  }

  def isVertical(): Boolean = {
    val relativePointV: IPoint2D = relativePoint()
    (Math.abs(relativePointV.getX()) * HORIZONTAL_VERTICAL_RELATION <= Math.abs(relativePointV.getY()))
  }

  def isHorizontal(): Boolean = {
    val relativePointV: IPoint2D = relativePoint()
    (Math.abs(relativePointV.getX()) >= Math.abs(relativePointV.getY()) * HORIZONTAL_VERTICAL_RELATION)
  }

  def isPoint(): Boolean = {
    val relativePointV = relativePoint()
    doubleZero(relativePointV.getX()) && doubleZero(relativePointV.getY())
  }

  def distance(): Double = {
    val xDist = getStart().getX() - getEnd().getX()
    val yDist = getStart().getY() - getEnd().getY()
    Math.sqrt(xDist * xDist + yDist * yDist)
  }

  override def toString(): String = {
    "[Line: " + _start.toString() + "," + _end.toString() + "]"
  }

  override def replacePointsInMap(pointReplacementMap: scala.collection.mutable.Map[IPoint2D, IPoint2D],
    annotatedShape: AnnotatedShapeImplementation): CLine = {
    val newStartPoint: IPoint2D = getPointWithDefault(pointReplacementMap, getStart())
    val newEndPoint: IPoint2D = getPointWithDefault(pointReplacementMap, getEnd())
    var newLine: CLine = null
    if (newStartPoint == getStart() && newEndPoint == getEnd())
      newLine = this
    else
      newLine = CLine.makeUnordered(newStartPoint, newEndPoint)
    if (!newLine.isPoint()) {
      getStart().replacePointsInMap(pointReplacementMap, annotatedShape)
      getEnd().replacePointsInMap(pointReplacementMap, annotatedShape)
      var annotationForLine: Set[Object] = null
      if (annotatedShape != null)
        annotationForLine = annotatedShape.getAnnotationForShapes(this)
      if (annotationForLine != null) {
        annotatedShape.putAllAnnotation(newLine, annotationForLine)
      }
    }
    newLine
  }

  /** Just order the points in this line alphabetically. */
  def orderedLine(): CLine = {
    if (getStart().compareTo(getEnd()) < 1)
      this
    else
      new CLine(getEnd(), getStart())
  }

  /** Check if the points in this line is ordered alphabetically */
  def isLineOrdered(): Boolean = {
    getStart().compareTo(getEnd()) < 1
  }

  override def getCenter(): IPoint2D = {
    val center: IPoint2D = _start.copy().add(_end)
    if (center.isInstanceOf[CPointInt]) {
      val x = center.getX().toInt
      val y = center.getY().toInt
      if (isEven(x) || isEven(y)) {
        return new CPointDouble(x * 0.5, y * 0.5)
      }
    }
    center.multiply(0.5)
    center
  }

  override def getDiameter(): Double = {
    distance()
  }

  def lineStartingAtPoint(point: IPoint2D): CLine = {
    if (getStart().equals(point))
      this
    else
      oppositeDirectionLine() // opposite Direction
  }

  def oppositeDirectionLine(): CLine = {
    new CLine(_end, _start)
  }
}
