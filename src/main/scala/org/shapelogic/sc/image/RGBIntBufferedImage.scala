package org.shapelogic.sc.image

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._

/**
 * Wrapper
 */
class RGBIntBufferedImage(bufferedImage: BufferedImage) extends ReadImage[Byte] {
  lazy val width: Int = bufferedImage.getWidth
  lazy val height: Int = bufferedImage.getHeight

  def frozen: Boolean = false

  lazy val numBands: Int = bufferedImage.getColorModel.getNumComponents

  def getIndex(x: Int, y: Int): Int = {
    width * y + x
  }

  def getChannel(x: Int, y: Int, ch: Int): Byte = {
    bufferedImage.getData.getSample(x, y, ch).toByte
  }

  def getPixel(x: Int, y: Int): Array[Byte] = {
    val resByte = new Array[Byte](3)
    val resInt: Array[Int] = new Array[Int](3)
    val res2: Array[Int] = bufferedImage.getData.getPixel(x, y, resInt)
    var i = 0
    do {
      resByte(i) = resInt(i).toByte
      i += 1
    } while (i < numBands)
    resByte
  }

  def rgbOffsetsOpt: Option[RGBOffsets] = Some(rgbRGBOffsets)
}
