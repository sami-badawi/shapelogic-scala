package org.shapelogic.sc.polygon

import spire.implicits._
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Set
import scala.collection.mutable.HashMap

object FilterPolygonForSmallLines {
  val SMALL_LINE_LIMIT: Double = 0.10

}

/**
 * Take a polygon as input and a list of sets of point that potentially can be
 * combined. Create a new polygon.
 *
 * I think that if there have been no changes then just return the original polygon.
 * I think that polygons are supposed to be immutable.
 *
 * @author Sami Badawi
 *
 */
class FilterPolygonForSmallLines(inputPolygon: Polygon) extends Improver[Polygon] {
  import FilterPolygonForSmallLines._

  var _inputPolygon: Polygon = inputPolygon
  var _value: Polygon = null
  var _dirty: Boolean = true
  val _endPointsMultiClusters: ArrayBuffer[Set[IPoint2D]] = new ArrayBuffer()
  val _clustersToPointMapping: Map[Set[IPoint2D], IPoint2D] = new HashMap() // Map<Set<IPoint2D>,IPoint2D>
  val _clusterPointToCommonPointMapping: Map[IPoint2D, IPoint2D] = new HashMap()
  var _createdNewVersion: Boolean = false //XXX not set yet
  var _smallLineCutOffLength: Double = 1.0
  var _smallLinesFiltered: Int = 0

  override def getInput(): Polygon = {
    return _inputPolygon
  }

  override def isDirty(): Boolean = {
    return _dirty
  }

  override def setup(): Unit = {
    _dirty = true
    //		_endPointsMultiClusters
    //		_clustersToPointMapping = new HashMap<Set<IPoint2D>,IPoint2D>()
    //		_clusterPointToCommonPointMapping = new TreeMap<IPoint2D,IPoint2D>()
    _createdNewVersion = false
    _smallLineCutOffLength = SMALL_LINE_LIMIT * _inputPolygon.getDiameter()
    _value = PolygonFactory.createSameType(_inputPolygon)
    _smallLinesFiltered = 0
  }

  override def invoke(): Polygon = {
    setup()
    _inputPolygon.getLines().foreach { (lines: CLine) =>
      handleLine(lines)
    }
    val multiLines = _inputPolygon.getMultiLines() //List < MultiLine >
    if (multiLines != null)
      multiLines.foreach { (lines: MultiLine) =>
        handleMulitLine(lines)
      }
    if (_smallLinesFiltered == 0)
      _value = _inputPolygon
    _value.getValue()
    _dirty = false
    return _value
  }

  override def getValue(): Polygon = {
    if (_dirty)
      invoke()
    return _value
  }

  /**
   * I think that this need to be done differently for different types of polygons
   * For Polygon: Just go through lines and filter
   * For MultiLinePolygon: Run through both multi lines and independent lines
   *
   * How do I get this unified?
   * Change so they have the same interface so now Polygon also return independent lines.
   *
   * This can be optimized
   */
  def handleLine(line: CLine): Unit = {
    var filter: Boolean = false
    if (line.distance() < _smallLineCutOffLength) {
      var pointsCountMap: Map[IPoint2D, Integer] = _inputPolygon.getPointsCountMap()
      if (pointsCountMap.get(line.getStart()) == 1 || pointsCountMap.get(line.getEnd()) == 1) {
        filter = true
      }
    }
    if (filter) {
      _smallLinesFiltered += 1
    } else {
      _value.addIndependentLine(line)
    }
  }

  def handleMulitLine(multiLine: MultiLine): Unit = {
    if (multiLine == null)
      return
    val sizeOfMulitLine: Int = multiLine.getPoints().size
    if (sizeOfMulitLine < 2) {
      _smallLinesFiltered += 1
      return
    }
    if (sizeOfMulitLine == 2 && multiLine.getDiameter() < _smallLineCutOffLength) {
      _smallLinesFiltered += 1
      return
    }
    //XXX this needs to be expanded to handling the start and end point
    var removeFirst: Boolean = false
    if (multiLine.getStart().distance(multiLine.getPoints()(1)) <
      _smallLineCutOffLength) {
      removeFirst = true
    }
    var removeLast: Boolean = false
    var lastNumber: Int = multiLine.getPoints().size - 1
    var secondLastPoint: IPoint2D = multiLine.getPoints()(lastNumber - 1)
    if (multiLine.getEnd().distance(secondLastPoint) < _smallLineCutOffLength) {
      removeLast = true
    }
    if (removeFirst || removeLast) {
      var result: MultiLine = new MultiLine(_value.getAnnotatedShape())
      cfor(0)(_ < multiLine.getPoints().size, _ + 1) { i =>
        if (i == 0) {
          if (!removeFirst)
            result.addAfterEnd(multiLine.getPoints()(i))
        } else if (i == lastNumber) {
          if (!removeLast)
            result.addAfterEnd(multiLine.getPoints()(i))
        } else
          result.addAfterEnd(multiLine.getPoints()(i))
      }
      _value.addMultiLine(result)
      return
    }
    _value.addMultiLine(multiLine)
  }

  override def createdNewVersion(): Boolean = {
    _createdNewVersion
  }

  override def setInput(input: Polygon): Unit = {
    _inputPolygon = input
  }

}
