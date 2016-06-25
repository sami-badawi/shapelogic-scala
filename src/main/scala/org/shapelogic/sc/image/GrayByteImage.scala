package org.shapelogic.sc.image

class GrayByteImage(width: Int, height: Int) extends ImageBase[Byte] {
  def frozen: Boolean = false

  lazy val bufferLenght = height * width

  val buffer: Array[Byte] = new Array[Byte](bufferLenght)

  def getChannel(x: Int, y: Int, ch: Int): Byte = {
    buffer(width * y + x)
  }

  def setChannel(x: Int, y: Int, ch: Int, value: Byte): Unit = {
    if (!frozen)
      buffer(width * y + x) = value
  }

  def fill(value: Byte): Unit = {
    var i = 0
    while (i < bufferLenght) {
      buffer(i) = value
      i += 1
    }
  }
}
