package org.shapelogic.sc.numeric

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class GenericInverseSpec extends FunSuite with BeforeAndAfterEach {

  test("Check Direct") {
    import GenericInverse.DirectInverse._
//    import TransFunction.ops._ // needs Simulacrum
    val byte: Byte = -1
//    assertResult(0) { byte.transform }
    assertResult(0) { implicitly[TransFunction[Byte]].transform(byte) }
  }

}