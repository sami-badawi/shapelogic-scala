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

  lazy val xMin: Int = bufferImage.xMin
  lazy val yMin: Int = bufferImage.yMin
  lazy val xMax: Int = bufferImage.xMax
  lazy val yMax: Int = bufferImage.yMax

  var xCurrent: Int = xMin - 1
  var yCurrent: Int = yMin
  var index: Int = bufferImage.getIndex(xCurrent, yCurrent)

  lazy val bufferLenght = bufferImage.bufferLenght
  lazy val bufferLenghtM1 = bufferLenght - 1
  lazy val rgbOffsets: RGBOffsets = bufferImage.getRGBOffsetsDefaults

  def reset(): Unit = {
    xCurrent = -1
    yCurrent = 0
    index = bufferImage.getIndex(-1, 0)
  }

  def step(): Unit = {
    if (xCurrent < xMax)
      xCurrent += 1
    else {
      xCurrent = xMin
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