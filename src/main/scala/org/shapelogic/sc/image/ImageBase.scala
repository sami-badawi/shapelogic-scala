package org.shapelogic.sc.image

trait ImageBase[@specialized T] {
  def frozen: Boolean

  def channels: Int
  
  def getChannel(x: Int, y: Int, ch: Int): T

  def setChannel(x: Int, y: Int, ch: Int, value: T): Unit
  
  /**
   * Fill whole image with color
   */
  def fill(value: T): Unit
}
