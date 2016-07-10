package org.shapelogic.sc.io

import java.awt.image.BufferedImage
import org.shapelogic.sc.image.RGBOffsets

object BufferedImageTypeTranslator {

  val rgbRGBOffsets = RGBOffsets(red = 0, green = 1, blue = 2, alpha = 0, hasAlpha = false)
  val bgrRGBOffsets = RGBOffsets(red = 2, green = 1, blue = 0, alpha = 0, hasAlpha = false)

  def bufferedImageTypeToRGBOffsets(bufferedImageType: Int): Option[RGBOffsets] = {
    bufferedImageType match {
      case BufferedImage.TYPE_INT_RGB => Some(rgbRGBOffsets)
      case BufferedImage.TYPE_INT_BGR => Some(bgrRGBOffsets)
      case other => None
    }
  }
}