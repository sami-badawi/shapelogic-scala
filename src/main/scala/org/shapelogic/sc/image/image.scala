package org.shapelogic.sc

package object image {

  /**
   * Simple description of layout of colors band in array
   */
  case class RGBOffsets(red: Int, green: Int, blue: Int, alpha: Int, hasAlpha: Boolean)

  val grayRGBOffsets = RGBOffsets(red = 0, green = 1, blue = 2, alpha = 3, hasAlpha = false)
  val bgrRGBOffsets = RGBOffsets(red = 2, green = 1, blue = 0, alpha = 3, hasAlpha = false)
  val rgbRGBOffsets = RGBOffsets(red = 0, green = 1, blue = 2, alpha = 3, hasAlpha = false)

  val redBlueSwap = Array(2, 1, 0, 3)
}