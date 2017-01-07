package org.shapelogic.sc.operation

import org.scalatest._

class NumberPromotionSpec extends FunSuite with BeforeAndAfterEach {
  test("Image get instantiated to 0") {
    assertResult(255) { NumberPromotion.BytePromotion.promote(-1) }
  }

}