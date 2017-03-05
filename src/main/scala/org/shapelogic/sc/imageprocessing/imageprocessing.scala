package org.shapelogic.sc

/**
 * Image processing operation many ported from ShapeLogic Java
 */
package object imageprocessing {
  case class PaintAndCheckLines(paintLines: Seq[SBPendingVertical], checkLines: Seq[SBPendingVertical])
}