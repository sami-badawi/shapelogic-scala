package org.shapelogic.sc.numeric

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class GenericFunctionSpec extends FunSuite with BeforeAndAfterEach {

  lazy val bytePromoterGenericFunction: GenericFunction[Byte] = new GenericFunction[Byte] {
    type Res = Int
    val byteMask: Int = 0xff
    def transform(input: Byte): Int = {
      input & byteMask
    }
  }

  test("Create typeclass and use directly") {
    implicit val genericFunctionEx = bytePromoterGenericFunction
    val byte: Byte = -1
    assertResult(255) { genericFunctionEx.transform(byte) }
  }

  test("Create typeclass and use simulacrum and promoted type classes") {
    implicit val genericFunctionEx = bytePromoterGenericFunction
//    import GenericFunction.ops._ // needs Simulacrum
    val byte: Byte = -1
    assertResult(255) { bytePromoterGenericFunction.transform(byte) }
//    assertResult(255) { byte.transform }
  }

}