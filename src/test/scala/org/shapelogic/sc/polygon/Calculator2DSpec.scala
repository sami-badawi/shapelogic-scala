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

  //	public void testCrossProduct() {
  //		assertResult(1.,crossProduct(xAxis1, yAxis1));
  //		assertResult(0.,crossProduct(xAxis1, xAxis1));
  //	}
  //	
  //	/** This is signed */
  //	public void testDistanceOfPointToLine() {
  //		assertResult(0.,distanceOfPointToLine(xAxis1, xAxis1Line));
  //		assertResult(1.,distanceOfPointToLine(yAxis1, xAxis1Line));
  //	}
  //	
  //	public void testScaleLineFromStartPoint() {
  //		assertResult(pointToLine(xAxis2),scaleLineFromStartPoint(xAxis1Line, 2.));
  //	}
  //	
  //	public void testPointToLine() {
  //		assertResult(xAxis1Line,pointToLine(xAxis1));
  //	}
  //	
  //	public void testProjectionOfPointOnLine() {
  //		assertResult(origin,projectionOfPointOnLine(yAxis1, xAxis1Line));
  //	}
  //	
  //	public void testInverseLine() {
  //		assertResult(xAxis1Line, inverseLine(new CLine(xAxis1,origin)));
  //	}
  //
  //	public void testAddLines() {
  //		CLine addedLines = addLines(xAxis1Line, pointToLine(yAxis1));
  //		assertResult(pointToLine(diagonal1),addedLines);
  //	}
  //	
  //	public void testIntersectionOfLines(){
  //		assertResult(origin,intersectionOfLines(xAxis1Line, new CLine(yAxis1,origin)));
  //		assertResult(xAxis1,intersectionOfLines(xAxis1Line, new CLine(diagonal1,xAxis1)));
  //		
  //		CLine activeLine = new CLine(new CPointInt(3,2), new CPointInt(2,1));
  //		CPointInt expectedPoint = new CPointInt(1,0);
  //		assertResult(expectedPoint,intersectionOfLines(xAxis1Line, activeLine));
  //	}
  //
  //	public void testIntersectionOfLinesDouble(){
  //		
  //		CLine activeLine = new CLine(new CPointDouble(3,2), new CPointDouble(2,1));
  //		CPointDouble expectedPoint = new CPointDouble(1,0);
  //		assertResult(expectedPoint,intersectionOfLines(xAxis1Line, activeLine));
  //	}
  //
  //	public void testToCPointDouble() {
  //		assertResult(xAxis1Double,toCPointDouble(xAxis1));
  //	}
  //	
  //	public void testToCPointInt() {
  //		assertResult(xAxis1,toCPointInt(xAxis1Double));
  //	}
  //	
  //	public void testToCPointIntIfPossible() {
  //		IPoint2D badIntPoint = new CPointDouble(0.0, 0.8); 
  //		assertTrue(toCPointIntIfPossible(xAxis1) instanceof CPointInt);
  //		assertTrue(toCPointIntIfPossible(xAxis1Double) instanceof CPointInt);
  //		assertFalse(toCPointIntIfPossible(badIntPoint) instanceof CPointInt);
  //	}
  //	
  //	public void testLinesParallel(CLine line1, CLine line2) {
  //		assertTrue(linesParallel(xAxis1Line, pointToLine(xAxis1)));
  //	}
  //
  //	public void testIntersectionOfLinesInt(){
  //		CPointInt topPoint = new CPointInt(1,1); 
  //		CPointInt bottomPoint1 = new CPointInt(1,27); 
  //		CPointInt bottomPoint2 = new CPointInt(2,28); 
  //		CPointInt bottomPoint3 = new CPointInt(28,28);
  //		CLine activeLine = new CLine(topPoint, bottomPoint1);
  //		CLine projectionLine = new CLine(bottomPoint2, bottomPoint3);
  //		CPointDouble expectedPoint = new CPointDouble(1,28);
  //		assertResult(expectedPoint,intersectionOfLines(activeLine, projectionLine));
  //	}
  //	public void testDirectionBetweenNeighborPoints() {
  //		assertResult(0, (int)directionBetweenNeighborPoints(origin, xAxis1));
  //		assertResult(1, (int)directionBetweenNeighborPoints(origin, diagonal1));
  //		assertResult(2, (int)directionBetweenNeighborPoints(origin, yAxis1)); 
  //		assertResult(3, (int)directionBetweenNeighborPoints(origin, new CPointInt(-1,1))); 
  //		assertResult(4, (int)directionBetweenNeighborPoints(origin, new CPointInt(-1,0))); 
  //		assertResult(5, (int)directionBetweenNeighborPoints(origin, new CPointInt(-1,-1))); 
  //		assertResult(6, (int)directionBetweenNeighborPoints(origin, new CPointInt(0,-1))); 
  //		assertResult(7, (int)directionBetweenNeighborPoints(origin, new CPointInt(1,-1))); 
  //	}
  //
  //  
}