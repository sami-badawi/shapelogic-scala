package org.shapelogic.sc.io

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._
import scala.util.Try
import java.awt.image.Raster
import java.awt.image.DataBufferByte
import org.shapelogic.sc.image.ReadImage
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.image._

/**
 * BufferImage is the workhorse image type
 * Factory for this
 */
object LoadBufferImage {

  val coveredBufferedImageTypeSet: Set[Int] = Set(
    BufferedImage.TYPE_3BYTE_BGR,
    BufferedImage.TYPE_BYTE_GRAY)

  def bufferedImageToBufferImage(bufferedImage: BufferedImage): Option[BufferImage[Byte]] = {
    val rgbType = bufferedImage.getType
    val colorModel = bufferedImage.getColorModel
    println(s"colorModel: $colorModel")
    if (coveredBufferedImageTypeSet.contains(rgbType))
      Try({
        val raster = bufferedImage.getData
        val byteBuffer: Array[Byte] = LoadImage.rasterToByteArray(raster)
        val res: BufferImage[Byte] =
          if (rgbType == BufferedImage.TYPE_3BYTE_BGR) {
            val byteArray = raster.getDataBuffer.asInstanceOf[DataBufferByte].getData
            new BufferImage(
              width = bufferedImage.getWidth,
              height = bufferedImage.getHeight,
              numBands = 3,
              bufferInput = byteArray,
              rgbOffsetsOpt = Some(rgbRGBOffsets))
          }
          else if (rgbType == BufferedImage.TYPE_BYTE_GRAY)
            new BufferImage(
              width = bufferedImage.getWidth,
              height = bufferedImage.getHeight,
              numBands = 3,
              bufferInput = byteBuffer,
              rgbOffsetsOpt = Some(grayRGBOffsets))
          else null
        res
      }).toOption
    else
      None
  }

  def main(args: Array[String]): Unit = {
    println(s"args: ${args.toSeq}")
    val filename = if (0 < args.size) args(0) else "../image3bgr.jpg"
    val imageOpt = LoadImage.loadFile(filename).toOption
    imageOpt match {
      case Some(image) => {
        val wrappedOpt = bufferedImageToBufferImage(image)
        wrappedOpt match {
          case Some(wrapped) => {
            val pointRGB = wrapped.getPixel(10, 10).toSeq
            println(s"Image loaded. RGB: $pointRGB")
          }
          case None => println("Very strange")
        }
      }
      case None => println("Image could not be loaded")
    }
  }
}