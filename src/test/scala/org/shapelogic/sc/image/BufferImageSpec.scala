package org.shapelogic.sc.image

import org.scalatest._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

class BufferImageSpecSpec extends FunSuite with BeforeAndAfterEach {
  test("Image get instantiated to 0") {
    val grayByteImage = new BufferImage[Byte](15, 10, 1, null)
    assertResult(0) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }

  test("Image fill with 100") {
    val grayByteImage = new BufferImage[Byte](10, 10, 1, null)
    grayByteImage.data(0)
    println("grayByteImage.data.getClass.getSimpleName " + grayByteImage.data.getClass.getSimpleName)
    grayByteImage.fill(100)
    assertResult(100) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }
}