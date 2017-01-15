package org.shapelogic.sc.numeric

import simulacrum._

import scala.reflect.ClassTag
import scala.specialized

import spire.math.Numeric
import spire.math._

object GenericInverse {
  object DirectInverse {

    val onesByte: Byte = -1
    implicit lazy val byteInverse: TransFunction[Byte] = new TransFunction[Byte] {
      type Res = Byte
      def transform(input: Byte): Byte = {
        (~input).toByte
      }
    }

    implicit lazy val shortInverse: TransFunction[Short] = new TransFunction[Short] {
      type Res = Short
      def transform(input: Short): Short = {
        (~input).toShort
      }
    }

    implicit lazy val intInverse: TransFunction[Int] = new TransFunction[Int] {
      type Res = Int
      def transform(input: Int): Int = {
        ~input
      }
    }

    implicit lazy val floatInverse: TransFunction[Float] = new TransFunction[Float] {
      type Res = Float
      def transform(input: Float): Float = {
        1.0f - input
      }
    }

    implicit lazy val doubleInverse: TransFunction[Double] = new TransFunction[Double] {
      type Res = Double
      def transform(input: Double): Double = {
        1.0 - input
      }
    }
  }

}