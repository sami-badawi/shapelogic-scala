package org.shapelogic.sc.operation

import simulacrum._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.image.BufferImage

/**
 * From buffer, index and RGBOffsets get all the color information as un-boxed numbers
 */
case class IndexColorPixel[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag](rgbOffsets: RGBOffsets, buffer: Array[T]) {
  lazy val red = rgbOffsets.red
  lazy val green = rgbOffsets.green
  lazy val blue = rgbOffsets.blue
  lazy val alpha = rgbOffsets.alpha

  def getRed(index: Int): T = {
    buffer.apply(index + red)
  }

  def getGreen(index: Int): T = {
    buffer.apply(index + green)
  }

  def getBlue(index: Int): T = {
    buffer.apply(index + blue)
  }
}

object IndexColorPixel {

  def apply[T: ClassTag](bufferImage: BufferImage[T]): IndexColorPixel[T] = {
    new IndexColorPixel[T](bufferImage.getRGBOffsetsDefaults, bufferImage.data)
  }
}