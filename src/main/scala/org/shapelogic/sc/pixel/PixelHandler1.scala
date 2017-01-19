package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.numeric.NumberPromotionMax

/**
 * The idea is that you are transforming to the same output type
 * But there is an inner type that you can do the calculation in
 * 
 * This is almost the same as NumberPromotionMax[I]
 * But the names are different
 */
trait PixelHandler1[I] extends NumberPromotionMax[I] {

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

  def trans(index: Int): Out

}