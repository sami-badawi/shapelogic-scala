package org.shapelogic.sc.operation

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

object NumberPromotionSpec extends NumberPromotion.HighPriorityImplicits[Byte] {
  import NumberPromotion.BytePromotion

  class ANumber[@specialized I: ClassTag: Numeric: Ordering](val value: I) {
  }

  class AByte(byte: Byte) extends ANumber[Byte](byte) {
  }

  val minus1: AByte = new AByte(-1)

  lazy val promoter: NumberPromotion[Byte] = implicitly[NumberPromotion[Byte]]

  val promotedMinus1 = promoter.promote(minus1.value)

}

class NumberPromotionSpec extends FunSuite with BeforeAndAfterEach {
  import NumberPromotionSpec._

  test("NumberPromotion.BytePromotion.promote(-1) == 255") {
    assertResult(255) { NumberPromotion.BytePromotion.promote(-1) }
  }

  test("NumberPromotion.ByteIdentityPromotion.promote(-1) == -1") {
    assertResult(-1) { NumberPromotion.ByteIdentityPromotion.promote(-1) }
  }

  test("promotedMinus1 == -1") {
    assertResult(-1) { promotedMinus1 }
  }
}