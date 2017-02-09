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
import java.awt.image.MemoryImageSource
import java.awt.color.ColorSpace
import scala.util.Failure
import scala.util.Success

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

  def awtBufferedImage2BufferImageTry(awtBufferedImage: BufferedImage): Try[BufferImage[Byte]] = {
    Try({
      val rgbType = awtBufferedImage.getType
      println(s"rgbType: $rgbType")
      val colorModel = awtBufferedImage.getColorModel
      println(s"out colorModel: $colorModel")
      if (coveredBufferedImageTypeSet.contains(rgbType)) {

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
            println(s"Problem imssing rgbType converter for: $rgbType")
            throw new Exception(s"Problem imssing rgbType converter for: $rgbType")
          }
        res
      } else {
        println(s"Problem imssing rgbType converter for: $rgbType")
        throw new Exception(s"Problem imssing rgbType converter for: $rgbType")
      }
    })
  }

  def awtBufferedImage2BufferImage(awtBufferedImage: BufferedImage): Option[BufferImage[Byte]] = {
    val imageTry = awtBufferedImage2BufferImageTry(awtBufferedImage)
    imageTry match {
      case Success(image) => Some(image)
      case Failure(ex) => {
        println(s"awtBufferedImage2BufferImage: ${ex.getMessage}")
        None
      }
    }
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

  /**
   *
   */
  def makeAwtIntImage(width: Int, height: Int): Image = {
    val pixels = new Array[Int](width * height) // 0xAARRGGBB
    val source = new MemoryImageSource(width, height, pixels, 0, width);
    source.setAnimated(true);
    source.setFullBufferUpdates(true);
    val image: Image = Toolkit.getDefaultToolkit().createImage(source);
    image.setAccelerationPriority(1f);
    image
  }

  /**
   * This is not a pretty clumsy
   */
  def image2BufferedImage(img: Image, imageType: Int = BufferedImage.TYPE_INT_ARGB): BufferedImage = {
    if (img.isInstanceOf[BufferedImage]) {
      return img.asInstanceOf[BufferedImage]
    }

    // Create empty buffered image
    val bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), imageType)

    // Draw the image on to the buffered image
    val graphics2D: Graphics2D = bufferedImage.createGraphics()
    graphics2D.drawImage(img, 0, 0, null)
    graphics2D.dispose()
    bufferedImage
  }

  def byteArray2BufferedImage(array: Array[Byte], width: Int, height: Int): BufferedImage = {
    val cs: ColorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY)
    val nBits = Array[Int](8)
    val cm: ColorModel = new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE)
    val sampleModel: SampleModel = cm.createCompatibleSampleModel(width, height)
    val db: DataBufferByte = new DataBufferByte(array, width * height)
    val raster: WritableRaster = Raster.createWritableRaster(sampleModel, db, null)
    val bfferedImage: BufferedImage = new BufferedImage(cm, raster, false, null)
    bfferedImage
  }

  /**
   * int w, int h, ColorModel cm,
   * byte[] pix, int off, int scan
   *
   * Not sure what the ColorModel is doing
   */
  def bufferImage2AwtBufferedImage(bufferImage: BufferImage[Byte]): Option[BufferedImage] = {
    if (bufferImage.numBands == 1) {
      return Try(byteArray2BufferedImage(bufferImage.data, bufferImage.width, bufferImage.height)).toOption
    }
    val colorModel: ColorModel = ColorModel.getRGBdefault
    try {
      val source = new MemoryImageSource(
        bufferImage.width,
        bufferImage.height,
        colorModel,
        bufferImage.data,
        0,
        bufferImage.width);
      val image: Image = Toolkit.getDefaultToolkit().createImage(source);
      val bufferedImage = if (bufferImage.numBands == 1)
        image2BufferedImage(image, BufferedImage.TYPE_BYTE_GRAY)
      else
        image2BufferedImage(image, BufferedImage.TYPE_4BYTE_ABGR)
      Some(bufferedImage)
    } catch {
      case ex: Throwable => {
        println("bufferImage2AwtBufferedImage " + ex.getMessage)
        None
      }
    }
  }
}