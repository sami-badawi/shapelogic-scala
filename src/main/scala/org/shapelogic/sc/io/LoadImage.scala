package org.shapelogic.sc.io

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._
import scala.util.Try
import org.shapelogic.sc.image.RGBNumImage

object LoadImage {

  def loadFile(filename: String): Try[BufferedImage] = {
    Try {
      val img: BufferedImage = ImageIO.read(new File(filename))
      val height = img.getHeight
      println(s"filename: $filename had height: $height")
      img
    }
  }
  
  def intArrayToByteArray(intArray: Array[Int]): Array[Byte] = {
    null
  }

  def bufferedImageToRGBNumImage(bufferedImage: BufferedImage): Option[RGBNumImage[Byte]] = {
    if (bufferedImage.getColorModel == BufferedImage.TYPE_INT_RGB)
      Try({
        val intArray = bufferedImage.getData
        val byteBuffer: Array[Byte] = intArrayToByteArray( null)
        val res: RGBNumImage[Byte]= new RGBNumImage(width = bufferedImage.getWidth, height = bufferedImage.getHeight, bufferIn = byteBuffer )
        res
      }).toOption
    else
      None
  }

  def main(args: Array[String]): Unit = {
    val filename = args(0)
    loadFile(filename)
  }
}