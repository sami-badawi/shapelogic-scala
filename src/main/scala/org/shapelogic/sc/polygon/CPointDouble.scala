package org.shapelogic.sc.polygon

import org.shapelogic.sc.util.MapOperations._

import java.awt.geom.Point2D

import org.shapelogic.sc.util.Constants

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import org.shapelogic.sc.util.MapOperations

/**
 * Simple point that is comparable and has arithmetic operations.
 *
 * Sub classed from Java2D Point2D.Double
 *
 * @author Sami Badawi
 *
 */
class CPointDouble(xIn: Double, yIn: Double) extends Point2D.Double(xIn, yIn) with IPoint2D {
  //	private static final long serialVersionUID = 1L

  def this(point: Point2D) {
    this(point.getX(), point.getY())
  }
  def this() {
    this(0, 0)
  }

  override def compareTo(that: IPoint2D): Int = {
    if (getX() > that.getX()) 1
    else if (getX() < that.getX()) -1
    else if (getY() > that.getY()) 1
    else if (getY() < that.getY()) -1
    else 0
  }

  /* (non-Javadoc)
	 * @see org.shapelogic.polygon.CPoint2D#minus(java.awt.geom.Point2D)
	 */
  def minus(that: IPoint2D): IPoint2D = {
    translate(-that.getX(), -that.getY())
    this
  }

  def translate(x: Double, y: Double): Unit = {
    setLocation(getX() + x, getY() + y)
  }

  /* (non-Javadoc)
	 * @see org.shapelogic.polygon.CPoint2D#add(java.awt.geom.Point2D)
	 */
  def add(that: IPoint2D): IPoint2D = {
    translate(that.getX().toInt, that.getY().toInt)
    this
  }

  /* (non-Javadoc)
	 * @see org.shapelogic.polygon.CPoint2D#multiply(Double)
	 */
  def multiply(multiplier: Double): IPoint2D = {
    setLocation((getX() * multiplier).toInt, (getY() * multiplier).toInt)
    this
  }

  /* (non-Javadoc)
	 * @see org.shapelogic.polygon.CPoint2D#isNull()
	 */
  def isNull(): Boolean = {
    Math.abs(getX()) <= Constants.PRECISION && Math.abs(getX()) <= Constants.PRECISION
  }

  /* (non-Javadoc)
	 * @see org.shapelogic.polygon.CPoint2D#toDoubleArray()
	 */
  def toDoubleArray(): Array[Double] = {
    Array[Double](getX(), getY())
  }

  override def max(that: IPoint2D): IPoint2D = {
    if (this.compareTo(that) >= 0)
      this
    else
      that
  }

  override def min(that: IPoint2D): IPoint2D = {
    if (this.compareTo(that) <= 0)
      this
    else
      that
  }

  override def distance(that: IPoint2D): Double = {
    val xDist = getX() - that.getX()
    val yDist = getY() - that.getY()
    Math.sqrt(xDist * xDist + yDist * yDist)
  }

  override def round(): IPoint2D = {
    setLocation(Math.round(getX()), Math.round(getY()))
    this
  }

  override def copy(): IPoint2D = {
    clone().asInstanceOf[IPoint2D]
  }

  override def toString(): String = {
    "[CPointDouble: " + getX() + ", " + getY() + "]"
  }

  def angle(): Double = {
    if (getX() == 0 && getY() == 0)
      java.lang.Double.NaN
    else
      Math.atan2(getY(), getX())
  }

  override def isOnAxis(): Boolean = {
    getX() == 0 || getY() == 0
  }

  override def isOnDiagonal(): Boolean = {
    Math.abs(getX()) == Math.abs(getY())
  }

  override def distanceFromOrigin(): Double = {
    val xDist = getX()
    val yDist = getY()
    Math.sqrt(xDist * xDist + yDist * yDist)
  }

  override def turn90(): IPoint2D = {
    new CPointDouble(-getY(), getX())
  }

  override def getCenter(): IPoint2D = {
    this
  }

  override def getDiameter(): Double = {
    1.0
  }

  override def replacePointsInMap(
    pointReplacementMap: Map[IPoint2D, IPoint2D],
    annotatedShape: AnnotatedShapeImplementation): IPoint2D = {
    val newPoint: IPoint2D = MapOperations.getPointWithDefault(pointReplacementMap, this)
    var annotationForOldPoint: Set[Object] = null
    if (annotatedShape != null)
      annotationForOldPoint = annotatedShape.getAnnotationForShapes(this)
    if (annotationForOldPoint != null) {
      annotatedShape.putAllAnnotation(newPoint, annotationForOldPoint)
    }
    newPoint
  }
}
