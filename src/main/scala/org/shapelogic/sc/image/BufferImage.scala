package org.shapelogic.sc.image

import simulacrum._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

class BufferImage[@specialized T: ClassTag](
    val width: Int,
    val height: Int,
    val numBands: Int,
    bufferInput: Array[T] = null) extends ImageBase[T] with BufferImageTrait[T] {

  /*
   * 
   */
  lazy val stride: Int = width * numBands

  /**
   * If there is a subimage
   */
  val startIndex: Int = 0

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

  def fill(value: T): Unit = ???
  def setChannel(x: Int, y: Int, ch: Int, value: T): Unit = ???
  def setPixel(x: Int, y: Int, value: Array[T]): Unit = ???

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