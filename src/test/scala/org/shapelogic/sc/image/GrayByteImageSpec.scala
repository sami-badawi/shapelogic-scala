package org.shapelogic.sc.image

import org.scalatest._
import org.shapelogic.sc.old.GrayByteImage

class GrayByteImageSpec extends FunSuite with BeforeAndAfterEach {
  test("Image get instantiated to 0") {
    val grayByteImage = new GrayByteImage(15, 10, null)
    assertResult(0) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }

  test("Image fill with 100") {
    val grayByteImage = new GrayByteImage(15, 10, null)
    grayByteImage.fill(100)
    assertResult(100) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }
}