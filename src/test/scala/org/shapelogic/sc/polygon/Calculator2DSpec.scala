package org.shapelogic.sc.polygon

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class Calculator2DSpec extends FunSuite with BeforeAndAfterEach {
  import Calculator2D._

  val origin = new CPointInt(0, 0);
  val xAxis1 = new CPointInt(1, 0);
  val xAxis2 = new CPointInt(2, 0);
  val yAxis1 = new CPointInt(0, 1);
  val diagonal1 = new CPointInt(1, 1);
  val xAxis1Line = new CLine(origin, xAxis1);
  val xAxis1Double = new CPointDouble(1, 0);

  test(" void testHatPoint()") {
    assertResult(yAxis1) { hatPoint(xAxis1) }
  }

  test("testDotProduct") {
    assertResult(0.0) { dotProduct(xAxis1, yAxis1) }
    assertResult(1.0) { dotProduct(xAxis1, xAxis1) }
  }

  test("testCrossProduct(") {
    assertResult(1.0) { crossProduct(xAxis1, yAxis1) }
    assertResult(0.0) { crossProduct(xAxis1, xAxis1) }
  }

  /** This is signed */
  test("testDistanceOfPointToLine(") {
    assertResult(0.0) { distanceOfPointToLine(xAxis1, xAxis1Line) }
    assertResult(1.0) { distanceOfPointToLine(yAxis1, xAxis1Line) }
  }

  test("testScaleLineFromStartPoint(") {
    assertResult(pointToLine(xAxis2)) { scaleLineFromStartPoint(xAxis1Line, 2.0) }
  }

  test("testPointToLine(") {
    assertResult(xAxis1Line) { pointToLine(xAxis1) }
  }

  test("testProjectionOfPointOnLine(") {
    assertResult(origin) { projectionOfPointOnLine(yAxis1, xAxis1Line) }
  }

  test("testInverseLine(") {
    assertResult(xAxis1Line) { inverseLine(new CLine(xAxis1, origin)) }
  }

  test("testAddLines(") {
    val addedLines = addLines(xAxis1Line, pointToLine(yAxis1));
    assertResult(pointToLine(diagonal1)) { addedLines }
  }

  //XXX linear algebra is not enabled put back in
  ignore("testIntersectionOfLines") {
    assertResult(origin) { intersectionOfLines(xAxis1Line, new CLine(yAxis1, origin)) }
    assertResult(xAxis1) { intersectionOfLines(xAxis1Line, new CLine(diagonal1, xAxis1)) }

    val activeLine = new CLine(new CPointInt(3, 2), new CPointInt(2, 1));
    val expectedPoint = new CPointInt(1, 0);
    assertResult(expectedPoint) { intersectionOfLines(xAxis1Line, activeLine) }
  }

  test("testIntersectionOfLinesDouble") {

    val activeLine = new CLine(new CPointDouble(3, 2), new CPointDouble(2, 1));
    val expectedPoint = new CPointDouble(1, 0);
    assertResult(expectedPoint) { intersectionOfLines(xAxis1Line, activeLine) }
  }

  test("testToCPointDouble") {
    assertResult(xAxis1Double) { toCPointDouble(xAxis1) }
  }

  test("testToCPointInt(") {
    assertResult(xAxis1) { toCPointInt(xAxis1Double) }
  }

  test("testToCPointIntIfPossible(") {
    val badIntPoint = new CPointDouble(0.0, 0.8);
    assert(toCPointIntIfPossible(xAxis1).isInstanceOf[CPointInt])
    assert(toCPointIntIfPossible(xAxis1Double).isInstanceOf[CPointInt])
    assertResult(false) { toCPointIntIfPossible(badIntPoint).isInstanceOf[CPointInt] }
  }

  test("testLinesParallel(CLine line1, CLine line2") {
    assert(linesParallel(xAxis1Line, pointToLine(xAxis1)))
  }

  test("testIntersectionOfLinesInt") {
    val topPoint = new CPointInt(1, 1)
    val bottomPoint1 = new CPointInt(1, 27);
    val bottomPoint2 = new CPointInt(2, 28);
    val bottomPoint3 = new CPointInt(28, 28);
    val activeLine = new CLine(topPoint, bottomPoint1);
    val projectionLine = new CLine(bottomPoint2, bottomPoint3);
    val expectedPoint = new CPointDouble(1, 28);
    assertResult(expectedPoint) { intersectionOfLines(activeLine, projectionLine) }
  }

  test("testDirectionBetweenNeighborPoints(") {
    assertResult(0) { directionBetweenNeighborPoints(origin, xAxis1) }
    assertResult(1) { directionBetweenNeighborPoints(origin, diagonal1) }
    assertResult(2) { directionBetweenNeighborPoints(origin, yAxis1) }
    assertResult(3) { directionBetweenNeighborPoints(origin, new CPointInt(-1, 1)) }
    assertResult(4) { directionBetweenNeighborPoints(origin, new CPointInt(-1, 0)) }
    assertResult(5) { directionBetweenNeighborPoints(origin, new CPointInt(-1, -1)) }
    assertResult(6) { directionBetweenNeighborPoints(origin, new CPointInt(0, -1)) }
    assertResult(7) { directionBetweenNeighborPoints(origin, new CPointInt(1, -1)) }
  }

}