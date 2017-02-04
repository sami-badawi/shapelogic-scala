package org.shapelogic.sc.polygon

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class BBoxSpec extends FunSuite with BeforeAndAfterEach {
//  import BBox._
//
//    val north: IPoint2D = new CPointDouble(110.,100.);
//    val south: IPoint2D = new CPointDouble(110.,120.);
//    val east: IPoint2D = new CPointDouble(120.,110.);
//    val west: IPoint2D = new CPointDouble(100.,110.);
//    val center: IPoint2D = new CPointDouble(110.,110.);
//    val eMin: IPoint2D = new CPointDouble(100.,100.);
//    val eMax: IPoint2D = new CPointDouble(120.,120.);
//    
//  test("Check testPolygon") {
//    	val poly1 = new Polygon();
//        assertEquals("point size should be 0", 0, poly1.getPoints().size());
//        poly1.addLine(north,south);
//        assertEquals("point size should be 2", 2, poly1.getPoints().size());
//        poly1.addLine(east,west);
//        assertEquals("Fist point should be west ", poly1.getPoints().iterator().next(), west);
//        poly1.invoke();
//        val bbox: BBox = poly1.getBBox();
//        assertEquals("minVal wrong", bbox.minVal, eMin);
//        assertEquals("maxVal wrong", bbox.maxVal, eMax);
//    }
//    
//  test("Check testDiagonal") {
//    	val poly1 = new Polygon();
//        poly1.addLine(north,south);
//        poly1.addLine(east,west);
//        poly1.invoke();
//        val bbox: BBox = poly1.getBBox();
//        assertEquals("center wrong", eMin, bbox.getDiagonalVector(0.0));
//        assertEquals("center wrong", center, bbox.getDiagonalVector(0.5));
//        assertEquals("center wrong", eMax, bbox.getDiagonalVector(1.));
//    }  
}