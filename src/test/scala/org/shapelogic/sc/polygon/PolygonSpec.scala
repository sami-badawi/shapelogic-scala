package org.shapelogic.sc.polygon

import org.scalatest._
/**
 *
 * @author Sami Badawi
 *
 */
class PolygonSpec extends FunSuite with BeforeAndAfterEach {

  val p1: IPoint2D = new CPointDouble(3.0f, 4.0f);
  val p2: IPoint2D = new CPointDouble(1.0f, 2.0f);
  val eMin: IPoint2D = new CPointDouble(1.0f, 2.0f);
  val eMax: IPoint2D = new CPointDouble(3.0f, 4.0f);

  test("testPolygon") {
    val polygon = new Polygon();
    polygon.addLine(p1, p2)
    val b1: BBox = polygon.getBBox()
    assertResult(b1.minVal) { eMin }
    assertResult(b1.maxVal) { eMax }
    assertResult(2) { polygon.getPoints().size }
  }

  test("testPolygonClone") {
    val polygon = new Polygon()
    val polygonClone = polygon.clone().asInstanceOf[Polygon]
    assert(polygon ne polygonClone);
    assertResult(polygon.getClass()) { polygonClone.getClass() }
  }
}
