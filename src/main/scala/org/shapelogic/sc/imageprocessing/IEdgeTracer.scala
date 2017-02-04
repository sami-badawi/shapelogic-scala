package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.polygon.Polygon;

/**
 * Interface for edge tracers.
 *
 * @author Sami Badawi
 *
 */
trait IEdgeTracer {

  def autoOutline(startX: Int, startY: Int): Polygon

}
