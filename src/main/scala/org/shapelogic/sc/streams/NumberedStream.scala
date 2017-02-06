package org.shapelogic.sc.streams

/**
 * NumberedStream is a Sequential Stream where each element has an intrinsic number.
 *
 * @author Sami Badawi
 *
 */
trait NumberedStream[E] extends Stream[E] {

  /**
   * Get the calculate value for index based on the previous stream. <br />
   * <br />
   * So it just calls
   * invoke(getInputStream().get(index), index)
   *
   * @param index in
   * @return
   */
  def get(input: Int): E

  /** Index of last successfully processed element. */
  def getIndex(): Int

  /**
   * Last possible element. <br />
   *
   * If last is not known then this is set to LAST_UNKNOWN.<br />
   * When you add a new element this will not grow.<br />
   * When maxLast is set so it this, but this can can get lower.<br />
   * Set when you find the end.<br />
   * <br />
   * If you use this for iteration you need to call the function at each
   * iteration.
   */
  def getLast(): Int

  /** Manually set max value for last possible element. */
  def getMaxLast(): Int

  /** Set a max value for last possible element. */
  def setMaxLast(maxLast: Int): Unit
}