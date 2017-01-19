package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets

trait PixelHandler1[I] {
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

  def i2Out(c: I): Out
  def out2i(c: Out): I
  
}