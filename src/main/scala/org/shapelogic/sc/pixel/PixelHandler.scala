package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets

/**
 * The idea is to have the logic in objects of this type
 * The the full operations can just be a runner
 */
trait PixelHandler[T] {
  type Out
  
  def data: Array[T]
  
  def rgbOffsets: RGBOffsets
  
  def calc(index: Int): Out
}