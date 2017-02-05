package org.shapelogic.sc.polygon

import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet

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
class PolygonEndPointAdjuster(inputPolygon: Polygon) extends Improver[Polygon] {

  var _inputPolygon: Polygon = inputPolygon
  var _value: Polygon = null
  var _dirty: Boolean = true
  var _endPointsMultiClusters: ArrayBuffer[Set[IPoint2D]] = null
  var _clustersToPointMapping: Map[Set[IPoint2D], IPoint2D] = null
  var _clusterPointToCommonPointMapping: Map[IPoint2D, IPoint2D] = null
  var _createdNewVersion: Boolean = false; //XXX not set yet 

  override def getInput(): Polygon = {
    return _inputPolygon;
  }

  override def isDirty(): Boolean = {
    return _dirty;
  }

  override def setup(): Unit = {
    _clustersToPointMapping = new HashMap[Set[IPoint2D], IPoint2D]();
    _clusterPointToCommonPointMapping = new HashMap[IPoint2D, IPoint2D]();
  }

  override def invoke(): Polygon = {
    setup();
    val clusters = _inputPolygon.getEndPointsMultiClusters() //List<Set<IPoint2D>>
    if (clusters.size == 0) {
      _value = _inputPolygon;
    } else {
      clusters.foreach { (cluster: Set[IPoint2D]) =>
        testCluster(cluster);
      }
      _value = _inputPolygon.replacePointsInMap(_clusterPointToCommonPointMapping, _inputPolygon.getAnnotatedShape());
      _value.setVersion(_inputPolygon.getVersion() + 1);
    }
    _dirty = false;
    return _value;
  }

  override def getValue(): Polygon = {
    if (_dirty)
      invoke();
    return _value;
  }

  /**
   * If all the points in a cluster can be combined to a single point
   * @return the point if it is possible otherwise null
   *
   * This class has very strict criteria for joining points.
   * Only join if all lines are either very small or straight,
   * and the new point keep all the straight lines straight.
   * This might be relaxed in subclasses, by overriding this method
   */
  def testCluster(cluster: Set[IPoint2D]): IPoint2D = {
    var commonPoint: IPoint2D = null;
    val shortLinesTouchingCluster = new HashSet[CLine]()
    val longLinesTouchingCluster = new HashSet[CLine]();
    val linesTouchingCluster = new HashSet[CLine]();
    cluster.foreach { (clusterPoint: IPoint2D) =>
      linesTouchingCluster.++=(_inputPolygon.getLinesForPoint(clusterPoint));
      linesTouchingCluster.foreach { (line: CLine) =>
        if (line.distance() < 2)
          shortLinesTouchingCluster.add(line);
        else
          longLinesTouchingCluster.add(line);
      }
    }
    val adjustmentCandidates = new HashSet[IPoint2D]();
    val longLinesIterator: Iterator[CLine] = longLinesTouchingCluster.iterator
    val fistLongLine: CLine = longLinesIterator.next();
    if (fistLongLine == null)
      return null;
    while (longLinesIterator.hasNext) {
      var longLine: CLine = longLinesIterator.next();
      commonPoint = Calculator2D.intersectionOfLines(fistLongLine, longLine);
      if (commonPoint != null)
        adjustmentCandidates.add(commonPoint);
    }
    if (adjustmentCandidates.size == 1) {
      commonPoint = adjustmentCandidates.head
      var stop = false
      longLinesTouchingCluster.foreach { (line: CLine) =>
        if (!stop && !Calculator2D.pointIsOnLine(commonPoint, line)) {
          commonPoint = null;
          stop = true
        }
      }
    }
    if (commonPoint != null) {
      _clustersToPointMapping.put(cluster, commonPoint);
      cluster.foreach { (point: IPoint2D) =>
        _clusterPointToCommonPointMapping.put(point, commonPoint);
      }
    }
    return commonPoint;
  }

  def adjustmentPointOkForLine(line: CLine, newPoint: IPoint2D): Boolean = {
    return false;
  }

  def getEndPointsMultiClusters(): ArrayBuffer[Set[IPoint2D]] = //
    {
      return _endPointsMultiClusters;
    }

  override def createdNewVersion(): Boolean = {
    return _createdNewVersion;
  }

  override def setInput(input: Polygon): Unit = {
    _inputPolygon = input;
  }

}

object PolygonEndPointAdjuster {
  def extendLine(line: CLine, clusterPoint: IPoint2D): IPoint2D = {
    var extendedPoint: IPoint2D = null;
    if (startPointIsClosest(line, clusterPoint)) {
      extendedPoint = line.getStart();
    } else {
      extendedPoint = line.getEnd();
    }
    return extendedPoint;
  }

  def startPointIsClosest(line: CLine, clusterPoint: IPoint2D): Boolean = {
    return line.getStart().distance(clusterPoint) < line.getEnd().distance(clusterPoint)
  }
}
