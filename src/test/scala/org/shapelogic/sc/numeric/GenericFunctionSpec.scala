package org.shapelogic.sc.numeric

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class GenericFunctionSpec extends FunSuite with BeforeAndAfterEach {

  test("") {
    implicit val genericFunctionEx = new GenericFunction[Byte] {
      type Res = Int
      val byteMask: Int = 0xff
      def transform(input: Byte): Int = {
        input & byteMask
      }
    }
    val byte: Byte = -1
    assertResult(255) { genericFunctionEx.transform(byte) }
  }

}