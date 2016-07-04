package org.shapelogic.sc.image

/**
 * First version of real classes
 * Might be changed to be a typeclass in Cats later
 */
trait ReadImage[@specialized T] {
  def frozen: Boolean

  def channels: Int

  /**
   * Get the first channel if this is byte array
   * If it is an Int array with bytes packed in it would be the Int
   */
  def getIndex(x: Int, y: Int): Int

  def getChannel(x: Int, y: Int, ch: Int): T

  def getPixel(x: Int, y: Int): Array[T]
}
