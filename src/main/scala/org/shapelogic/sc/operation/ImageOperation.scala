package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
 * This will always create another image of same dimensions
 * Not sure if it should always have the same number of bands
 * It it working by color band in parallel
 * It is hard to give a good signature that catches go up and go down
 * Maybe simpler to make a gray to color and color to gray version of this
 *
 * XXX For now I assume that the two images are identical size. This might change later
 * XXX Might not need @specialized
 */
class ImageOperation[T: ClassTag, A: ClassTag](
    bufferImage: BufferImage[T], conv: T => A) extends Serializable {

  val outputImage = BufferImage.makeBufferImage[A](bufferImage.width, bufferImage.height, bufferImage.numBands)
  lazy val numBands = bufferImage.numBands

  var xCurrent: Int = 0
  var yCurrent: Int = 0
  var index: Int = 0

  def step(): Unit = {
    if (xCurrent < bufferImage.width - 1)
      xCurrent += 1
    else {
      xCurrent = 0
      yCurrent += 1
    }
    index = bufferImage.getIndex(xCurrent, yCurrent)
  }

  def calcAndSet(): Unit = {
    var i = 0
    do {
      outputImage.data(index + i) = conv(bufferImage.data(index + i))
      i += 1
    } while (i < numBands)
  }

  def setCurrentXY(x: Int, y: Int): Int = {
    xCurrent = x
    yCurrent = y
    index = bufferImage.getIndex(x, y)
    index
  }
}

object ImageOperation {

  /**
   * This will make a deep copy of an image
   */
  def copy[T: ClassTag](bufferImage: BufferImage[T]): ImageOperation[T, T] = {
    new ImageOperation[T, T](bufferImage, Predef.identity)
  }

  val byteFloatConvert: Float = 1.0f / 255.0f
  def byte2Float[T: ClassTag](bufferImage: BufferImage[Byte]): ImageOperation[Byte, Float] = {
    new ImageOperation[Byte, Float](bufferImage, (byte: Byte) => byte * byteFloatConvert)
  }

  def constantValue[T: ClassTag](bufferImage: BufferImage[T], defalut: T): ImageOperation[T, T] = {
    new ImageOperation[T, T](bufferImage, _ => defalut)
  }
}