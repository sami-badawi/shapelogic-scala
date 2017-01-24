package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation

import spire.math.Numeric
import spire.implicits._

import scala.specialized
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.numeric.NumberPromotionMax

/**
 * Takes input image and create output image with same shape.
 * Almost identical to SimpleTransform but this can create a different result buffer type
 * 
 * The reason that both SimpleTransform and ChannelTransform exist is that
 * specialization create a version of the class for every combination of the
 *  generic type parameters T and O
 * 
 * ChannelTransform has no knowledge of the internals of the numbers
 * It is just a runner
 * If it was not for demands by BufferImage all it needed was
 * context bounds for:
 * ClassTag and the transform: T => O parameter
 */
class ChannelTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T])(transform: T => O, alphaTransform: T => O) {

  /**
   * Having this be a lazy init did not work
   */
  var outputImage: BufferImage[O] = null
  val verboseLogging = false

  lazy val inBuffer = inputImage.data
  lazy val rgbOffsets = inputImage.getRGBOffsetsDefaults
  lazy val alphaChannel = if (rgbOffsets.hasAlpha) rgbOffsets.alpha else -1
  lazy val outBuffer = outputImage.data
  lazy val inputNumBands = inputImage.numBands
  lazy val pixelOperation: PixelOperation[T] = new PixelOperation(inputImage)

  /**
   * Not taking alpha channel into account it should
   */
  def handleIndex(index: Int, indexOut: Int): Unit = {
    try {
      var i = 0
      do {
        if (i == alphaChannel)
          outBuffer(index + i) = alphaTransform(inBuffer(index + i))
        else {
          outBuffer(index + i) = transform(inBuffer(index + i))
          //          outBuffer.update(index + i, transform(inBuffer(index + i)))
          if (verboseLogging) {
            try {
              val input = inBuffer(index + i).toInt
              val value = transform(inBuffer(index + i))
              val out = outBuffer(index + i).toInt
              println(s"input: $input, value: $value, out: $out")
            } catch {
              case ex2: Throwable => {
              }
            }
          }
        }
        i += 1
      } while (i < inputNumBands)

    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
      }
    }
  }

  /**
   * Run over input and output
   * Should I do by line?
   */
  def calc(): BufferImage[O] = {
    outputImage = new BufferImage(
      width = inputImage.width,
      height = inputImage.height,
      numBands = inputImage.numBands,
      bufferInput = null,
      rgbOffsetsOpt = inputImage.rgbOffsetsOpt)
    val pointCount = inputImage.width * inputImage.height
    pixelOperation.reset()
    var count = 0
    var index: Int = pixelOperation.index
    while (pixelOperation.hasNext) {
      index = pixelOperation.next()
      handleIndex(index, indexOut = index)
      count += 1
    }
    if (verboseLogging)
      println(s"count: $count, index: $index")
    outputImage
  }

  lazy val result: BufferImage[O] = calc()
}