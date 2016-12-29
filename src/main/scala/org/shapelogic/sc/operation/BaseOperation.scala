package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
 * This idea is that you can run over an image
 */
class BaseOperation[@specialized T: ClassTag, @specialized A: ClassTag](val bufferImage: BufferImage[T])(implicit conv: T => A) extends Serializable {

  var xCurrent: Int = 0
  var yCurrent: Int = 0
  var index: Int = 0

  var reciever: A = _
  var accumulator: A = _

  def step(): Unit = {
    if (xCurrent < bufferImage.width - 1)
      xCurrent += 1
    else {
      xCurrent = 0
      yCurrent += 1
    }
    index = bufferImage.getIndex(xCurrent, yCurrent)
  }

  def retriev(): A = {
    conv(bufferImage.data(index))
  }

  def setCurrentXY(x: Int, y: Int): Int = {
    xCurrent = x
    yCurrent = y
    index = bufferImage.getIndex(x, y)
    index
  }
}