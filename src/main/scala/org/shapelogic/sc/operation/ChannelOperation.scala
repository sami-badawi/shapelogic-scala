package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.pixel.PixelHandlerSame

/**
 * This idea is that you can run over an image
 *
 * Many input channels same numer of output channel possibly an alpha output channel
 * Input and output type are the same
 * The channels are handled in parallel, not swapping
 * ChannelOperation has no knowledge of the internals of the numbers
 * It is just a runner
 *
 * Example of use: edge detector working by band
 */
class ChannelOperation[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag](
    inputImage: BufferImage[T])(pixelHandler: PixelHandlerSame.Aux[T, O]) {
  val verboseLogging = false

  var outputImage: BufferImage[T] = null

  lazy val pixelOperation: PixelOperation[T] = new PixelOperation[T](inputImage)
  lazy val inBuffer = inputImage.data
  lazy val outBuffer = outputImage.data
  lazy val rgbOffsets = inputImage.getRGBOffsetsDefaults
  lazy val alphaChannel = if (rgbOffsets.hasAlpha) rgbOffsets.alpha else -1
  lazy val numBands = inputImage.numBands

  def handleIndex(index: Int, indexOut: Int): Unit = {
    try {
      for (i <- Range(0, numBands))
        if (i != alphaChannel)
          outBuffer(indexOut + 1) = pixelHandler.calc(index + i)
        else
          outBuffer(indexOut + 1) = inBuffer(index + i)
    } catch {
      case ex: Throwable => print(",")
    }
  }

  def makeOutputImage(): BufferImage[T] = {
    new BufferImage[T](
      width = inputImage.width,
      height = inputImage.height,
      numBands = inputImage.numBands,
      bufferInput = null,
      rgbOffsetsOpt = None)
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
      indexOut += 1
      handleIndex(index, indexOut)
    }
    if (verboseLogging)
      println(s"count: $indexOut, index: $index")
    outputImage
  }

  lazy val result: BufferImage[T] = calc()
}