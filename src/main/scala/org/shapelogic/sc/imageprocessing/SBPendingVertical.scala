package org.shapelogic.sc.imageprocessing

/**
 * Ported from ShapeLogic Java
 */
case class SBPendingVertical(xMin: Int, xMax: Int, y: Int, searchUp: Boolean) {

  def this(xMinIn: Int, yIn: Int) {
    this(xMinIn, xMinIn, yIn, false)
  }

  def opposite(): SBPendingVertical = {
    this.copy(searchUp = !searchUp)
  }
}

object SBPendingVertical {
  def opposite(inLine: SBPendingVertical): SBPendingVertical = {
    if (inLine == null)
      null
    else
      inLine.opposite()
  }
}