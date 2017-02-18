package org.shapelogic.sc.polygon

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

import collection.JavaConverters._

/**
 *
 * @author Sami Badawi
 *
 */
class PolygonEndPointAdjusterSpec extends FunSuite with BeforeAndAfterEach {

  ignore("testLBracketWithMissingCornerPoint") {
    val topPoint = new CPointInt(1, 1)
    val bottomPoint1 = new CPointInt(1, 27)
    val bottomPoint2 = new CPointInt(2, 28)
    val bottomPoint3 = new CPointInt(28, 28)

    val multiLinePolygon = new MultiLinePolygon(null)
    multiLinePolygon.startMultiLine()
    multiLinePolygon.addAfterEnd(topPoint)
    multiLinePolygon.addAfterEnd(bottomPoint1)
    multiLinePolygon.addAfterEnd(bottomPoint2)
    multiLinePolygon.addAfterEnd(bottomPoint3)
    multiLinePolygon.endMultiLine()
    multiLinePolygon.getValue() //Causes a call to calc
    assertResult(4) { multiLinePolygon.getPoints().size }
    assertResult(3) { multiLinePolygon.getLines().size }
    val clusterAdjuster = new PolygonEndPointAdjuster(multiLinePolygon)
    val clusteredPolygon = clusterAdjuster.getValue().asInstanceOf[MultiLinePolygon]
    assert(multiLinePolygon ne clusteredPolygon)
    assertResult(3) { clusteredPolygon.getPoints().size }
    assertResult(2) { clusteredPolygon.getLines().size }
  }
}
