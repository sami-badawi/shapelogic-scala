package org.shapelogic.sc.old

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._
import org.shapelogic.sc.image.ReadImage
import org.shapelogic.sc.image.grayRGBOffsets
import scala.Int
import org.shapelogic.sc.image.RGBOffsets

/**
 * Wrapper around BufferedImage
 */
class GrayByteBufferedImage(bufferedImage: BufferedImage) extends ReadImage[Byte] {
  lazy val width: Int = bufferedImage.getWidth
  lazy val height: Int = bufferedImage.getHeight

  def frozen: Boolean = false

  def numBands: Int = 1

  def getIndex(x: Int, y: Int): Int = {
    width * y + x
  }

  def getChannel(x: Int, y: Int, ch: Int): Byte = {
    bufferedImage.getData.getSample(x, y, 0).toByte
  }

  def getPixel(x: Int, y: Int): Array[Byte] = {
    Array[Byte](getChannel(x, y, ch = 0))
  }

  def rgbOffsetsOpt: Option[RGBOffsets] = Some(grayRGBOffsets)

  def isInBounds(x: Int, y: Int): Boolean = {
    0 <= x && x < width && 0 <= y && y < height
  }
}
