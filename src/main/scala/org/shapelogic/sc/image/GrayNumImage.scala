package org.shapelogic.sc.image

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

class GrayNumImage[@specialized(Byte, Short, Int, Float, Double) N: ClassTag](width: Int, height: Int) extends ImageBase[N] {
  def frozen: Boolean = false

  def channels: Int = 1

  lazy val bufferLenght = height * width

  val buffer: Array[N] = new Array[N](bufferLenght)

  def getIndex(x: Int, y: Int): Int = {
    width * y + x
  }
  
  def getChannel(x: Int, y: Int, ch: Int): N = {
    buffer(width * y + x)
  }

  def getPixel(x: Int, y: Int): Array[N] = {
    Array[N](getChannel(x, y, ch = 0))
  }

  def setPixel(x: Int, y: Int, value: Array[N]): Unit = {
    if (!frozen)
      buffer(width * y + x) = value(0)
  }

  def setChannel(x: Int, y: Int, ch: Int, value: N): Unit = {
    if (!frozen)
      buffer(width * y + x) = value
  }

  def fill(value: N): Unit = {
    var i = 0
    while (i < bufferLenght) {
      buffer(i) = value
      i += 1
    }
  }
}
