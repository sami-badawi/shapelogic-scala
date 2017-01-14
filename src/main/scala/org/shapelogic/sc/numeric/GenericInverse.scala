package org.shapelogic.sc.numeric

import simulacrum._

import scala.reflect.ClassTag
import scala.specialized

import spire.math.Numeric
import spire.math._

object GenericInverse {
  object DirectInverse {

    val onesByte: Byte = -1
    implicit lazy val byteInverse: GenericFunction[Byte] = new GenericFunction[Byte] {
      type Res = Byte
      def transform(input: Byte): Byte = {
        (~input).toByte
      }
    }

    implicit lazy val shortInverse: GenericFunction[Short] = new GenericFunction[Short] {
      type Res = Short
      def transform(input: Short): Short = {
        (~input).toShort
      }
    }

    implicit lazy val intInverse: GenericFunction[Int] = new GenericFunction[Int] {
      type Res = Int
      def transform(input: Int): Int = {
        ~input
      }
    }

    implicit lazy val floatInverse: GenericFunction[Float] = new GenericFunction[Float] {
      type Res = Float
      def transform(input: Float): Float = {
        1.0f - input
      }
    }

    implicit lazy val doubleInverse: GenericFunction[Double] = new GenericFunction[Double] {
      type Res = Double
      def transform(input: Double): Double = {
        1.0 - input
      }
    }
  }

}