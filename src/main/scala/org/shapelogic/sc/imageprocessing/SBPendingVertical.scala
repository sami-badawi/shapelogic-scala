package org.shapelogic.sc.imageprocessing

/**
 * Ported from ShapeLogic Java
 *
 * New semantic in ShapeLogic Scala is that:
 * SBPendingVertical is an vertical line where pixels can be added to current if
 * 1: They are similar
 * 2: Not used
 *
 * The new lines will only search in the same direction as the original
 * Unless they are in an expansion area
 */
case class SBPendingVertical(xMin: Int, xMax: Int, y: Int, searchUp: Boolean) {

  def this(xMinIn: Int, yIn: Int) {
    this(xMinIn, xMinIn, yIn, false)
  }

  def opposite(): SBPendingVertical = {
    this.copy(searchUp = !searchUp)
  }

  /**
   * This is a little tricky since sometimes y axis points up and sometime it points down
   * So up here just means raising values
   */
  def nextY: Int = if (searchUp) y + 1 else y - 1
}

object SBPendingVertical {
  def opposite(inLine: SBPendingVertical): SBPendingVertical = {
    if (inLine == null)
      null
    else
      inLine.opposite()
  }
}