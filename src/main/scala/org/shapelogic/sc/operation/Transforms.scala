package org.shapelogic.sc.operation

import spire.math.Numeric
import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.numeric._
import org.shapelogic.sc.pixel._
import spire.math._
import spire.implicits._
import scala.reflect.ClassTag
import org.shapelogic.sc.numeric.GenericInverse
import org.shapelogic.sc.numeric.GenericFunction
import org.shapelogic.sc.numeric.GenericFunction._

object Transforms {
  def makeInverseTransformByte(
    inputImage: BufferImage[Byte]): SimpleTransform[Byte] = {
    type T = Byte
    import GenericInverse.DirectInverse._
    import TransFunction.ops._

    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = genericFunction.transform
    val res = new SimpleTransform[T](inputImage)(function)
    res
  }

  def makeInverseTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T]): SimpleTransform[T] = {
    import GenericInverse.DirectInverse._

    // Missing cases that should never be called constraint to AnyRef prevents compile error
    import org.shapelogic.sc.numeric.fallback._

    import TransFunction.ops._

    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = genericFunction.transform
    val res = new SimpleTransform[T](inputImage)(function)
    res
  }
}