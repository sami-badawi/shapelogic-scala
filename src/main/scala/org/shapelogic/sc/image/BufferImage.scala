package org.shapelogic.sc.image

import simulacrum._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
 * Work horse buffer image
 * This will take care of most cases
 */
class BufferImage[@specialized T: ClassTag](
    val width: Int,
    val height: Int,
    val numBands: Int,
    bufferInput: Array[T] = null,
    val rgbOffsetsOpt: Option[RGBOffsets] = None) extends ImageBase[T] with BufferImageTrait[T] {

  /**
   * Number of positions between pixel in new row
   */
  lazy val stride: Int = width * numBands

  /**
   * If there is a subimage
   */
  lazy val startIndex: Int = 0

  lazy val bufferLenght = height * stride

  /**
   * Get the first channel if this is byte array
   * If it is an Int array with bytes packed in it would be the Int
   */
  def getIndex(x: Int, y: Int): Int = {
    startIndex + y * stride + x * numBands;
  }

  /**
   * Might change to data
   */
  lazy val data: Array[T] = if (bufferInput != null) bufferInput else new Array[T](bufferLenght)

  def fill(value: T): Unit = {
    var i = 0
    while (i < bufferLenght) {
      data(i) = value
      i += 1
    }
  }

  def setChannel(x: Int, y: Int, ch: Int, value: T): Unit = {
    data(getIndex(x, y) + ch) = value
  }

  def setPixel(x: Int, y: Int, value: Array[T]): Unit = {
    val start = getIndex(x, y)
    var i = 0
    while (i < bufferLenght) {
      data(start + i) = value(i)
      i += 1
    }
  }

  /**
   * Default is that image is frozen if it is known
   */
  var frozen: Boolean = bufferInput != null

  def getChannel(x: Int, y: Int, ch: Int): T = {
    data(getIndex(x, y) + ch)
  }

  def getPixel(x: Int, y: Int): Array[T] = {
    val start = getIndex(x, y)
    val res = new Array[T](numBands)
    var i = 0
    do {
      res(i) = data(start + i)
      i += 1
    } while (i < numBands)
    res
  }
}