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
    val baseOperation = new BaseOperationByteResult[T, C](inputImage)(pixelDistance)
    baseOperation.result
  }

  def sobelOperationByteFunction(
    inputImage: BufferImage[Byte],
    maxDistance: Int,
    similarIsMatch: Boolean): BufferImage[Byte] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransform[Byte, Int](inputImage, maxDistance, similarIsMatch)
  }

  def sobelOperationShortFunction(
    inputImage: BufferImage[Short],
    maxDistance: Int,
    similarIsMatch: Boolean): BufferImage[Byte] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransform[Short, Int](inputImage, maxDistance, similarIsMatch)
  }
}