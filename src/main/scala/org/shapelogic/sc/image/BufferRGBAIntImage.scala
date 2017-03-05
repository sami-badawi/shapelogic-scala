package org.shapelogic.sc.image

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import java.nio.ByteBuffer
import org.shapelogic.sc.util.ColorHelper

/**
 * Default Java BufferedImage format 3 or 4 bytes packed in an Int
 * This is an equivalent representation that can be used for transport in and out
 */
class BufferRGBAIntImage(
    val width: Int,
    val height: Int,
    val numBands: Int,
    bufferInput: Array[Int] = null,
    val rgbOffsetsOpt: Option[RGBOffsets] = None) extends WriteImage[Byte] with BufferImageTrait[Int] {

  /**
   * Number of positions between pixel in new row
   */
  lazy val stride: Int = width

  lazy val bufferLenght = height * stride

  /**
   * Get the first channel if this is byte array
   * If it is an Int array with bytes packed in it would be the Int
   */
  def getIndex(x: Int, y: Int): Int = {
    y * stride + x
  }

  /**
   * Might change to data
   */
  lazy val data: Array[Int] = if (bufferInput != null) bufferInput else new Array[Int](bufferLenght)

  def fill(value: Byte): Unit = {
    var i = 0
    val intValue = ColorHelper.byte2RbgaInt(value)
    while (i < bufferLenght) {
      data(i) = intValue
      i += 1
    }
  }

  def setChannel(x: Int, y: Int, ch: Int, value: Byte): Unit = {
    data(getIndex(x, y) + ch) = value
  }

  def setPixel(x: Int, y: Int, value: Array[Byte]): Unit = {
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

  /**
   * XXX Fix
   */
  def extractFromInt(pixel: Int, ch: Int): Byte = {
    val res = if (ch == 0)
      pixel & 0xFF
    else if (ch == 1)
      pixel >> 8 & 0xFF
    else if (ch == 2)
      pixel >> 16 & 0xFF
    else
      pixel >> 24 & 0xFF
    res.toByte
  }

  /**
   * XXX Fix
   */
  def extractByteArrayFromInt(pixel: Int): Array[Byte] = {
    ByteBuffer.allocate(4).putInt(pixel).array()
  }

  def getChannel(x: Int, y: Int, ch: Int): Byte = {
    val packed = data(getIndex(x, y))
    extractFromInt(packed, ch)
  }

  def getPixel(x: Int, y: Int): Array[Byte] = {
    val packed = data(getIndex(x, y))
    extractByteArrayFromInt(packed)
  }

  def isInBounds(x: Int, y: Int): Boolean = {
    0 <= x && x < width && 0 <= y && y < height
  }
}