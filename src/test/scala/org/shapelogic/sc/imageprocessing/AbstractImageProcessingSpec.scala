package org.shapelogic.sc.imageprocessing

import org.scalatest._

import spire.algebra._
import spire.std._
import spire.implicits._
import org.shapelogic.sc.polygon.Polygon
import org.shapelogic.sc.polygon.IPoint2D
import org.shapelogic.sc.polygon.CLine

/**
 * A few helper print functions.
 * This should maybe just go into overridden toString living on the individual
 * classes
 */
object AbstractImageProcessingSpec {

  var _doPrint = true

  def printLines(polygon: Polygon): Unit = {
    if (!_doPrint)
      return
    println("Print lines:")
    polygon.getLines().foreach { (line: CLine) =>
      println(line)
    }
  }

  def printPoints(polygon: Polygon): Unit = {
    if (!_doPrint)
      return
    println("Print points:")
    polygon.getPoints().foreach { (point: IPoint2D) =>
      println(point)
    }
  }

  def printAnnotaions(polygon: Polygon): Unit = {
    if (!_doPrint)
      return
    println("Print annotations:")
    val map = polygon.getAnnotatedShape().getMap() //Map<Object, Set<GeometricShape2D>>
    map.foreach { entry =>
      println(entry._1 + ":\n" + entry._2)
    }
  }

  def printPolygon(polygon: Polygon): Unit = {
    if (!_doPrint)
      return
    println("Print polygon:")
    printLines(polygon)
    printPoints(polygon)
  }
}