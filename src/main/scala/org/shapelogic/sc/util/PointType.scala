package org.shapelogic.sc.util

case class PointType(name: String) extends OHInterface {
  override def getOhName(): String = {
    name
  }
}

/**
 * Enum for with types for Points.
 *
 * This could change quite a bit
 *
 * @author Sami Badawi
 */
object PointType {
  //  type PointType = Value
  /** Before a type is determined */
  val UNKNOWN = PointType("UNKNOWN")

  /** If the point has a sharp angle for now 30 degrees is set as the limit */
  val HARD_CORNER = PointType("HARD_CORNER")

  /** If it is not a hard point */
  val SOFT_POINT = PointType("SOFT_POINT")

  /** 3 or more lines meets, U is for unknown */
  val U_JUNCTION = PointType("U_JUNCTION")

  /** 3 lines meet 2 are collinear and the last is somewhat orthogonal */
  val T_JUNCTION = PointType("T_JUNCTION")

  /** 3 lines meet less than 180 degrees between all of them */
  val ARROW_JUNCTION = PointType("ARROW_JUNCTION")

  /** 3 lines meet, not a T junction */
  val Y_JUNCTION = PointType("Y_JUNCTION")

  /**
   * point is an end point,
   * maybe later there should be a distinction between end points and have
   * nothing else close by and end points that have close neighbors
   */
  val END_POINT = PointType("END_POINT")

}
