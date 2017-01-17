package org.shapelogic.sc.util

import simulacrum._

import java.awt.image._
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.image.ImageShape

@typeclass trait ImageInfo[T] {
  def info(img: T, filename: String): String
}

/**
 *
 */
object ImageInfo {

  implicit lazy val bufferedImageImageInfo = new ImageInfo[BufferedImage] {
    def info(img: BufferedImage, filename: String = ""): String = {
      val colorModel = img.getColorModel
      val width = img.getWidth
      val height = img.getHeight
      val res = s"BufferedImage info: width: $width height: $height colorModel: ${colorModel}"
      if (filename != null && !filename.isEmpty())
        s"$res, filename: $filename"
      else
        res
    }
  }

  implicit lazy val bufferImageImageInfo = new ImageInfo[ImageShape] {
    def info(img: ImageShape, filename: String = ""): String = {
      try {
        val colorModel = img.rgbOffsetsOpt
        val width = img.width
        val height = img.height
        val res = s"BufferedImage info: width: ${width} height: ${height} colorModel: ${colorModel}"
        if (filename != null && !filename.isEmpty())
          s"$res, filename: $filename"
        else
          res
      }
      catch {
        case ex: Throwable => {
          println(ex.getMessage)
//          ex.
          "Error: " + ex.getMessage
        }
      }
    }
  }
}