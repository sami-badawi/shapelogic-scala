package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
 * This idea is that you can run over an image
 * 
 * Not used now
 */
class BaseOperation[@specialized T: ClassTag, @specialized A: ClassTag](
    bufferImageIn: BufferImage[T], conv: T => A) extends PixelOperation(bufferImageIn) {

  var reciever: A = _
  var accumulator: A = _

  def retriev(): A = {
    conv(bufferImage.data(index))
  }
}