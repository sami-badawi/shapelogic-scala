package org.shapelogic.sc.util

import org.scalatest._

import ColorHelper._

class ColorHelperSpec extends FunSuite with BeforeAndAfterEach {
  test("alpha1") {
    assertResult(-16777216) { alpha1 }
  }

  test("byte2RbgInt(1)") {
    assertResult(65793) { byte2RbgInt(1) }
  }

}