package org.shapelogic.sc.polygon

/**
 * So the idea is that you should have a slice of the circle
 * Since it is circular it does not really make sense to talk about:
 * min and max
 *
 * what is the equivalent
 *
 * I guess start and end assuming that you go in normal direction of increasing angles.
 *
 * This will often be the same as min and max, but sometimes there is a cross over.
 *
 * That really just mean that check for angle in [max; 2 * PI[ and [0; min]
 * Otherwise it is check [min; max].
 *
 * How do I make it be the full circle?
 *
 * Is this a closed interval?
 *
 * For now do not create getter and setter
 *
 * @author Sami Badawi
 *
 */
class CircleInterval {
  import CircleInterval._

  var start: Double = 0
  var end: Double = 0
  var empty: Boolean = true

  def isContained(angle: Double): Boolean = {
    return true
  }

  def addClosestAngle(angleIn: Double): Unit = {
    var angle = normalizeAngle(angleIn)
    if (empty) {
      start = angle
      end = angle
      empty = false
      return
    }
    if (contains(angle))
      return
    val distToStart: Double = angleDistance(start, angle)
    val distToEnd: Double = angleDistance(end, angle)
    if (start == end) {
      if (angleDistance(start, angle) < angleDistance(angle, start))
        addGrowingAngle(angle)
      else
        addFallingAngle(angle)
    } else if (distToStart < distToEnd)
      addGrowingAngle(angle)
    else
      addFallingAngle(angle)
  }

  /** Turn new angle into start point */
  def addGrowingAngle(angleIn: Double): Unit = {
    val angle = normalizeAngle(angleIn)
    if (empty) {
      start = angle
      end = angle
      empty = false
      return
    }
    if (contains(angle))
      return
    start = angle
  }

  /** Turn new angle into end point */
  def addFallingAngle(angleIn: Double): Unit = {
    val angle = normalizeAngle(angleIn)
    if (empty) {
      start = angle
      end = angle
      empty = false
      return
    }
    if (contains(angle))
      return
    end = angle
  }

  def containsZero(): Boolean = {
    if (empty)
      return false
    return start > end
  }

  def contains(angleIn: Double): Boolean = {
    var angle = normalizeAngle(angleIn)
    if (empty)
      return false
    if (!containsZero()) {
      return start <= angle && angle <= end
    } else {
      return start <= angle || angle <= end
    }
  }

  def intervalLength(): Double = {
    if (empty)
      return 0.0
    if (containsZero())
      return Math.PI * 2 + end - start
    else
      return end - start
  }

}

object CircleInterval {
  def normalizeAngle(angle: Double): Double = {
    return angle % (Math.PI * 2)
  }

  /** signed angle from angle1 to angle2 */
  def angleDistance(angle1: Double, angle2: Double): Double = {
    var dist: Double = angle2 - angle1
    if (dist > 2 * Math.PI)
      dist = dist % (2 * Math.PI)
    return dist
  }
}
