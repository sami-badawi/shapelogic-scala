package org.shapelogic.sc.operation

import spire.math.Numeric
import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.pixel.PixelOperation
import org.shapelogic.sc.pixel.PixelHandler

/**
 * This will always create another image of same dimensions
 * Not sure if it should always have the same number of bands
 * It it working by color band in parallel
 * It is hard to give a good signature that catches go up and go down
 * Maybe simpler to make a gray to color and color to gray version of this
 *
 * XXX For now I assume that the two images are identical size. This might change later
 * XXX Might not need @specialized
 */
class ImageOperation[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag](
    inputImage: BufferImage[T])(pixelHandler: PixelHandler.Aux[T, O]) {
  lazy val pixelOperation: PixelOperation[T] = new PixelOperation[T](inputImage)

  var outputImage: BufferImage[T] = null
  lazy val outBuffer = outputImage.data
  lazy val inputBuffer = inputImage.data
  lazy val rgbOffsets = inputImage.getRGBOffsetsDefaults
  lazy val alphaChannel = if (rgbOffsets.hasAlpha) rgbOffsets.alpha else -1
  lazy val numBands = inputImage.numBands
  val verboseLogging = false

  def handleIndex(index: Int, indexOut: Int): Unit = {
    try {
      var i = 0
      do {
        if (i == alphaChannel)
          outBuffer(indexOut + i) = inputBuffer(index + i)
        else
          outBuffer(indexOut + i) = pixelHandler.calc(index, i)
        i += 1
      } while (i < numBands)
    } catch {
      case ex: Throwable => print(",")
    }
  }

  def makeOutputImage(): BufferImage[T] = {
    BufferImage.makeBufferImage[T](
      width = inputImage.width,
      height = inputImage.height,
      numBands = inputImage.numBands,
      bufferInput = null,
      rgbOffsetsOpt = inputImage.rgbOffsetsOpt)
  }

  /**
   * Run over input and output
   * Should I do by line?
   */
  def calc(): BufferImage[T] = {
    outputImage = makeOutputImage()
    val pointCount = inputImage.width * inputImage.height
    pixelOperation.reset()
    var count = 0
    var indexOut: Int = -1
    var index: Int = pixelOperation.index
    while (pixelOperation.hasNext) {
      index = pixelOperation.next()
      handleIndex(index, index)
    }
    if (verboseLogging)
      println(s"count: $indexOut, index: $index")
    outputImage
  }

  lazy val result: BufferImage[T] = calc()

}

object ImageOperation {

  //  /**
  //   * This will make a deep copy of an image
  //   */
  //  def copy[T: ClassTag: Numeric](bufferImage: BufferImage[T]): ImageOperation[T, T] = {
  //    new ImageOperation[T, T](bufferImage, Predef.identity)
  //  }
  //
  //  val byteFloatConvert: Float = 1.0f / 255.0f
  //  def byte2Float[T: ClassTag: Numeric](bufferImage: BufferImage[Byte]): ImageOperation[Byte, Float] = {
  //    new ImageOperation[Byte, Float](bufferImage, (byte: Byte) => byte * byteFloatConvert)
  //  }
  //
  //  def constantValue[T: ClassTag: Numeric](bufferImage: BufferImage[T], defalut: T): ImageOperation[T, T] = {
  //    new ImageOperation[T, T](bufferImage, _ => defalut)
  //  }
}