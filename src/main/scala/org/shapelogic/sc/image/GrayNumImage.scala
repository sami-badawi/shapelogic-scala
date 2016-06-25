package org.shapelogic.sc.image

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

class GrayNumImage[@specialized(Byte, Int, Float, Double) N: ClassTag: Ring](width: Int, height: Int) extends ImageBase[N] {
  def frozen: Boolean = false

  lazy val bufferLenght = height * width

  val buffer: Array[N] = new Array[N](bufferLenght)

  def getChannel(x: Int, y: Int, ch: Int): N = {
    buffer(width * y + x)
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
