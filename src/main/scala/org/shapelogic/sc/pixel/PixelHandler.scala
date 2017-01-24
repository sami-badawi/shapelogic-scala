package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.numeric.NumberPromotionMax

/**
 * The idea is to have the logic in objects of this type
 * The the full operations can just be a runner
 */
trait PixelHandler[I] {
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
   * indexIn: index of input buffer
   * channelOut: channel number for output
   */
  def calc(indexIn: Int, channelOut: Int): I
}

object PixelHandler {
  type Aux[I, J] = PixelHandler[I] {
    type C = J
  }
}