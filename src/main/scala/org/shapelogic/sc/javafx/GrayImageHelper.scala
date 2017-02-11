package org.shapelogic.sc.javafx

import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.IndexColorModel
import java.awt.image.Raster
import java.awt.image.RenderedImage
import java.awt.image.SampleModel
import java.awt.image.WritableRaster
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import javafx.scene.image.Image
import javax.imageio.ImageIO

/**
 * Somehow it JavaFX Image does not seem to have a good handling of gray images
 * So this operation goes over awt.image which is step of indirection that I
 * normally try to avoid.
 *
 * XXX If a better solution show up replace this
 */
object GrayImageHelper {

  /**
   * This is just gray scale byte represented RGB byte
   */
  def getGrayByteColorModel(): IndexColorModel = {
    val redBuffer: Array[Byte] = new Array[Byte](256)
    val greenBuffer: Array[Byte] = new Array[Byte](256)
    val blueBuffer: Array[Byte] = new Array[Byte](256)
    for (i <- Range(0, 256)) {
      redBuffer(i) = i.toByte
      greenBuffer(i) = i.toByte
      blueBuffer(i) = i.toByte
    }
    val grayByteColorModel: IndexColorModel = new IndexColorModel(8, 256, redBuffer, greenBuffer, blueBuffer)
    grayByteColorModel
  }

  def getIndexSampleModel(width: Int, height: Int): SampleModel = {
    val indexColorModel: IndexColorModel = getGrayByteColorModel()
    val writableRaster: WritableRaster = indexColorModel.createCompatibleWritableRaster(1, 1)
    var sampleModel: SampleModel = writableRaster.getSampleModel()
    sampleModel = sampleModel.createCompatibleSampleModel(width, height)
    sampleModel
  }

  def createBufferedImage(pixels: Array[Byte], width: Int, height: Int): BufferedImage = {
    val sampleModel: SampleModel = getIndexSampleModel(width, height)
    val db: DataBuffer = new DataBufferByte(pixels, width * height, 0)
    val raster: WritableRaster = Raster.createWritableRaster(sampleModel, db, null)
    val indexColorModel: IndexColorModel = getGrayByteColorModel()
    val bufferedImage: BufferedImage = new BufferedImage(indexColorModel, raster, false, null)
    bufferedImage
  }

  def grayBuffer2Image(bytearray: Array[Byte], width: Int, height: Int): Image = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    try {
      ImageIO.write(createBufferedImage(bytearray, width, height).asInstanceOf[RenderedImage], "png", out)
      out.flush()
      val in: ByteArrayInputStream = new ByteArrayInputStream(out.toByteArray())
      new javafx.scene.image.Image(in)
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
        null
      }
    }
  }
}