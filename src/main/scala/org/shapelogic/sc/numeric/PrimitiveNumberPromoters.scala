package org.shapelogic.sc.numeric

/**
 * Collection of NumberPromotion classes for primitve numbers
 */
object PrimitiveNumberPromoters {

  val verboseLogging = false

  val byteMask: Int = 0xff
  val shortMask: Int = 0xffff

  object BytePromotion extends NumberPromotionMax[Byte] {
    println("Hello World, BytePromotion")
    type Out = Int
    def promote(input: Byte): Int = {
      val res = input & byteMask
      if (verboseLogging)
        println(s"Promote: $input to $res")
      res
    }
    val maxValue: Int = 255
  }

  object ShortPromotion extends NumberPromotionMax[Short] {
    println("Hello World, ShortPromotion")
    type Out = Int
    def promote(input: Short): Int = {
      input & 0xffff
    }
    val maxValue: Int = 0xffff
  }

  object IntPromotion extends NumberPromotionMax[Int] {
    type Out = Int
    def promote(input: Int): Int = {
      input
    }
    val maxValue: Int = Int.MaxValue
  }

  object FloatPromotion extends NumberPromotionMax[Float] {
    type Out = Float
    def promote(input: Float): Float = {
      input
    }
    val maxValue: Float = 1
  }

  object DoublePromotion extends NumberPromotionMax[Double] {
    type Out = Double
    def promote(input: Double): Double = {
      input
    }
    val maxValue: Double = 1
  }

  /**
   * All packaged up together
   */
  object NormalPrimitiveNumberPromotionImplicits {
    implicit val bytePromotionImplicit = BytePromotion
    implicit val shortPromotionImplicit = ShortPromotion
    // First used the direct definition
    implicit lazy val intPromotionImplicit = IntPromotion
    implicit lazy val floatPromotionImplicit = FloatPromotion
    implicit lazy val doublePromotionImplicit = DoublePromotion
  }

}