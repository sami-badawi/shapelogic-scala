package org.shapelogic.sc.imageprocessing

/**
 * LineProperties contains properties that are important for a point
 * when you are dealing with curved multi line.
 *
 * @author Sami Badawi
 *
 * Properties to keep track of
 * Direction change for 2 adjacent lines, when following the multi line
 * Sharp corners
 *
 */
class PointProperties {
  var directionChange: Double = 0
  var sharpCorner: Boolean = false
}
