package org.shapelogic.sc.util

import org.shapelogic.sc.util.Constants.PRECISION;

/**
 * There are precision issues when doing calculations with Double
 *
 * @author Sami Badawi
 *
 */
object DoubleCalculations {

  def doubleEquals(x: Double, y: Double): Boolean = {
    return Math.abs(x - y) < PRECISION;
  }

  def doubleZero(x: Double): Boolean = {
    return Math.abs(x) < PRECISION;
  }

  def doubleIsInt(x: Double): Boolean = {
    return Math.abs(x - Math.round(x)) < PRECISION;
  }

  def isEven(input: Double): Boolean = {
    val inputLong = input.toLong
    return (inputLong & 1) != 0;
  }

  /** If one is 0 and other not return false */
  def oppositeSign(d1: Double, d2: Double): Boolean = {
    if (Math.abs(d1) <= PRECISION || Math.abs(d2) <= PRECISION) {
      return false;
    }
    return (d1 < 0) != (d2 < 0);
  }

  /** If one is 0 and other not return false */
  def sameSign(d1: Double, d2: Double): Boolean = {
    if (Math.abs(d1) <= PRECISION || Math.abs(d2) <= PRECISION) {
      if (Math.abs(d1) <= PRECISION && Math.abs(d2) <= PRECISION)
        return true;
      return false;
    }
    return (d1 < 0) == (d2 < 0);
  }
}
