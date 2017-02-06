package org.shapelogic.sc.util

/**
 * Maybe this should just be super abstract
 *
 *  What does it need?
 *  A name or an OH name
 *  A group or parent
 *
 * @author Sami Badawi
 *
 */
trait OHInterface {
  def getOhName(): String
}
