package org.shapelogic.sc.polygon

import java.awt.Point
import java.awt.geom.Point2D
import java.util.Arrays

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

/**
 *
 * @author Sami Badawi
 *
 */
class VectorSpec extends FunSuite with BeforeAndAfterEach {

  test("testEquals") {
    val da1 = Array(1.0, 2.0)
    val da1b = Array(1.0, 2.0)
    assert(Arrays.equals(da1, da1b))
  }

  test("testSort") {
    val expected = Array(2.0, 3.0)
    val unsorted = Array(3.0, 2.0)
    Arrays.sort(unsorted)
    assert(Arrays.equals(expected, unsorted))
  }

  test("testHashcode") {
    val da1 = Array(1.0, 2.0)
    val da1b = Array(1.0, 2.0)
    assert(da1 ne da1b) //was test for not identical
    assert(da1.hashCode() != da1b.hashCode())
  }

  test("testCompare") {
    val p1 = new Point(2, 3)
    val p2 = new Point(2, 3)
    assertResult(p1) { p2 }
    assertResult(p1.hashCode()) { p2.hashCode() }
    val pD1 = new Point2D.Double(2.0, 3.0)
    assertResult(p1) { pD1 }
    assertResult(pD1) { p1 }
  }

  test("testComparable") {
    val p1 = new CPointInt(2, 3)
    val p2 = new CPointInt(2, 3)
    assertResult(p1) { p2 }
    assertResult(p1.hashCode()) { p2.hashCode() }
    assertResult(0) { p1.compareTo(p2) }
    assert(p1.minus(p2).isNull())
    assert(p2.multiply(0).isNull())
  }

}
