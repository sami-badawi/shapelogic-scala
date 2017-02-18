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
trait ValueAreaFactory[T] {
  def makePixelArea(x: Int, y: Int, startColor: Array[T]): IColorAndVariance[T]
  def getStore(): Seq[IColorAndVariance[T]]
  def getBackgroundColor(): Int
  def sort(): Unit
  def areasGreaterThan(minSize: Int): Int
}
