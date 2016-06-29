package org.shapelogic.sc.image

/**
 * Similar to GrayNumImage but simpler in case there is problem with the type system
 */
class GrayByteImage(width: Int, height: Int) extends ImageBase[Byte] {
  def frozen: Boolean = false

  def channels: Int = 1

  lazy val bufferLenght = height * width

  val buffer: Array[Byte] = new Array[Byte](bufferLenght)

  def getIndex(x: Int, y: Int, ch: Int): Int = {
    width * y + x
  }

  def getChannel(x: Int, y: Int, ch: Int): Byte = {
    buffer(width * y + x)
  }

  def getPixel(x: Int, y: Int): Array[Byte] = {
    Array[Byte](getChannel(x, y, ch = 0))
  }

  def setChannel(x: Int, y: Int, ch: Int, value: Byte): Unit = {
    if (!frozen)
      buffer(width * y + x) = value
  }

  def setPixel(x: Int, y: Int, value: Array[Byte]): Unit = {
    if (!frozen)
      buffer(width * y + x) = value(0)
  }

  def fill(value: Byte): Unit = {
    var i = 0
    while (i < bufferLenght) {
      buffer(i) = value
      i += 1
    }
  }
}
