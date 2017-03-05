package org.shapelogic.sc.operation.implement

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.implement.Color2GrayHandler._
import spire.math.Numeric._
import spire.algebra._
import spire.math._
import spire.implicits._
import scala.reflect.runtime.universe._
import org.shapelogic.sc.operation.BaseOperation
import org.shapelogic.sc.numeric.NumberPromotion
import scala.reflect.ClassTag
import org.shapelogic.sc.pixel.implement.Color2GrayHandler
import org.shapelogic.sc.numeric.PrimitiveNumberPromotersAux

object Color2GrayOperation {

  // ================== Non generic ==================
  implicit class Color2GrayOperationByte(inputImage: BufferImage[Byte]) extends BaseOperation[Byte, Int](inputImage)(new Color2GrayHandlerByte(inputImage)) {
  }

  def color2GrayOperationByteFunction(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    //    Color2GrayHandlerByteM
    val hasBufferImage = new Color2GrayOperationByte(inputImage)
    hasBufferImage.result
  }

  // ================== Generic ==================
  def makeTransform[T: ClassTag, C: ClassTag: Numeric](inputImage: BufferImage[T])(implicit mumberPromotion: NumberPromotion.Aux[T, C]): BufferImage[T] = {
    val pixelOperation = new Color2GrayHandler.Color2GrayHandlerG[T, C](inputImage)(mumberPromotion)
    val baseOperation = new BaseOperation[T, C](inputImage)(pixelOperation)
    baseOperation.result
  }

  /**
   * Generic version, specialized to byte
   */
  def color2GrayByteTransform(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    import PrimitiveNumberPromotersAux.AuxImplicit._
    makeTransform[Byte, Int](inputImage)
  }
}