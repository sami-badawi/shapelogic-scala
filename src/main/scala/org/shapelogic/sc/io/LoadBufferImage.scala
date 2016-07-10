package org.shapelogic.sc.io

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._
import scala.util.Try
import org.shapelogic.sc.image.RGBNumImage
import org.shapelogic.sc.image.RGBIntBufferedImage
import java.awt.image.Raster
import java.awt.image.DataBufferByte
import org.shapelogic.sc.image.ReadImage

/**
 * BufferImage is the workhorse image type
 * Factory for this
 */
object LoadBufferImage {

  def bufferedImageToRGBNumImage(bufferedImage: BufferedImage): Option[RGBNumImage[Byte]] = {
    val rgbType = bufferedImage.getType
    val colorModel = bufferedImage.getColorModel
    println(s"colorModel: $colorModel")
    if (rgbType == BufferedImage.TYPE_3BYTE_BGR)
      Try({
        val raster = bufferedImage.getData
        val byteBuffer: Array[Byte] = LoadImage.rasterToByteArray(raster)
        val res: RGBNumImage[Byte] = new RGBNumImage(width = bufferedImage.getWidth, height = bufferedImage.getHeight, bufferIn = byteBuffer)
        res
      }).toOption
    else
      None
  }

  def bufferedImageToRGBIntImage(bufferedImage: BufferedImage): Option[ReadImage[Byte]] = {
    val colorModel = bufferedImage.getColorModel
    val rgbType = bufferedImage.getType
    println(s"colorModel: $colorModel, \nrgbType: $rgbType")
    println(s"Expected: ${BufferedImage.TYPE_INT_RGB}")
    try {
      if (rgbType == BufferedImage.TYPE_INT_RGB) {
        val res = new RGBIntBufferedImage(bufferedImage)
        Some(res)
      } else if (rgbType == BufferedImage.TYPE_3BYTE_BGR) {
        bufferedImageToRGBNumImage(bufferedImage)
      } else
        None
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
        ex.printStackTrace()
        None
      }
    }
  }

  def main(args: Array[String]): Unit = {
    println(s"args: ${args.toSeq}")
    val filename = args(0)
    val imageOpt = LoadImage.loadFile(filename).toOption
    imageOpt match {
      case Some(image) => {
        val wrappedOpt = bufferedImageToRGBIntImage(image)
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