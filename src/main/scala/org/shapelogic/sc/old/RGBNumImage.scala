package org.shapelogic.sc.old

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import spire.algebra._
import spire.std._
import spire.syntax.ring._
import spire.implicits._
import org.shapelogic.sc.image._

/**
 * Concrete implementation of image supported by unboxed primitive array
 */
@deprecated("BufferImage is more general, use that", "2016-07-11")
class RGBNumImage[@specialized(Byte, Short, Int, Float, Double) N: ClassTag](
    val width: Int,
    val height: Int, bufferIn: Array[N]) extends WriteImage[N] {
  def frozen: Boolean = false

  def numBands: Int = 3

  lazy val bufferLenght = height * width * numBands

  val data: Array[N] = if (bufferIn == null) new Array[N](bufferLenght) else bufferIn

  def getIndex(x: Int, y: Int): Int = {
    (width * y + x) * numBands
  }

  def getChannel(x: Int, y: Int, ch: Int): N = {
    data((width * y + x) * numBands + ch)
  }

  def getPixel(x: Int, y: Int): Array[N] = {
    val index = (width * y + x) * numBands
    Array[N](data(index), data(index + 1), data(index + 2))
  }

  def setChannel(x: Int, y: Int, ch: Int, value: N): Unit = {
    if (!frozen)
      data((width * y + x) * numBands + ch) = value
  }

  def setPixel(x: Int, y: Int, value: Array[N]): Unit = {
    if (!frozen) {
      val index = getIndex(x, y)
      cfor(0)(_ < numBands, _ + 1) { i =>
        data(index + i) = value(i)
      }
    }
  }

  def fill(value: N): Unit = {
    var i = 0
    while (i < bufferLenght) {
      data(i) = value
      i += 1
    }
  }

  def rgbOffsetsOpt: Option[RGBOffsets] = Some(rgbRGBOffsets)

  def isInBounds(x: Int, y: Int): Boolean = {
    0 <= x && x < width && 0 <= y && y < height
  }
}
