package org.shapelogic.sc.calculation

/**
 * Top interface for calculations.
 *
 * @author Sami Badawi
 *
 */
trait CalcValue[T] {
  /**
   * Starts a lazy calculation.
   * If dirty do calc() else return cached value.
   *
   *  This should maybe be moved up in the hierarchy
   */
  def getValue(): T
}
