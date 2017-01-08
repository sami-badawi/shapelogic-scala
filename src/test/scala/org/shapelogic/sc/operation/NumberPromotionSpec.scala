package org.shapelogic.sc.operation

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

object NumberPromotionSpec {

  /**
   * Number with promoter inside
   */
  class ANumber[@specialized T: ClassTag: Numeric: Ordering](val value: T) {
    def printInfo() = {
      val typeOfInput = implicitly[ClassTag[T]]
      println(s"============= NumberIdPromotion typeOfInput: $typeOfInput")
    }
    lazy val promoterImplicits = new NumberPromotion.HighWithLowPriorityImplicits[T]()
    import promoterImplicits._
    lazy val promoter: NumberPromotion[T] = implicitly[NumberPromotion[T]]
    lazy val promoted = promoter.promote(value)
  }

  class AByte(byte: Byte) extends ANumber[Byte](byte) {
  }

  val minus1: AByte = new AByte(-1)

  val minus1ANumber = new ANumber[Byte](-1)
}

class NumberPromotionSpec extends FunSuite with BeforeAndAfterEach {
  import NumberPromotionSpec._

  test("NumberPromotion.BytePromotion.promote(-1) == 255") {
    assertResult(255) { NumberPromotion.BytePromotion.promote(-1) }
  }

  test("NumberPromotion.ByteIdentityPromotion.promote(-1) == -1") {
    assertResult(-1) { NumberPromotion.ByteIdentityPromotion.promote(-1) }
  }

  test("promotedMinus1 == 255") {
    implicit val shouldHavePriority = NumberPromotion.BytePromotion

    val promoter: NumberPromotion[Byte] = implicitly[NumberPromotion[Byte]]

    val promotedMinus1 = promoter.promote(minus1.value)
    assertResult(255) { promotedMinus1 }
  }

  test("minus1.promoted == 255") {
    minus1.promoted
    minus1.printInfo
    val expected = -1 //XXX should be 255
    assertResult(expected) { minus1.promoted }
  }

  test("minus1ANumber.promoted == 255") {
    minus1.promoted
    minus1.printInfo
    val expected = -1 //XXX should be 255
    assertResult(expected) { minus1ANumber.promoted }
  }
}

class NumberPromotionWithLowPriorityImplicitsByteSpec extends FunSuite with BeforeAndAfterEach with NumberPromotion.LowPriorityImplicitsByte {
  import NumberPromotionSpec._

  // Tried to put LowPriorityImplicitsByte in package object it was still ambigoues
  //  import operation._

  test("promotedMinus1 == -1") {
    val promoter: NumberPromotion[Byte] = implicitly[NumberPromotion[Byte]]
    val promotedMinus1 = promoter.promote(minus1.value)
    assertResult(-1) { promotedMinus1 }
  }

  // This is ambiguous
  //  test("promotedMinus1 == 255") {
  //    implicit val shouldHavePriority = NumberPromotion.BytePromotion
  //
  //    val promoter: NumberPromotion[Byte] = implicitly[NumberPromotion[Byte]]
  //
  //    val promotedMinus1 = promoter.promote(minus1.value)
  //    assertResult(255) { promotedMinus1 }
  //  }
}

class NumberPromotionHighWithLowPriorityImplicitsByteSpec extends FunSuite with BeforeAndAfterEach with NumberPromotion.HighWithLowPriorityImplicitsByte {
  import NumberPromotionSpec._

  // Tried to put LowPriorityImplicitsByte in package object it was still ambigoues
  //  import operation._

  test("promotedMinus1 == 255") {
    val promoter: NumberPromotion[Byte] = implicitly[NumberPromotion[Byte]]
    val promotedMinus1 = promoter.promote(minus1.value)
    assertResult(255) { promotedMinus1 }
  }
}

class NumberPromotionHighWithLowPriorityImplicitsGenericSpec extends FunSuite with BeforeAndAfterEach {
  import NumberPromotionSpec._

  //I cannot use this as a mix in since it is a class, it cannot be a trait since they cannot take context bounds
  //LowPriorityImplicits must be a trait otherwise it will not be specialized
  val promoterImplicits = new NumberPromotion.HighWithLowPriorityImplicits[Byte]()
  import promoterImplicits._

  // Tried to put LowPriorityImplicitsByte in package object it was still ambigoues
  //  import operation._

  test("promotedMinus1 == 255") {
    val promoter: NumberPromotion[Byte] = implicitly[NumberPromotion[Byte]]
    val promotedMinus1 = promoter.promote(minus1.value)
    assertResult(255) { promotedMinus1 }
  }
}

class NumberPromotionHighWithLowPriorityImplicitsGenericIntSpec extends FunSuite with BeforeAndAfterEach {
  import NumberPromotionSpec._

  //I cannot use this as a mix in since it is a class, it cannot be a trait since they cannot take context bounds
  val promoterImplicits = new NumberPromotion.HighWithLowPriorityImplicits[Int]()
  import promoterImplicits._

  test("promotedMinus1 == -1") {
    val promoter: NumberPromotion[Int] = implicitly[NumberPromotion[Int]]
    val promotedMinus1 = promoter.promote(minus1.value)
    assertResult(-1) { promotedMinus1 }
  }
}
