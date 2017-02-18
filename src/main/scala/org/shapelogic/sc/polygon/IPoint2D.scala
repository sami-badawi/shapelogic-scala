package org.shapelogic.sc.polygon

/**
 *
 * @author Sami Badawi
 *
 */
trait IPoint2D extends Comparable[IPoint2D] with Cloneable with GeometricShape2D with PointReplacable[IPoint2D] {

  def setLocation(x: Double, y: Double): Unit

  def minus(that: IPoint2D): IPoint2D

  def add(that: IPoint2D): IPoint2D

  def multiply(multiplier: Double): IPoint2D

  def isNull(): Boolean

  def toDoubleArray(): Array[Double]

  def getX(): Double

  def getY(): Double

  def min(that: IPoint2D): IPoint2D

  def max(that: IPoint2D): IPoint2D

  def distance(that: IPoint2D): Double

  def distanceFromOrigin(): Double

  def round(): IPoint2D

  def copy(): IPoint2D

  def angle(): Double

  def isOnAxis(): Boolean

  def isOnDiagonal(): Boolean

  def turn90(): IPoint2D
}