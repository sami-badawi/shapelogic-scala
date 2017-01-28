package org.shapelogic.sc.polygon

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class CLineSpec extends FunSuite with BeforeAndAfterEach {

  val p1: IPoint2D = new CPointDouble(1.0, 1.0);
  val p2: IPoint2D = new CPointDouble(2.0, 1.0);
  val p3: IPoint2D = new CPointDouble(1.0, 2.0);

  val rV: IPoint2D = new CPointDouble(0.0, 1.0);

  val lineVertical = new CLine(p1, p3)
  val lineHorizontal = new CLine(p1, p2)

  test("testRelativePoint") {
    assertResult(rV) { lineVertical.relativePoint() }
    assert(lineVertical.isVertical());
    assert(lineHorizontal.isHorizontal());
    assertResult(0.0) { lineHorizontal.angle() }
    assertResult(Math.PI / 2) { lineVertical.angle() }
  }

}