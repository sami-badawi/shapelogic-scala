package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.Color2GrayHandler._
import spire.math.Numeric
import spire.math.Numeric._
import spire.math.Integral
import spire.algebra._ 
import spire.math._
import spire.implicits._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

object Color2GrayOperation {
  class Color2GrayOperationByte(inputImage: BufferImage[Byte]) extends BaseOperation[Byte, Int](inputImage)(new Color2GrayHandlerByte(inputImage)) {
  }

}