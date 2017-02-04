package org.shapelogic.sc.polygon

/**
 *
 * @author Sami Badawi
 *
 */
trait GeometricShape2D {
  def getCenter(): IPoint2D
  def getDiameter(): Double
}
