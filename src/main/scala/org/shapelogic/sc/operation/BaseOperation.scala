package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

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
class BaseOperation[@specialized T: ClassTag, @specialized A: ClassTag](
    bufferImageIn: BufferImage[T], conv: T => A) extends PixelOperation(bufferImageIn) {

}