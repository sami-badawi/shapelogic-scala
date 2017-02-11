package org.shapelogic.sc.numeric

/**
 * Collection of NumberPromotion classes for primitve numbers
 */
object PrimitiveNumberPromotersAux {

  val verboseLogging = false

  val byteMask: Int = 0xff
  val shortMask: Int = 0xffff

  object BytePromotion extends NumberPromotionMax[Byte] {
    type Out = Int
    def promote(input: Byte): Int = {
      val res = input & byteMask
      res
    }
    val minValue: Int = 0
    val maxValue: Int = 255
    def demote(out: Int): Byte = {
      out.toByte
    }

    def parseCalc(text: String): Int = {
      text.trim().toInt
    }
  }

  object ShortPromotion extends NumberPromotionMax[Short] {
    type Out = Int
    def promote(input: Short): Int = {
      input & 0xffff
    }
    val minValue: Int = 0
    val maxValue: Int = 0xffff
    def demote(out: Int): Short = {
      out.toShort
    }

    def parseCalc(text: String): Int = {
      text.trim().toInt
    }
  }

  object IntPromotion extends NumberPromotionMax[Int] {
    type Out = Int
    def promote(input: Int): Int = {
      input
    }
    val minValue = 0
    val maxValue: Int = Int.MaxValue
    def demote(out: Int): Int = {
      out
    }
    def parseCalc(text: String): Int = {
      text.trim().toInt
    }
  }

  object FloatPromotion extends NumberPromotionMax[Float] {
    type Out = Float
    def promote(input: Float): Float = {
      input
    }
    val minValue: Float = 0
    val maxValue: Float = 1
    def demote(out: Float): Float = {
      out
    }
    def parseCalc(text: String): Float = {
      text.trim().toFloat
    }
  }

  object DoublePromotion extends NumberPromotionMax[Double] {
    type Out = Double
    def promote(input: Double): Double = {
      input
    }
    val minValue: Double = 0
    val maxValue: Double = 1
    def demote(out: Double): Double = {
      out
    }

    def parseCalc(text: String): Double = {
      text.trim().toDouble
    }
  }

  object AuxImplicit {
    implicit val bytePromotion: NumberPromotionMax.Aux[Byte, Int] = BytePromotion
    implicit val shortPromotion: NumberPromotionMax.Aux[Short, Int] = ShortPromotion
    implicit val intPromotion: NumberPromotionMax.Aux[Int, Int] = IntPromotion
    implicit val floatPromotion: NumberPromotionMax.Aux[Float, Float] = FloatPromotion
    implicit val doublePromotion: NumberPromotionMax.Aux[Double, Double] = DoublePromotion
  }
}