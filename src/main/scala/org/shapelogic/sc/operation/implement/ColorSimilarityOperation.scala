package org.shapelogic.sc.operation.implement

import org.shapelogic.sc.image._
import org.shapelogic.sc.pixel.implement.SobelPixel._
import spire.math.Numeric._
import spire.algebra._
import spire.math._
import spire.implicits._
import scala.reflect.runtime.universe._
import org.shapelogic.sc.operation.BaseOperation
import scala.reflect.ClassTag
import org.shapelogic.sc.pixel.PixelDistance
import org.shapelogic.sc.pixel.PixelHandlerSame
import org.shapelogic.sc.numeric.NumberPromotion
import org.shapelogic.sc.numeric.PrimitiveNumberPromotersAux
import scala.math.Ordering
import org.shapelogic.sc.operation.BaseOperationByteResult

object ColorSimilarityOperation {

  /**
   * Generic version
   */
  def makeTransform[T: ClassTag, C: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T],
    colorArray: Array[T],
    maxDistance: C,
    similarIsMatch: Boolean)(
      implicit mumberPromotion: NumberPromotion.Aux[T, C] //
      ): BufferImage[Byte] = {
    lazy val pixelDistance = new PixelDistance[T, C](inputImage, maxDistance, similarIsMatch)(
      implicitly[ClassTag[T]],
      implicitly[ClassTag[C]],
      implicitly[Numeric[C]],
      implicitly[Ordering[C]],
      mumberPromotion)
    pixelDistance.setReferencePointArray(colorArray)
    val baseOperation = new BaseOperationByteResult[T, C](inputImage)(pixelDistance)
    baseOperation.result
  }

  def makeTransformFromPoint[T: ClassTag, C: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T],
    x: Int,
    y: Int,
    maxDistance: C,
    similarIsMatch: Boolean)(
      implicit mumberPromotion: NumberPromotion.Aux[T, C]): BufferImage[Byte] = {
    lazy val pixelDistance = new PixelDistance[T, C](inputImage, maxDistance, similarIsMatch)(
      implicitly[ClassTag[T]],
      implicitly[ClassTag[C]],
      implicitly[Numeric[C]],
      implicitly[Ordering[C]],
      mumberPromotion)
    pixelDistance.takeColorFromPoint(x, y)
    val baseOperation = new BaseOperationByteResult[T, C](inputImage)(pixelDistance)
    baseOperation.result
  }

  def colorSimilarOperationByteFunction(
    inputImage: BufferImage[Byte],
    colorArray: Array[Byte],
    maxDistance: Int,
    similarIsMatch: Boolean): BufferImage[Byte] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransform[Byte, Int](inputImage, colorArray, maxDistance, similarIsMatch)
  }

  def pointSimilarOperationByteFunction(
    inputImage: BufferImage[Byte],
    x: Int,
    y: Int,
    maxDistance: Int,
    similarIsMatch: Boolean): BufferImage[Byte] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransformFromPoint[Byte, Int](inputImage, x, y, maxDistance, similarIsMatch)
  }

  def colorSimilarOperationShortFunction(
    inputImage: BufferImage[Short],
    colorArray: Array[Short],
    maxDistance: Int,
    similarIsMatch: Boolean): BufferImage[Byte] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransform[Short, Int](inputImage, colorArray, maxDistance, similarIsMatch)
  }
}