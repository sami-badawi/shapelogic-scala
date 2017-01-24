package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets

/**
 * The idea is to have the logic in objects of this type
 * The the full operations can just be a runner
 */
trait PixelHandler[I] {
  type Calc
  type Out

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
  def calc(indexIn: Int, channelOut: Int): Out

  def i2Calc(input: I): Calc
  def calc2I(c: Calc): I

  def calc2Out(c: Calc): Out
  def out2Calc(c: Out): Calc

}

object PixelHandler {
  type Aux[I, C, O] = PixelHandler[I] {
    type Calc = C
    type Res = O
  }
}