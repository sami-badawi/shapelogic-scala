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

  /**
   * This is redundant now, but the generic only worked after adding context bound on TransFunction
   */
  def makeTransformByte(
    inputImage: BufferImage[Byte])(implicit tf: TransFunction[Byte]): SimpleTransform[Byte] = {
    type T = Byte
    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = genericFunction.transform
    new SimpleTransform[T](inputImage)(function)
  }

  def inverseTransformByte(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    import GenericInverse.DirectInverse._
    makeTransformByte(inputImage).result
  }

  def blackTransformByte(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    import GenericFunctions.DirectBlack._
    makeTransformByte(inputImage).result
  }
  /**
   * First fully generic image operation
   * Only the TransFunction context bound is needed
   * Maybe remove the other
   */
  def makeInverseTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering: TransFunction](
    inputImage: BufferImage[T]): SimpleTransform[T] = {
    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = genericFunction.transform
    new SimpleTransform[T](inputImage)(function)
  }

  /**
   * Fully generic image operation
   */
  def makeBlackTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering: TransFunction](
    inputImage: BufferImage[T]): SimpleTransform[T] = {
    import GenericInverse.DirectInverse._
    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = genericFunction.transform
    new SimpleTransform[T](inputImage)(function)
  }
}