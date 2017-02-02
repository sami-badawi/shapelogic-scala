package org.shapelogic.sc.image

import org.scalatest._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

class BufferBooleanImageSpec extends FunSuite with BeforeAndAfterEach {

  test("Image get instantiated to 0") {
    val grayByteImage = new BufferBooleanImage(15, 10, 1, null)
    assertResult(false) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }

  test("Image set pixel to true") {
    val grayByteImage = new BufferBooleanImage(10, 10, 1, null)
    grayByteImage.data(0)
    println("grayByteImage.data.getClass.getSimpleName " + grayByteImage.data.getClass.getSimpleName)
    grayByteImage.setChannel(x = 0, y = 0, ch = 0, true)
    assertResult(true) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
    assertResult(false) { grayByteImage.getChannel(x = 1, y = 0, ch = 0) }
  }

  test("Image fill with true") {
    val grayByteImage = new BufferBooleanImage(10, 10, 1, null)
    grayByteImage.data(0)
    println("grayByteImage.data.getClass.getSimpleName " + grayByteImage.data.getClass.getSimpleName)
    grayByteImage.fill(true)
    assertResult(true) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
    assertResult(true) { grayByteImage.getChannel(x = 1, y = 0, ch = 0) }
  }
}