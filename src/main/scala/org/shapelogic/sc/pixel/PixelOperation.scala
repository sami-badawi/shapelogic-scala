package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.image._

/**
 * This idea is that you can run over an image
 * If I assume that there is a color model I could do things faster
 * 
 * This only have an input image
 */
class PixelOperation[T: ClassTag](val bufferImage: BufferImage[T])
    extends Serializable with Iterator[Int] {

  var xCurrent: Int = -1
  var yCurrent: Int = 0
  var index: Int = bufferImage.getIndex(-1, 0)

  lazy val bufferLenght = bufferImage.bufferLenght
  lazy val bufferLenghtM1 = bufferLenght - 1
  lazy val rgbOffsets: RGBOffsets = bufferImage.getRGBOffsetsDefaults

  def reset(): Unit = {
    xCurrent = -1
    yCurrent = 0
    index = bufferImage.getIndex(-1, 0)
  }

  def step(): Unit = {
    if (xCurrent < bufferImage.width - 1)
      xCurrent += 1
    else {
      xCurrent = 0
      yCurrent += 1
    }
    index = bufferImage.getIndex(xCurrent, yCurrent)
  }

  def setCurrentXY(x: Int, y: Int): Int = {
    xCurrent = x
    yCurrent = y
    index = bufferImage.getIndex(x, y)
    index
  }

  def hasNext: Boolean = {
    index < bufferLenghtM1
  }

  def next(): Int = {
    step()
    index
  }
}