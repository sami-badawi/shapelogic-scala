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

  class Color2GrayOperationShort(inputImage: BufferImage[Short]) extends BaseOperation[Short, Int](inputImage)(new Color2GrayHandlerShort(inputImage)) {
  }

  class Color2GrayOperationInt(inputImage: BufferImage[Int]) extends BaseOperation[Int, Int](inputImage)(new Color2GrayHandlerInt(inputImage)) {
  }

  class Color2GrayOperationFloat(inputImage: BufferImage[Float]) extends BaseOperation[Float, Float](inputImage)(new Color2GrayHandlerFloat(inputImage)) {
  }

  class Color2GrayOperationDouble(inputImage: BufferImage[Double]) extends BaseOperation[Double, Double](inputImage)(new Color2GrayHandlerDouble(inputImage)) {
  }
}