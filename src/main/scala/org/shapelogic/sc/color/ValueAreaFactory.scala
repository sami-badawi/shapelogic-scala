package org.shapelogic.sc.color

/**
 *  Ported from ShapeLogic Java
 *  XXX Need changes
 *
 * Factory and storage interface. <br />
 *
 * @author Sami Badawi
 *
 */
trait ValueAreaFactory {
  def makePixelArea(x: Int, y: Int, startColor: Array[Byte]): IColorAndVariance
  def getStore(): Seq[IColorAndVariance]
  def getBackgroundColor(): Int
  def sort(): Unit
  def areasGreaterThan(minSize: Int): Int
}
