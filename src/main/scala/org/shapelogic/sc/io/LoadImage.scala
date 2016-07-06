package org.shapelogic.sc.io

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._
import scala.util.Try
import org.shapelogic.sc.image.RGBNumImage
import org.shapelogic.sc.image.RGBIntBufferedImage

object LoadImage {

  def loadFile(filename: String): Try[BufferedImage] = {
    Try {
      val img: BufferedImage = ImageIO.read(new File(filename))
      val colorModel = img.getColorModel
      println(s"colorModel: $colorModel")
      val height = img.getHeight
      println(s"filename: $filename had height: $height")
      img
    }
  }

  def intArrayToByteArray(intArray: Array[Int]): Array[Byte] = {
    null
  }

  def bufferedImageToRGBNumImage(bufferedImage: BufferedImage): Option[RGBNumImage[Byte]] = {
    val colorModel = bufferedImage.getColorModel
    println(s"colorModel: $colorModel")
    if (bufferedImage.getColorModel == BufferedImage.TYPE_INT_RGB)
      Try({
        val intArray = bufferedImage.getData
        val byteBuffer: Array[Byte] = intArrayToByteArray(null)
        val res: RGBNumImage[Byte] = new RGBNumImage(width = bufferedImage.getWidth, height = bufferedImage.getHeight, bufferIn = byteBuffer)
        res
      }).toOption
    else
      None
  }

  def bufferedImageToRGBIntImage(bufferedImage: BufferedImage): Option[RGBIntBufferedImage] = {
    val colorModel = bufferedImage.getColorModel
    println(s"colorModel: $colorModel")
    if (bufferedImage.getColorModel == BufferedImage.TYPE_INT_RGB)
      try {
        val res = new RGBIntBufferedImage(bufferedImage)
        Some(res)
      } catch {
        case ex: Throwable => {
          println(ex.getMessage)
          ex.printStackTrace()
          None
        }
      }
    else
      None
  }

  def main(args: Array[String]): Unit = {
    println(s"args: ${args.toSeq}")
    val filename = args(0)
    val imageOpt = loadFile(filename).toOption
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