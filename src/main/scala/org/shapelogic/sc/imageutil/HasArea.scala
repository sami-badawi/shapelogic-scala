package org.shapelogic.sc.imageutil

/**
 * For anything that can have an area.<br />
 *
 * This might be changed to return double at some point.
 *
 * @author Sami Badawi
 */
trait HasArea {

  /** Area of this color range. */
  def getArea(): Int

}
