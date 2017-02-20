package org.shapelogic.sc.image

import spire.math.Numeric
import spire.math.Integral
import spire.implicits._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.polygon.BoxLike
import org.shapelogic.sc.polygon.Box

/**
 * Work horse buffer image
 * This will take care of most cases
 *
 * @param bufferInput this should not be called with input null
 *
 */
final class BufferImage[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag](
    val width: Int,
    val height: Int,
    val numBands: Int,
    bufferInput: Array[T],
    val rgbOffsetsOpt: Option[RGBOffsets] = None,
    boxOpt: Option[BoxLike] = None) extends WriteImage[T] with BufferImageTrait[T] with BoxLike {

  /**
   * Number of positions between pixel in new row
   */
  lazy val stride: Int = width * numBands

  lazy val bufferLenght = height * stride
  lazy val pixelCount = height * width

  lazy val xMin: Int = boxOpt.map(_.xMin).getOrElse(0)
  lazy val yMin: Int = boxOpt.map(_.yMin).getOrElse(0)
  lazy val xMax: Int = boxOpt.map(_.xMax).getOrElse(width - 1)
  lazy val yMax: Int = boxOpt.map(_.yMax).getOrElse(height - 1)

  lazy val box: Box = Box(xMin, yMin, xMax, yMax)

  /**
   * Get the first channel if this is byte array
   * If it is an Int array with bytes packed in it would be the Int
   */
  def getIndex(x: Int, y: Int): Int = {
    y * stride + x * numBands
  }

  /**
   * This cannot be lazy or it will be recreated every time it is used
   */
  val data: Array[T] = bufferInput

  def fill(value: T): Unit = {
    cfor(0)(_ < bufferLenght, _ + 1) { i =>
      data.update(i, value)
    }
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
   * Default is that image is not frozen
   * Set to true if it is known say after a load
   */
  private var frozenP: Boolean = false

  /**
   * You can work on an image when you are done you can freeze it and it can be
   * used as an immutable image after that.
   */
  def freeze(): Unit = {
    frozenP = true
  }

  /**
   * If an image is frozed it should be safe to consider it immutable
   * and take sub images from it.
   *
   * Currently this is not enforced
   */
  def frozen: Boolean = frozenP

  def getChannel(x: Int, y: Int, ch: Int): T = {
    data(getIndex(x, y) + ch)
  }

  def getPixel(x: Int, y: Int): Array[T] = {
    val start = getIndex(x, y)
    val res = new Array[T](numBands)
    cfor(0)(_ < numBands, _ + 1) { i =>
      res(i) = data(start + i)
    }
    res
  }

  def isInBounds(x: Int, y: Int): Boolean = {
    xMin <= x && x <= xMax && yMin <= y && y <= yMax
  }

  /**
   * Creates an empty image with the same properties
   */
  def empty(): BufferImage[T] = {
    val buffer = new Array[T](width * height * numBands)
    new BufferImage[T](width = width,
      height = height,
      numBands = numBands,
      bufferInput = buffer,
      rgbOffsetsOpt = rgbOffsetsOpt,
      boxOpt = boxOpt)
  }

  def getRGBOffsetsDefaults: RGBOffsets = {
    BufferImage.getRGBOffsets(rgbOffsetsOpt, numBands)
  }

  /**
   * This is the jump that you need to make in index to get to the
   *
   * Goes in normal growing radian direction
   * but since y is down this is clockwise
   */
  lazy val cyclePoints: Array[Int] = Array(
    numBands, // 0
    numBands + stride, // 45 down
    stride, // 90 down
    -numBands + stride, //135 down
    -numBands, // 180
    -numBands - stride, // 225 up
    -stride, // 270 up
    numBands - stride // 315 up
    )

  lazy val hasAlpha: Boolean = getRGBOffsetsDefaults.hasAlpha
  lazy val numBandsNoAlpha: Int = if (hasAlpha) numBands - 1 else numBands
  lazy val alphaChannel = if (hasAlpha) getRGBOffsetsDefaults.alpha else -1
}

object BufferImage {

  /**
   * create an image
   */
  def apply[T: ClassTag](
    width: Int,
    height: Int,
    numBands: Int,
    bufferInput: Array[T] = null,
    rgbOffsetsOpt: Option[RGBOffsets] = None,
    freeze: Boolean = true): BufferImage[T] = {
    val imageArray = if (bufferInput != null)
      bufferInput
    else
      new Array[T](width * height * numBands)
    val image = new BufferImage(width, height, numBands, imageArray, rgbOffsetsOpt)
    if (freeze)
      image.freeze()
    image
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