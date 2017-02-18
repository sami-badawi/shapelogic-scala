package org.shapelogic.sc.util

import org.shapelogic.sc.polygon.IPoint2D

import scala.collection.Map

/**
 * Map Operations utility class.<br />
 *
 * XXX this should not contain references to any other packages.
 *
 * @author Sami Badawi
 *
 */
object MapOperations {
  /**
   * Get from map with Default. Where default == input key.
   *
   * XXX IPoint2D is from another package.
   */
  def getPointWithDefault(map: Map[IPoint2D, IPoint2D], point: IPoint2D): IPoint2D = {
    val result: IPoint2D = map.getOrElse(point, null)
    if (result == null)
      point
    else
      result
  }
}
