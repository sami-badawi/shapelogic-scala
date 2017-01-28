package org.shapelogic.sc.polygon

import java.awt.Point
import java.awt.geom.Point2D

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import org.shapelogic.sc.util.MapOperations

/**
 * Simple point that is comparable and has arithmetic operations.
 *
 * C Point Int means Comparable Point based on integers
 *
 * Sub classed from Java2D Point
 *
 * @author Sami Badawi
 *
 */
class CPointInt(xIn: Int, yIn: Int) extends Point(xIn, yIn) with IPoint2D {
  //	private static final long serialVersionUID = 1L;

  def this(point: Point2D) = {
    this(point.getX().toInt, point.getY().toInt)
  }

  override def compareTo(that: IPoint2D): Int = {
    if (getX() > that.getX()) return 1
    else if (getX() < that.getX()) return -1
    else if (getY() > that.getY()) return 1
    else if (getY() < that.getY()) return -1
    else return 0
  }

  /** Subtract other point from this */
  def minus(that: IPoint2D): IPoint2D = {
    translate(-that.getX().toInt, -that.getY().toInt)
    return this;
  }

  /** Add other point into this */
  def add(that: IPoint2D): IPoint2D = {
    translate(that.getX().toInt, that.getY().toInt);
    return this;
  }

  /** Multiply number with each coordinate of this */
  def multiply(multiplier: Double): IPoint2D = {
    setLocation((getX() * multiplier).toInt, ((getY() * multiplier)).toInt);
    this;
  }

  /** Test if point is (0,0) */
  def isNull(): Boolean = {
    return 0 == getX() && 0 == getY();
  }

  /** Create Double array with this info in */
  def toDoubleArray(): Array[Double] = {
    return Array[Double](getX(), getY())
  }

  override def max(that: IPoint2D): IPoint2D = {
    if (this.compareTo(that) <= 0)
      return this;
    else
      return that;
  }

  override def min(that: IPoint2D): IPoint2D = {
    if (this.compareTo(that) >= 0)
      return this;
    else
      return that;
  }

  override def distance(that: IPoint2D): Double = {
    val xDist = getX() - that.getX();
    val yDist = getY() - that.getY();
    return Math.sqrt(xDist * xDist + yDist * yDist)
  }

  override def round(): IPoint2D = {
    return this;
  }

  override def copy(): IPoint2D = {
    return clone().asInstanceOf[IPoint2D]
  }

  def angle(): Double = {
    if (getX() == 0 && getY() == 0)
      return java.lang.Double.NaN;
    return Math.atan2(getY(), getX());
  }

  override def isOnAxis(): Boolean = {
    return getX() == 0 || getY() == 0;
  }

  override def isOnDiagonal(): Boolean = {
    return Math.abs(getX()) == Math.abs(getY());
  }

  override def distanceFromOrigin(): Double = {
    val xDist = getX();
    val yDist = getY();
    return Math.sqrt(xDist * xDist + yDist * yDist);
  }

  override def turn90(): IPoint2D = {
    return new CPointInt(-getY().toInt, getX().toInt);
  }

  override def getCenter(): IPoint2D = {
    return this;
  }

  override def getDiameter(): Double = {
    return 1.0
  }

  override def replacePointsInMap(
    pointReplacementMap: Map[IPoint2D, IPoint2D],
    annotatedShape: AnnotatedShapeImplementation): IPoint2D = {
    val newPoint = MapOperations.getPointWithDefault(pointReplacementMap, this);
    var annotationForOldPoint: Set[Object] = null;
    if (annotatedShape != null)
      annotationForOldPoint = annotatedShape.getAnnotationForShapes(this);
    if (annotationForOldPoint != null) {
      annotatedShape.putAllAnnotation(newPoint, annotationForOldPoint);
    }
    return newPoint;
  }

  override def toString(): String = {
    return "[CPointInt " + x + ", " + y + "]";
  }
}
