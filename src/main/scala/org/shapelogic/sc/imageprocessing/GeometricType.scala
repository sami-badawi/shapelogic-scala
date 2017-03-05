package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.OHInterface

case class GeometricType(name: String, id: Int) extends OHInterface {
  override def getOhName(): String = {
    name
  }
}

object GeometricType {

  // Points have number between 0 and 1000
  val BACKGROUND_POINT = GeometricType("BACKGROUND_POINT", 0) //Background point, , cross index of 0

  val T_JUNCTION = GeometricType("T_JUNCTION", 13)

  val Y_JUNCTION = GeometricType("Y_JUNCTION", 15)

  val PIXEL_LINE_END = GeometricType("PIXEL_LINE_END", 237) // Marked with E in diagrams. Normal point, 1 neighbors, cross index of 2

  val PIXEL_SINGLE_POINT = GeometricType("PIXEL_SINGLE_POINT", 239) //Single point, , cross index of 0

  val PIXEL_SOLID = GeometricType("PIXEL_SOLID", 241) //Inner point, 8 neighbors or 7 where the last on is an even number.

  val PIXEL_EXTRA_NEIGHBOR = GeometricType("PIXEL_EXTRA_NEIGHBOR", 199) //More neighbors, more than 2 neighbors, cross index of 4

  val PIXEL_ON_LINE = GeometricType("PIXEL_ON_LINE", 201) // Marked with P in diagrams. Normal point, 2 neighbors, cross index of 4

  val PIXEL_BORDER = GeometricType("PIXEL_BORDER", 247) //Edge of solid, cross index of 2

  val PIXEL_JUNCTION = GeometricType("PIXEL_JUNCTION", 249) //Junction point, more than cross index of 4

  val PIXEL_L_CORNER = GeometricType("PIXEL_L_CORNER", 251) //L corner, 2 neighbors with modulo distance either 2 or 6, cross index of 4

  val PIXEL_V_CORNER = GeometricType("PIXEL_V_CORNER", 253) //A corner, 2 neighbors, cross index of 2, should always be next to a junction

  val PIXEL_FOREGROUND_UNKNOWN = GeometricType("PIXEL_FOREGROUND_UNKNOWN", 255) //Before it is calculated

  // Annotations for lines
  val NORMAL_LINE = GeometricType("NORMAL_LINE", 1001)

  val CIRCLE = GeometricType("CIRCLE", 1003)

  val ARCH = GeometricType("ARCH", 1005)

  val SPIRAL = GeometricType("SPIRAL", 1007)

  val S_SHAPE = GeometricType("S_SHAPE", 1009)

  val STRAIGHT = GeometricType("STRAIGHT", 1011)

  val WEAVE = GeometricType("WEAVE", 1013)
}
