package org.shapelogic.sc.numeric

import spire.math.Numeric
import spire.implicits._
import scala.specialized
import scala.reflect.ClassTag

/**
 * You frequently have to change from
 */
trait NumberPromotion[I] {
  /*
   * Do not know this type in advance
   */
  type Out

  /**
   * Promote to a place that is better for computation
   * One use is to fix signed Byte to unsigned Byte
   */
  def promote(input: I): Out

  def demote(out: Out): I

  def parseCalc(text: String): Out
}

trait HasNumberPromotion[I] {
  def promotor: NumberPromotion[I]
}

object NumberPromotion {
  import PrimitiveNumberPromoters._

  /**
   * Lemma pattern
   */
  type Aux[I, O] = NumberPromotion[I] { type Out = O }

  type Same[I] = NumberPromotion[I] { type Out = I }
}
