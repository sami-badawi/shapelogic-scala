package org.shapelogic.sc.polygon

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class BBoxSpec extends FunSuite with BeforeAndAfterEach {
  //  import BBox._
  //
  val north: IPoint2D = new CPointDouble(110.0, 100.0)
  val south: IPoint2D = new CPointDouble(110.0, 120.0)
  val east: IPoint2D = new CPointDouble(120.0, 110.0)
  val west: IPoint2D = new CPointDouble(100.0, 110.0)
  val center: IPoint2D = new CPointDouble(110.0, 110.0)
  val eMin: IPoint2D = new CPointDouble(100.0, 100.0)
  val eMax: IPoint2D = new CPointDouble(120.0, 120.0)

  test("Check testPolygon") {
    val poly1 = new Polygon();
    //    "point size should be 0",
    assertResult(0) { poly1.getPoints().size }
    poly1.addLine(north, south);
    //    "point size should be 2", 
    assertResult(2) { poly1.getPoints().size }
    poly1.addLine(east, west)
    //    "Fist point should be west ", 
    assertResult(poly1.getPoints().iterator.next) { north }
    poly1.invoke();
    val bbox: BBox = poly1.getBBox();
    //    "minVal wrong", 
    assertResult(bbox.minVal) { eMin }
    //    "maxVal wrong", 
    assertResult(bbox.maxVal) { eMax }
  }

  test("Check testDiagonal") {
    val poly1 = new Polygon()
    poly1.addLine(north, south)
    poly1.addLine(east, west)
    poly1.invoke();
    val bbox: BBox = poly1.getBBox()
    //"center wrong",
    assertResult(eMin) { bbox.getDiagonalVector(0.0) }

    //"center wrong", 
    assertResult(center) { bbox.getDiagonalVector(0.5) }

    //"center wrong",
    assertResult(eMax) { bbox.getDiagonalVector(1.0) }
  }
}