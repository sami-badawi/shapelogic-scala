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
  }

}