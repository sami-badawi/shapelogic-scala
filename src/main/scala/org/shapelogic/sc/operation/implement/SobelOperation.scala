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
import org.shapelogic.sc.pixel.PixelHandlerSame
import org.shapelogic.sc.pixel.implement.SobelPixel
import org.shapelogic.sc.numeric.NumberPromotion
import org.shapelogic.sc.numeric.PrimitiveNumberPromotersAux

object SobelOperation {

  /**
   * Generic version
   */
  def makeTransform[T: ClassTag, C: ClassTag: Numeric](inputImage: BufferImage[T])(implicit mumberPromotion: NumberPromotion.Aux[T, C]): BufferImage[T] = {
    val sobelPixel = new SobelPixel.SobelPixelG[T, C](inputImage)(mumberPromotion)
    val baseOperation = new BaseOperation[T, C](inputImage)(sobelPixel)
    baseOperation.result
  }

  def sobelOperationByteFunction(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransform[Byte, Int](inputImage)
  }

  def sobelOperationShortFunction(inputImage: BufferImage[Short]): BufferImage[Short] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransform[Short, Int](inputImage)
  }
}