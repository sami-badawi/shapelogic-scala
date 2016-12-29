package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.image._

/**
 * This will always create another image of same dimensions
 * It it working by color band, but they can be swapped
 *
 * XXX For now I assume that the two images are identical size. This might change later
 */
class ImageOperationBandSwap[T: ClassTag, A: ClassTag](
    bufferImage: BufferImage[T], conv: T => A, swap: Seq[Int]) extends ImageOperation(bufferImage, conv) {

  override def calcAndSet(): Unit = {
    var i = 0
    do {
      outputImage.data(index + swap(i)) = conv(bufferImage.data(index + i))
      i += 1
    } while (i < numBands)
  }
}

object ImageOperationBandSwap {

  def redBlueImageOperationBandSwap[T: ClassTag](bufferImage: BufferImage[T]): ImageOperationBandSwap[T, T] = {
    new ImageOperationBandSwap[T, T](bufferImage, x => x, redBlueSwap)
  }
}