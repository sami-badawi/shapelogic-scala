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
import org.shapelogic.sc.image.ImageTransformWithName
import org.shapelogic.sc.image.ImageTransformWithNameT
import org.shapelogic.sc.image.ImageTransformDialog
import org.shapelogic.sc.image.ImageTransformDialogT
import org.shapelogic.sc.operation.implement.ImageOperationBandSwap
import org.shapelogic.sc.operation.implement.SobelOperation

object Transforms {

  /**
   * This is redundant now, but the generic only worked after adding context bound on TransFunction
   */
  def makeTransformByte(
    inputImage: BufferImage[Byte])(implicit tf: TransFunction[Byte]): SimpleTransform[Byte] = {
    type T = Byte
    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = tf.transform _
    new SimpleTransform[T](inputImage)(function)
  }

  def inverseTransformByte(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    import GenericInverse.DirectInverse._
    AssembleOperation.makeGenericTransFunctionInstance[Byte](inputImage)
  }

  def blackTransformByte(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    import GenericFunctions.DirectBlack._
    AssembleOperation.makeGenericTransFunctionInstance[Byte](inputImage)
  }

  def whiteTransformByte(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    import GenericFunctions.DirectWhite._
    AssembleOperation.makeGenericTransFunctionInstance[Byte](inputImage)
  }

  /**
   * First fully generic image operation
   * Only the TransFunction context bound is needed
   * Maybe remove the other
   */
  def makeTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: TransFunction](
    inputImage: BufferImage[T]): SimpleTransform[T] = {
    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = genericFunction.transform
    new SimpleTransform[T](inputImage)(function)
  }

  /**
   * This is packing up all the image operation to be displayed in
   *
   * For loose coupling there is no GUI concept here
   */
  def makeImageTransformWithNameSeq(): Seq[ImageTransformWithNameT] = {
    Seq(
      ImageTransformWithName(inverseTransformByte, "Inverse"),
      ImageTransformWithName(ImageOperationBandSwap.redBlueImageOperationTransform, "Swap"),
      ImageTransformWithName(SobelOperation.sobelOperationByteFunction, "Sobel"),
      ImageTransformWithName(blackTransformByte, "Make image Black"),
      ImageTransformWithName(whiteTransformByte, "Make image White"))
  }

  def makeImageTransformDialogSeq(): Seq[ImageTransformDialogT] = {
    Seq(
      ImageTransformDialog(
        transform = ThresholdOperation.makeByteTransform,
        name = "Threshold",
        dialog = "Input threshold",
        defaultValue = "111"),
      ImageTransformDialog(ChannelChoserOperation.makeByteTransform, "Channel Choser", "Channel number", "1"))
  }

}