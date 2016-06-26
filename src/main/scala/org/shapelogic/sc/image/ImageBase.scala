package org.shapelogic.sc.image

/**
 * First version of real classes
 * Might be changed to be a typeclass in Cats later
 */
trait ImageBase[@specialized T] {
  def frozen: Boolean

  def channels: Int

  def getIndex(x: Int, y: Int, ch: Int): Int

  def getChannel(x: Int, y: Int, ch: Int): T

  def getPixel(x: Int, y: Int): Array[T]

  def setChannel(x: Int, y: Int, ch: Int, value: T): Unit

  def setPixel(x: Int, y: Int, value: Array[T]): Unit
  /**
   * Fill whole image with color
   */
  def fill(value: T): Unit
}
