package org.shapelogic.sc.image

import org.scalatest._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

class GrayNumImageSpec extends FunSuite with BeforeAndAfterEach {
  test("Image get instantiated to 0") {
    val grayByteImage = new GrayNumImage[Byte](15, 10, null)
    assertResult(0) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }

  test("Image fill with 100") {
    val grayByteImage = new GrayNumImage[Byte](15, 10, null)
    grayByteImage.fill(100)
    assertResult(100) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }
}