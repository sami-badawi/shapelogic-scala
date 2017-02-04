package org.shapelogic.sc.polygon

import scala.collection.Set

/**
 * there is only one class implementing this I should probably take it out
 *
 * @author Sami Badawi
 *
 */
trait IPolygon2D extends GeometricShape2D {
  def getPoints(): Set[_ <: IPoint2D]
  def getLines(): Set[CLine]
  def getBBox(): BBox
  def isClosed(): Boolean
  def getAspectRatio(): Double
}
