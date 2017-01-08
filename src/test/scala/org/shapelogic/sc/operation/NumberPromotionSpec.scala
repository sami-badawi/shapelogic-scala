package org.shapelogic.sc.operation

import org.scalatest._

class NumberPromotionSpec extends FunSuite with BeforeAndAfterEach {
  test("NumberPromotion.BytePromotion.promote(-1) == 255") {
    assertResult(255) { NumberPromotion.BytePromotion.promote(-1) }
  }

  test("NumberPromotion.ByteIdentityPromotion.promote(-1) == -1") {
    assertResult(-1) { NumberPromotion.ByteIdentityPromotion.promote(-1) }
  }
}