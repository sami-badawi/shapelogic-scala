package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.pixel.PixelHandler1

/**
 * This idea is that you can run over an image
 *
 * Many input channels one output channel possibly an alpha output channel
 * BaseOperation has no knowledge of the internals of the numbers
 * It is just a runner
 * If it was not for demands by BufferImage all it needed was
 * context bounds for:
 * ClassTag and the transform: T => T parameter
 *
 */
abstract class BaseOperation[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T])(implicit promoter: PixelHandler1.Aux[T, O]) {
  val pixelOperation: PixelOperation[T] = new PixelOperation[T](inputImage)

}