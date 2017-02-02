package org.shapelogic.sc.calculation

/**
 * Interface for calculations with a dirty and setup concept.
 *
 * @author Sami Badawi
 *
 */
trait LazyCalc[T] extends CalcValue[T] {

  /**
   * When dirty is false that means that the calculated value can be used
   */
  def isDirty(): Boolean

  /**
   * Currently not super well define used for 2 purposes:
   * Reset: called from the outside if you want to reuse it
   *
   * init: Called from the inside when calculation start to have everything setup
   *
   * maybe the semantic could work for both?
   *
   * TODO: This should be separated better.
   */
  def setup(): Unit
}
