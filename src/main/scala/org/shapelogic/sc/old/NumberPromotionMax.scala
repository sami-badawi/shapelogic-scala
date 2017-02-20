package org.shapelogic.sc.numeric
/**
 * In order to make more generic image processing operations
 * more things need to be known
 * They are not always very uniform
 * You could imagine having one type of Float images that go between 0.0 and 1.0
 * and another one that goes from 0.0 to 255.0 so normal Byte range with bigger precision
 * or one that take up the whole float range.
 *
 * This is one reason that number promotion is handy
 */
@deprecated("""There were barely any difference between this and the base class.
It was too much work keeping this around.
Leave if there is a substantial difference.
""",
  "2017-02-20")
trait NumberPromotionMax[I] extends NumberPromotion[I] {
  //  def minValue: Out
  //  def maxValue: Out

  //  def demote(out: Out): I

  // Adding this will take it from a pure interface to a hybrid class interface
  // Not sure if it is worth it, might move it out
  //  lazy val minValueBuffer: I = demote(minValue)
  //  lazy val maxValueBuffer: I = demote(maxValue)
}

object NumberPromotionMax {
  import PrimitiveNumberPromoters._

  /**
   * Lemma pattern
   */
  type Aux[I, O] = NumberPromotionMax[I] { type Out = O }

  type Same[I] = NumberPromotionMax[I] { type Out = I }
}