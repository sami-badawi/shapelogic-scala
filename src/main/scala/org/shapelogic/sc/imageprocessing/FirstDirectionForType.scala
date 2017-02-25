package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants

/**
 * To keep track of if a pixel has neighbors of a given type of pixel.
 *
 * Simple enough not to use getter and setters.
 *
 * @author Sami Badawi
 */
class FirstDirectionForType {
  var count: Int = 0
  var countUsed: Int = 0
  var firstDirection: Byte = Constants.DIRECTION_NOT_USED
  var firstUsedDirection: Byte = Constants.DIRECTION_NOT_USED

  def addDirection(direction: Byte, used: Boolean): Unit = {
    if (used) {
      countUsed += 1
      if (firstUsedDirection == Constants.DIRECTION_NOT_USED) {
        firstUsedDirection = direction
      }
    } else {
      count += 1
      if (firstDirection == Constants.DIRECTION_NOT_USED) {
        firstDirection = direction
      }
    }
  }

  /** At lease on instance of this type present. */
  def isTypePresent(): Boolean = {
    return firstDirection != Constants.DIRECTION_NOT_USED
  }

  def setup(): Unit = {
    count = 0
    firstDirection = Constants.DIRECTION_NOT_USED
  }

  def countAll(): Int = {
    return countUsed + count
  }
}