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
class RGBNumImage[@specialized(Byte, Short, Int, Float, Double) N: ClassTag](
    width: Int, height: Int, bufferIn: Array[N]) extends ImageBase[N] {
  def frozen: Boolean = false

  def numBands: Int = 3

  lazy val bufferLenght = height * width * numBands

  val buffer: Array[N] = if (bufferIn == null) new Array[N](bufferLenght) else bufferIn

  def getIndex(x: Int, y: Int): Int = {
    (width * y + x) * numBands
  }

  def getChannel(x: Int, y: Int, ch: Int): N = {
    buffer((width * y + x) * numBands + ch)
  }

  def getPixel(x: Int, y: Int): Array[N] = {
    val index = (width * y + x) * numBands
    Array[N](buffer(index), buffer(index + 1), buffer(index + 2))
  }

  def setChannel(x: Int, y: Int, ch: Int, value: N): Unit = {
    if (!frozen)
      buffer((width * y + x) * numBands + ch) = value
  }

  def setPixel(x: Int, y: Int, value: Array[N]): Unit = {
    if (!frozen) {
      val index = getIndex(x, y)
      cfor(0)(_ < numBands, _ + 1) { i =>
        buffer(index + i) = value(i)
      }
    }
  }

  def fill(value: N): Unit = {
    var i = 0
    while (i < bufferLenght) {
      buffer(i) = value
      i += 1
    }
  }
}
