package org.shapelogic.sc.polygon

import java.lang.Comparable

/**
 * there is only one class implementing this I should probably take it out
 *
 * @author Sami Badawi
 *
 */
trait ILine2D extends Comparable[ILine2D] with GeometricShape2D {
  def getStart(): IPoint2D
  def getEnd(): IPoint2D
}
