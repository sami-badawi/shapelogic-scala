package org.shapelogic.sc.operation

import spire.math.Numeric
import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.image._
import org.shapelogic.sc.pixel.PixelHandler
import org.shapelogic.sc.pixel.PixelHandlerSwap

/**
 * This will always create another image of same dimensions
 * It it working by color band, but they can be swapped
 *
 * XXX For now I assume that the two images are identical size. This might change later
 */
class ImageOperationBandSwap[T: ClassTag](
    bufferImage: BufferImage[T], swap: Seq[Int]) extends ImageOperation[T, T](bufferImage)(pixelHandler = new PixelHandlerSwap[T](bufferImage, swap)) {

}

object ImageOperationBandSwap {

  val redBlueSwap = Array(0, 2, 1, 3)

  def redBlueImageOperationBandSwap[T: ClassTag](bufferImage: BufferImage[T]): ImageOperationBandSwap[T] = {
    new ImageOperationBandSwap[T](bufferImage, redBlueSwap)
  }

  def redBlueImageOperationTransform[T: ClassTag](bufferImage: BufferImage[T]): BufferImage[T] = {
    val imageOperation = new ImageOperationBandSwap[T](bufferImage, redBlueSwap)
    imageOperation.result
  }
}