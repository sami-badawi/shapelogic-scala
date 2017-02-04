package org.shapelogic.sc.polygon

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class CPointDoubleSpec extends FunSuite with BeforeAndAfterEach {
  val PI = scala.math.Pi

  test("testCreationVector") {
    val v0 = new CPointDouble();
    assertResult(v0.getX()) { 0.0 }
    val v1 = new CPointDouble(1.0f, 2.0f);
    assert(v1.getY() == 2);
  }

  test("testAddVector") {
    val e1 = new CPointDouble(3.0f, 4.0f);
    val v1 = new CPointDouble(1.0f, 2.0f);
    val v2 = new CPointDouble(2.0f, 2.0f);
    val sumV = v1.copy().add(v2);
    System.out.println("Sum: " + sumV);
    assertResult(e1) { sumV }
  }

  test("testCompareVector") {
    val v1 = new CPointDouble(1.0f, 2.0f);
    val v1b = new CPointDouble(1.0f, 2.0f);
    val v3 = new CPointDouble(2.0f, 1.0f);
    assertResult(v1b) { v1 }
    assert(v1b.compareTo(v1) >= 0);
    assert(v1b.compareTo(v1) <= 0);
    assert(v3.compareTo(v1) >= 0);
    assert(v3.compareTo(v1) > 0);
    assert(v1.compareTo(v3) < 0);
    assert(v1.compareTo(v3) <= 0);
  }

  test("testRoundVector") {
    val v1 = new CPointDouble(1.1f, 2.5f);
    val v1b = new CPointDouble(1.0f, 3.0f);
    assertResult(v1b) { v1.round() }
  }

  test("testAngle") {
    val pA0 = new CPointInt(1, 0);
    val pA45 = new CPointInt(1, 1);
    val pA90 = new CPointInt(0, 1);
    val pA135 = new CPointInt(-1, 1);
    val pA180 = new CPointInt(-1, 0);
    val pA225 = new CPointInt(-1, -1);
    val pA270 = new CPointInt(0, -1);
    val pA315 = new CPointInt(1, -1);
    assertResult(0.0) { pA0.angle() };
    assertResult(PI * 0.25) { pA45.angle() }
    assertResult(PI * 0.5) { pA90.angle() }
    assertResult(PI * 0.75) { pA135.angle() }
    assertResult(PI) { pA180.angle() }
    assertResult(PI * -0.75) { pA225.angle() }
    assertResult(PI * -0.5) { pA270.angle() }
    assertResult(PI * -0.25) { pA315.angle() }
  }

}