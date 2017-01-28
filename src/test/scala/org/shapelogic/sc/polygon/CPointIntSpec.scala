package org.shapelogic.sc.polygon

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.lang.Math._

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

import collection.JavaConverters._
import scala.collection.mutable.TreeSet
import scala.collection.mutable.HashSet

/**
 *
 * @author Sami Badawi
 *
 */
class CPointIntSpec extends FunSuite with BeforeAndAfterEach {

  test("testConstructorAndArithmetic()") {
    val p1: IPoint2D = new CPointInt(1, 2);
    val p2b = new Point(1, 2);
    val p2: IPoint2D = new CPointInt(p2b);
    assertResult(p1) { p2 }
    assertResult(p1.hashCode()) { p2.hashCode() }
    assert(p1.minus(p2).isNull())
    assert(p2.multiply(0).isNull());
  }

  test("testInteractionWithOtherClasses()") {
    val p1: IPoint2D = new CPointInt(1, 2);
    val p2b = new Point(1, 2);
    val p2: IPoint2D = new CPointDouble(p2b);
    assertResult(p1) { p2 }
    assertResult(p1.hashCode()) { p2.hashCode() }
    assert(p1.minus(p2).isNull());
    assert(p2.multiply(0).isNull());
  }

  test("testSort()") {
    val p1 = new CPointInt(3, 2);
    val p2b = new Point(1, 2);
    val p2 = new CPointInt(p2b);
    val list = new ArrayList[CPointInt]();
    list.add(p1);
    list.add(p2);
    assertResult(p1) { list.get(0) }
    assertResult(p2) { list.get(1) }
    Collections.sort(list);
    assertResult(p1) { list.get(1) };
    assertResult(p2) { list.get(0) }
  }

  test("testTreeSet()") {
    val set = new HashSet[CPointInt]();
    val p0 = new CPointInt(1, 3);
    val p1 = new CPointInt(1, 26);
    val p2 = new CPointInt(2, 2);
    val pBad2 = new CPointInt(2, 2);
    val p3 = new CPointInt(1, 27);
    val p4 = new CPointInt(2, 28);
    val p5 = new CPointInt(26, 28);
    val p6 = new CPointInt(27, 27);
    set.add(p4);
    set.add(p0);
    assertResult(2) { set.size }
    set.add(p2);
    set.add(p3);
    assertResult(4) { set.size }
    set.add(pBad2);
    set.add(p6);
    set.add(p5);
    assertResult(6) { set.size }
    set.add(p1);
    assertResult(7) { set.size }
    set.add(p1);
    set.add(pBad2);
    set.add(p3);
    assertResult(7) { set.size }
    //Check that the compareTo is asymmetrical
    assertResult(1) { p5.compareTo(p2) }
    assertResult(-1) { p2.compareTo(p5) }
    val seq = set.toSeq
    set.foreach { (point: IPoint2D) =>
      set.foreach { (point2: IPoint2D) =>
        assertResult(0) { point.compareTo(point2) + point2.compareTo(point) }
      }
    }
  }

  test("testAngle()") {
    val pA0 = new CPointInt(1, 0);
    val pA45 = new CPointInt(1, 1);
    val pA90 = new CPointInt(0, 1);
    val pA135 = new CPointInt(-1, 1);
    val pA180 = new CPointInt(-1, 0);
    val pA225 = new CPointInt(-1, -1);
    val pA270 = new CPointInt(0, -1);
    val pA315 = new CPointInt(1, -1);
    assertResult(0.0) { pA0.angle() }
    assertResult(PI * 0.25) { pA45.angle() }
    assertResult(PI * 0.5) { pA90.angle() }
    assertResult(PI * 0.75) { pA135.angle() }
    assertResult(PI) { pA180.angle() }
    assertResult(-PI * 0.75) { pA225.angle() }
    assertResult(-PI * 0.5) { pA270.angle() }
    assertResult(-PI * 0.25) { pA315.angle() }
  }

}