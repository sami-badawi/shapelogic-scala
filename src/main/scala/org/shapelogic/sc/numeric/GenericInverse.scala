package org.shapelogic.sc.numeric

import scala.reflect.ClassTag
import scala.specialized

import spire.math.Numeric
import spire.math._

object GenericInverse {
  object DirectInverse {

    val onesByte: Byte = -1
    implicit lazy val byteInverse: TransFunction[Byte] = new TransFunction[Byte] {
      def transform(input: Byte): Byte = {
        (~input).toByte
      }
    }

    implicit lazy val shortInverse: TransFunction[Short] = new TransFunction[Short] {
      def transform(input: Short): Short = {
        (~input).toShort
      }
    }

    implicit lazy val intInverse: TransFunction[Int] = new TransFunction[Int] {
      def transform(input: Int): Int = {
        ~input
      }
    }

    implicit lazy val floatInverse: TransFunction[Float] = new TransFunction[Float] {
      def transform(input: Float): Float = {
        1.0f - input
      }
    }

    implicit lazy val doubleInverse: TransFunction[Double] = new TransFunction[Double] {
      def transform(input: Double): Double = {
        1.0 - input
      }
    }
  }

}