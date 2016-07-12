package org.shapelogic.sc.image

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

/**
 * Concrete implementation of image supported by unboxed primitive array
 */
@deprecated("BufferImage is more general, use that","2016-07-11")
class GrayNumImage[@specialized(Byte, Short, Int, Float, Double) N: ClassTag](
    val width: Int,
    val height: Int,
    bufferInput: Array[N]) extends ImageBase[N] {
  def frozen: Boolean = false

  def numBands: Int = 1

  lazy val bufferLenght = height * width

  val data: Array[N] = if (bufferInput == null) new Array[N](bufferLenght) else bufferInput

  def getIndex(x: Int, y: Int): Int = {
    width * y + x
  }

  def getChannel(x: Int, y: Int, ch: Int): N = {
    data(width * y + x)
  }

  def getPixel(x: Int, y: Int): Array[N] = {
    Array[N](getChannel(x, y, ch = 0))
  }

  def setPixel(x: Int, y: Int, value: Array[N]): Unit = {
    if (!frozen)
      data(width * y + x) = value(0)
  }

  def setChannel(x: Int, y: Int, ch: Int, value: N): Unit = {
    if (!frozen)
      data(width * y + x) = value
  }

  def fill(value: N): Unit = {
    var i = 0
    while (i < bufferLenght) {
      data(i) = value
      i += 1
    }
  }

  def rgbOffsetsOpt: Option[RGBOffsets] = Some(grayRGBOffsets)
}
