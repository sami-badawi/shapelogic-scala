package org.shapelogic.sc.io

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._
import scala.util.Try
import org.shapelogic.sc.image.WrappedRGBIntBufferedImage
import java.awt.image.Raster
import java.awt.image.DataBufferByte
import org.shapelogic.sc.image.ReadImage
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.image._

/**
 * More tricky loading of images via AWT BufferedImage
 */
object LoadImage {

  def loadAWTBufferedImage(filename: String): Try[BufferedImage] = {
    println(s"loadAWTBufferedImage for $filename")
    Try {
      val img: BufferedImage = ImageIO.read(new File(filename))
      val colorModel = img.getColorModel
      println(s"colorModel: $colorModel")
      val height = img.getHeight
      println(s"filename: $filename had height: $height")
      img
    }
  }

  /**
   * In AWT BufferedImage it takes several steps to get to the actual buffer
   */
  def rasterToByteArray(raster: Raster): Array[Byte] = {
    if (raster.getDataBuffer.getDataType == DataBuffer.TYPE_BYTE) {
      val size = raster.getDataBuffer.getSize
      val className = raster.getDataBuffer.getClass.getSimpleName
      println(s"Type is TYPE_BYTE, size: $size, className: $className")
      val imageBytes = raster.getDataBuffer.asInstanceOf[DataBufferByte].getData
      imageBytes
    } else
      null
  }

  /**
   *
   */
  def bufferedImageToRGBIntImage(bufferedImage: BufferedImage): Option[ReadImage[Byte]] = {
    val colorModel = bufferedImage.getColorModel
    val rgbType = bufferedImage.getType
    println(s"colorModel: $colorModel, \nrgbType: $rgbType")
    println(s"Expected: ${BufferedImage.TYPE_INT_RGB}")
    try {
      if (rgbType == BufferedImage.TYPE_INT_RGB) {
        val res = new WrappedRGBIntBufferedImage(bufferedImage)
        Some(res)
      } else if (rgbType == BufferedImage.TYPE_3BYTE_BGR) {
        BufferedImageConverter.awtBufferedImage2BufferImage(bufferedImage)
      } else
        println("Cannot open this format")
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
    val imageOpt = loadAWTBufferedImage(filename).toOption
    imageOpt match {
      case Some(image) => {
        val wrappedOpt = BufferedImageConverter.awtBufferedImage2BufferImage(image)
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