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

  def trans(index: Int): Out

  def i2Calc(input: I): Calc
  def calc2I(c: Calc): I

  def calc2Out(c: Calc): Out
  def out2Calct(c: Out): Calc

}