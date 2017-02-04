package org.shapelogic.sc.image

import simulacrum._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import java.nio.ByteBuffer
import org.shapelogic.sc.util.ColorHelper

/**
 * Default Java BufferedImage format 3 or 4 bytes packed in an Int
 * This is an equivalent representation that can be used for transport in and out
 */
class BufferBooleanImage(
  val width: Int,
  val height: Int,
  val numBands: Int,
  bufferInput: Array[Int] = null)
    extends WriteImage[Boolean] with BufferImageTrait[Int] {

  val bitsInStorage = 32

  val rgbOffsetsOpt: Option[RGBOffsets] = None

  lazy val intArrayFullUse = (width * height) / 32
  lazy val intArrayModolus = (width * height) % 32
  lazy val intArrayLenght = intArrayFullUse + (if (intArrayModolus == 0) 1 else 1)

  /**
   * Number of positions between pixel in new row
   */
  lazy val stride: Int = width

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
    startIndex + y * stride + x;
  }

  /**
   * Might change to data
   */
  lazy val data: Array[Int] = if (bufferInput != null) bufferInput else new Array[Int](intArrayLenght)

  def fill(value: Boolean): Unit = {
    var i = 0
    val intValue = if (value) -1 else 0
    while (i < intArrayLenght) {
      data(i) = intValue
      i += 1
    }
  }

  def setChannel(x: Int, y: Int, ch: Int, value: Boolean): Unit = {
    val index = getIndex(x, y)
    ColorHelper.setBitInIntArray(data, index, value)
  }

  def setPixel(x: Int, y: Int, value: Array[Boolean]): Unit = {
    val start = getIndex(x, y)
    var i = 0
    while (i < value.length) {
      ColorHelper.setBitInIntArray(data, start + i, value(i))
      i += 1
    }
  }

  /**
   * Default is that image is frozen if it is known
   */
  var frozen: Boolean = bufferInput != null

  def getChannel(x: Int, y: Int, ch: Int): Boolean = {
    val start = getIndex(x, y)
    ColorHelper.getBitInIntArray(data, start + ch)
  }

  def getPixel(x: Int, y: Int): Array[Boolean] = {
    val output = new Array[Boolean](numBands)
    val start: Int = getIndex(x, y)
    var i: Int = 0
    while (i < numBands) {
      output(i) = ColorHelper.getBitInIntArray(data, start + i)
      i += 1
    }
    output
  }

  def isInBounds(x: Int, y: Int): Boolean = {
    0 <= x && x < width && 0 <= y && y < height
  }
}