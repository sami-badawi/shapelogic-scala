//package org.shapelogic.sc.operation
//
//import spire.math.Numeric
//import spire.implicits._
//
//import org.shapelogic.sc.image.BufferImage
//import scala.reflect.ClassTag
//import scala.reflect.runtime.universe._
//import org.shapelogic.sc.image._
//import org.shapelogic.sc.pixel.PixelHandler
//import org.shapelogic.sc.pixel.PixelHandlerSwap
//
///**
// * This will always create another image of same dimensions
// * It it working by color band, but they can be swapped
// *
// * XXX For now I assume that the two images are identical size. This might change later
// */
//class ImageOperationBandSwap[T: ClassTag: Numeric, A: ClassTag](
//    bufferImage: BufferImage[T], conv: T => A, swap: Seq[Int]) extends ImageOperation[T, T](bufferImage)(pixelHandler = new PixelHandlerSwap[T](bufferImage, swap)) {
//
//  override def calcAndSet(): Unit = {
//    var i = 0
//    do {
//      outputImage.data(index + swap(i)) = conv(bufferImage.data(index + i))
//      i += 1
//    } while (i < numBands)
//  }
//}
//
//object ImageOperationBandSwap {
//
//  def redBlueImageOperationBandSwap[T: ClassTag: Numeric](bufferImage: BufferImage[T]): ImageOperationBandSwap[T, T] = {
//    new ImageOperationBandSwap[T, T](bufferImage, Predef.identity, redBlueSwap)
//  }
//}