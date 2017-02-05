package org.shapelogic.sc.polygon

import org.shapelogic.sc.calculation.CalcInvoke;

/**
 *  ShapeImprover takes a geometric object and make an improved version.
 * This does not have to be a new object, only if there are big changes
 *
 * Is there any reason why this need to be a shape?
 * No I think maybe I will stick with the name for now.
 *
 * So this is also going to be the base interface for the annotation interface
 *
 * @author Sami Badawi
 *
 */
trait Improver[S] extends CalcInvoke[S] {

  def getInput(): S
  def setInput(input: S): Unit
  def createdNewVersion(): Boolean
}
