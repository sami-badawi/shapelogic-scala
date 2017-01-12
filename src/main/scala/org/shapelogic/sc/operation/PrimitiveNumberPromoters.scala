package org.shapelogic.sc.operation

/**
 *
 */
object PrimitiveNumberPromoters {

  val verboseLogging = false

  val byteMask: Int = 0xff
  val shortMask: Int = 0xffff

  object BytePromotion extends NumberPromotion[Byte] {
    println("Hello World, BytePromotion")
    type Out = Int
    def promote(input: Byte): Int = {
      val res = input & byteMask
      if (verboseLogging)
        println(s"Promote: $input to $res")
      res
    }
  }

  object ShortPromotion extends NumberPromotion[Short] {
    println("Hello World, ShortPromotion")
    type Out = Int
    def promote(input: Short): Int = {
      input & 0xffff
    }
  }

  object IntPromotion extends NumberPromotion[Int] {
    type Out = Int
    def promote(input: Int): Int = {
      input
    }
  }

  object FloatPromotion extends NumberPromotion[Float] {
    type Out = Float
    def promote(input: Float): Float = {
      input
    }
  }

  object DoublePromotion extends NumberPromotion[Double] {
    type Out = Double
    def promote(input: Double): Double = {
      input
    }
  }

  object NormalPrimitiveNumberPromotionImplicits {
    implicit val bytePromotionImplicit = BytePromotion
    implicit val shortPromotionImplicit = ShortPromotion
    // First used the direct definition
    implicit lazy val intPromotionImplicit = new NumberPromotion.NumberIdPromotion[Int]
    implicit lazy val floatPromotionImplicit = new NumberPromotion.NumberIdPromotion[Float]
    implicit lazy val doublePromotionImplicit = new NumberPromotion.NumberIdPromotion[Double]
  }

}