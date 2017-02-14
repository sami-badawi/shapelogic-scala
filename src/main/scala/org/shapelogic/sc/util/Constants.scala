package org.shapelogic.sc.util

/**
 * This just contains a lot of constants.
 * This should help prevent cyclic dependencies.
 *
 * @author Sami Badawi
 *
 */
object Constants {
  val releaseVersion = "0.7.3"

  val PRECISION: Double = 0.001
  val BYTE_MASK: Int = 255

  val DIRECTION_NOT_USED: Byte = -1
  val DIRECTIONS_AROUND_POINT: Int = 8
  val DIRECTIONS_4_AROUND_POINT: Int = 4
  val BEFORE_START_INDEX: Int = -1
  val START_INDEX: Int = 0
  val ZERO = new Integer(0)
  /** What you need to add to the x coordinate to get to the indexed point */
  val CYCLE_POINTS_X: Array[Int] = Array(1, 1, 0, -1, -1, -1, 0, 1)
  /** What you need to add to the y coordinate to get to the indexed point */
  val CYCLE_POINTS_Y: Array[Int] = Array(0, 1, 1, 1, 0, -1, -1, -1)

  //Directions
  val RIGHT: Int = 0
  val RIGHT_DOWN: Int = 1
  val DOWN: Int = 2
  val LEFT_DOWN: Int = 3
  val LEFT: Int = 4
  val LEFT_UP: Int = 5
  val UP: Int = 6
  val RIGHT_UP: Int = 7

  //Equality test tasks
  val SIMPLE_NUMERIC_TASK = "SimpleNumericTask"
  val COUNT_COLLECTION_TASK = "CountCollectionTask"
  val FILTER_COUNT_TASK = "FilterCountTask"

  //Boolean test tasks
  val BOOLEAN_TASK = "BooleanTask"

  //Greater test tasks
  val NUMERIC_GREATER_TASK = "NumericGreaterTask"
  val COUNT_COLLECTION_GREATER_TASK = "CountCollectionGreaterTask"
  val FILTER_COUNT_GREATER_TASK = "FilterCountGreaterTask"

  //A more general task that can create all the other tasks, by taking 2 parameters
  val PARAMETRIC_RULE_TASK = "ParametricRuleTask"
  val TOLERANCE: Double = 0.0001

  //Missing
  val LAST_UNKNOWN: Int = -2
  val NO_OH = ""

  //Names of Boolean operations
  val AND = "AND"
  val OR = "OR"
  val XOR = "XOR"

  /** Suffix to function name based on stream name. */
  val FUNCTION_NAME_SUFFIX = "_FUNCTION_"

  /** Names of scripting languages in JSR 223. */
  val GROOVY = "groovy"
  val JAVASCRIPT = "javascript"

  /** Names used for polygons in matches. Might be moved again. */
  val POLYGON = "polygon"
  val RAW_POLYGON = "rawPolygon"
}