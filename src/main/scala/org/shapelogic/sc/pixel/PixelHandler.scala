package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.numeric.NumberPromotion
import org.shapelogic.sc.numeric.NumberPromotion
import scala.reflect.ClassTag

/**
 * The idea is to have the logic in objects of this type
 * The the full operations can just be a runner
 *
 * Started with a version that had:
 * abstract type C inside
 * but that did not type check
 * The types would not unify
 */
trait PixelHandler[I, C] {
  //  type C //XXX did not type check

  def promoter: NumberPromotion.Aux[I, C]

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
   * indexIn: index of input buffer.
   * It is assumed that this is falling on a 0 channel and that it is a legal position
   * channelOut: channel number for output. This make is more flexible it can handle:
   * Channel to channel
   * Output channel taking input from more channels
   */
  def calc(indexIn: Int, channelOut: Int): I
}

object PixelHandler {

  /**
   * If I move the type C inside this would be useful again.
   * Now it is just an abstraction
   */
  type Aux[I, C] = PixelHandler[I, C] {
    //    type C = J //XXX did not type check
  }

  /**
   * Same input and Calc type
   * Will typically have a
   */
  type Same[I] = PixelHandler[I, I] {
    //    type C = I
    def promoter: NumberPromotion.Aux[I, I]
  }
}