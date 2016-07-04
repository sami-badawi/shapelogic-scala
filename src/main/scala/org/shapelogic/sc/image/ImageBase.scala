package org.shapelogic.sc.image

/**
 * First version of real classes
 * Might be changed to be a typeclass in Cats later
 */
trait ImageBase[@specialized T] extends ReadImage[T] {

  def setChannel(x: Int, y: Int, ch: Int, value: T): Unit

  def setPixel(x: Int, y: Int, value: Array[T]): Unit
  /**
   * Fill whole image with color
   */
  def fill(value: T): Unit
}
