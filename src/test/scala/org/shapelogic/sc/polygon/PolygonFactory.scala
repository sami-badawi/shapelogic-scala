package org.shapelogic.sc.polygon

/**
 *
 * @author Sami Badawi
 *
 */
object PolygonFactory {

  def createSameType(poygon: Polygon): Polygon = {
    if (poygon.isInstanceOf[MultiLinePolygon]) {
      new MultiLinePolygon(null)
    } else if (poygon.isInstanceOf[Polygon]) {
      new Polygon(null)
    } else
      null
  }
}
