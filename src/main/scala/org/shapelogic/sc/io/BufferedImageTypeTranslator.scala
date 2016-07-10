package org.shapelogic.sc.io

import java.awt.image.BufferedImage
import org.shapelogic.sc.image._

object BufferedImageTypeTranslator {

  def bufferedImageTypeToRGBOffsets(bufferedImageType: Int): Option[RGBOffsets] = {
    bufferedImageType match {
      case BufferedImage.TYPE_INT_RGB => Some(rgbRGBOffsets)
      case BufferedImage.TYPE_INT_BGR => Some(bgrRGBOffsets)
      case other => None
    }
  }
}