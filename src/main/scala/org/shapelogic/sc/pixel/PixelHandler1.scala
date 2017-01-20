package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.numeric.NumberPromotionMax

/**
 * The idea is that you are transforming to the same output type
 * But there is an inner type that you can do the calculation in
 *
 * This is almost the same as NumberPromotionMax[I]
 * But the names are different
 *
 * Maybe NumberPromotionMax[I] should not be a super class but a member
 * I do not want to write this every time that I make a new calc()
 *
 * The return type has to be the same as the input
 */
trait PixelHandler1[I] {
  type C
  def promoter: NumberPromotionMax.Aux[I, C]

  def data: Array[I]

  /**
   * numBands for input
   */
  def inputNumBands: Int

  /**
   * First output will get alpha if input does
   */
  def inputHasAlpha: Boolean

  def rgbOffsets: RGBOffsets

  /**
   * Take index that can extract a pixel
   * it can promote it to the Out type do the calculation
   * The return type has to be the same as the input
   */
  def calc(index: Int): I

}

object PixelHandler1{
  /**
   * Lemma pattern
   */
  type Aux[I, O] = PixelHandler1[I] { type C = O }
}