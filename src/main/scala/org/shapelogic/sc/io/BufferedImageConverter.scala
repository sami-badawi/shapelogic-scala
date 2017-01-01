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
 * Factory for this currently only handles the easy AWT BufferedImage
 */
object BufferedImageConverter {

  val coveredBufferedImageTypeSet: Set[Int] = Set(
    BufferedImage.TYPE_3BYTE_BGR,
    BufferedImage.TYPE_BYTE_GRAY,
    BufferedImage.TYPE_4BYTE_ABGR,
    BufferedImage.TYPE_4BYTE_ABGR_PRE)

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

  def awtBufferedImage2BufferImage(awtBufferedImage: BufferedImage): Option[BufferImage[Byte]] = {
    val rgbType = awtBufferedImage.getType
    println(s"rgbType: $rgbType")
    val colorModel = awtBufferedImage.getColorModel
    println(s"colorModel: $colorModel")
    if (coveredBufferedImageTypeSet.contains(rgbType)) {
      try {
        val raster = awtBufferedImage.getData
        val byteBuffer: Array[Byte] = rasterToByteArray(raster)
        val res: BufferImage[Byte] =
          if (rgbType == BufferedImage.TYPE_3BYTE_BGR) {
            val byteArray = raster.getDataBuffer.asInstanceOf[DataBufferByte].getData
            new BufferImage(
              width = awtBufferedImage.getWidth,
              height = awtBufferedImage.getHeight,
              numBands = 3,
              bufferInput = byteArray,
              rgbOffsetsOpt = Some(bgrRGBOffsets))
          } else if (rgbType == BufferedImage.TYPE_4BYTE_ABGR ||
            rgbType == BufferedImage.TYPE_4BYTE_ABGR_PRE) {
            val byteArray = raster.getDataBuffer.asInstanceOf[DataBufferByte].getData
            val byteArrayCount = byteArray.size
            val exptected = awtBufferedImage.getWidth * awtBufferedImage.getHeight * 4
            println(s"byteArrayCount: $byteArrayCount, exptected: $exptected")
            new BufferImage(
              width = awtBufferedImage.getWidth,
              height = awtBufferedImage.getHeight,
              numBands = 4,
              bufferInput = byteArray,
              rgbOffsetsOpt = Some(abgrRGBOffsets))
          } else if (rgbType == BufferedImage.TYPE_BYTE_GRAY)
            new BufferImage(
              width = awtBufferedImage.getWidth,
              height = awtBufferedImage.getHeight,
              numBands = 3,
              bufferInput = byteBuffer,
              rgbOffsetsOpt = Some(grayRGBOffsets))
          else {
            println(s"rgbType: $rgbType")
            null
          }
        Some(res)
      } catch {
        case ex: Throwable => {
          println("awtBufferedImage2BufferImage error:" + ex.getMessage)
          None
        }
      }
    } else
      None
  }

  /**
   * This will create ReadImage[Byte] that is a WrappedRGBIntBufferedImage
   *
   * Not sure if this will be used
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
      } else {
        BufferedImageConverter.awtBufferedImage2BufferImage(bufferedImage)
      }
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
        ex.printStackTrace()
        None
      }
    }
  }

  def bufferImage2AwtBufferedImage(awtBufferedImage: BufferImage[Byte]): Option[BufferedImage] = {
    None
  }
}