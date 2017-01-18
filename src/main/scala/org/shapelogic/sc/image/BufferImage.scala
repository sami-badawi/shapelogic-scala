package org.shapelogic.sc.image

import simulacrum._

import spire.math.Numeric
import spire.math.Integral
import spire.implicits._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
 * Work horse buffer image
 * This will take care of most cases
 */
sealed class BufferImage[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag](
    val width: Int,
    val height: Int,
    val numBands: Int,
    bufferInput: Array[T] = null,
    val rgbOffsetsOpt: Option[RGBOffsets] = None) extends WriteImage[T] with BufferImageTrait[T] {

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

  var bufferCreated: Boolean = false
  var makeCount = 0
  def makeBuffer(): Array[T] = {
    makeCount += 1
    if (bufferCreated) {
      println(s"makeBuffer() should only be called once")
      data
    } else if (bufferInput != null)
      bufferInput
    else {
      println(s"create new array of bufferLenght: $bufferLenght. makeCount: $makeCount")
      bufferCreated = true
      new Array[T](bufferLenght)
    }
  }

  /**
   * This cannot be lazy or it will be recreated every time it is used
   */
  val data: Array[T] = makeBuffer()

  def fill(value: T): Unit = {
    var i = 0
    while (i < bufferLenght) {
      data.update(i, value)
      i += 1
    }
    println(s"filled i: $i, data(0): ${data(0)}, value: $value")
    data(0) = value
    println(s"filled i: $i, data(0): ${data(0)}, value: $value")
  }

  def setChannel(x: Int, y: Int, ch: Int, value: T): Unit = {
    data(getIndex(x, y) + ch) = value
  }

  def setPixel(x: Int, y: Int, value: Array[T]): Unit = {
    val start = getIndex(x, y)
    var i = 0
    while (i < numBands) {
      data(start + i) = value(i)
      i += 1
    }
  }

  /**
   * Default is that image is frozen if it is known
   */
  private var frozenP: Boolean = bufferInput != null

  /**
   * You can work on an image when you are done you can freeze it and it can be
   * used as an immutable image after that.
   */
  def freeze(): Unit = {
    frozenP = true
  }

  def frozen: Boolean = frozenP

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

  def isInBounds(x: Int, y: Int): Boolean = {
    0 <= x && x < width && 0 <= y && y < height
  }

  /**
   * Creates an empty image with the same properties
   */
  def empty(): BufferImage[T] = {
    val buffer = new Array[T](width * height * numBands)
    println(s"buffer.lenght: ${buffer.size}")
    new BufferImage[T](width = width,
      height = height,
      numBands = numBands,
      bufferInput = buffer,
      rgbOffsetsOpt = rgbOffsetsOpt)
  }

  def getRGBOffsetsDefaults: RGBOffsets = {
    BufferImage.getRGBOffsets(rgbOffsetsOpt, numBands)
  }
}

object BufferImage {
  def makeBufferImage[T: ClassTag](width: Int,
    height: Int,
    numBands: Int,
    rgbOffsetsOpt: Option[RGBOffsets] = None): BufferImage[T] = {
    new BufferImage(width, height, numBands, null, rgbOffsetsOpt)
  }

  def getRGBOffsets(rgbOffsetsOpt: Option[RGBOffsets], numBands: Int): RGBOffsets = {
    rgbOffsetsOpt match {
      case Some(rgbOff) => rgbOff
      case None => {
        numBands match {
          case 1 => grayRGBOffsets
          case 2 => grayAlphaRGBOffsets
          case 3 => bgrRGBOffsets
          case 4 => abgrRGBOffsets
          case _ => abgrRGBOffsets // Should maybe throw exception
        }
      }
    }
  }
}