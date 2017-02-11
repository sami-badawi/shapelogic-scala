package org.shapelogic.sc.polygon

import scala.collection.mutable.Set
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer
import org.shapelogic.sc.calculation.CalcInvoke

import spire.implicits._
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.collection.mutable.Set

object Polygon {
  val MAX_DISTANCE_BETWEEN_CLUSTER_POINTS: Double = 2

}
class Polygon(annotatedShape: AnnotatedShapeImplementation) extends BaseAnnotatedShape(annotatedShape)
    with IPolygon2D with CalcInvoke[Polygon] with Cloneable with PointReplacable[Polygon] {
  import Polygon._
  var _bBox = new BBox()
  var _lines: Set[CLine] = Set()
  var _points: Set[IPoint2D] = Set()
  protected var _dirty = true
  protected var _aspectRatio: Double = 0
  protected var _closed: Boolean = false
  protected var _endPointCount: Int = -1
  protected var _pointsCountMap = Map[IPoint2D, Integer]()
  protected var _pointsToLineMap: Map[IPoint2D, Set[CLine]] = null
  protected var _version: Int = 0
  var _currentMultiLine: MultiLine = new MultiLine(this.getAnnotatedShape())
  protected var _endPointsClusters = ArrayBuffer[Set[IPoint2D]]()
  //I could make this lazy

  var _polygonImprovers: ArrayBuffer[Improver[Polygon]] = new ArrayBuffer() //XXX takes more imports
  protected var _perimeter: Double = 0

  def this() {
    this(null)
  }

  /**
   * Was constructor
   */
  def init() {
    setup()
    internalFactory()
  }

  /** All the objects that needs special version should be created here. */
  protected def internalFactory() = {
    _polygonImprovers = new ArrayBuffer[Improver[Polygon]]()
    //    _polygonImprovers.add(new FilterPolygonForSmallLines())
    //    _polygonImprovers.add(new PolygonAnnotator())
  }

  override def getBBox(): BBox = {
    getValue()
    _bBox
  }

  override def getLines(): Set[CLine] = {
    _lines
  }

  override def getPoints(): Set[IPoint2D] = {
    _points
  }

  override def getAspectRatio(): Double = {
    getValue()
    if (_bBox.minVal != null) {
      val lenX: Double = _bBox.maxVal.getX() - _bBox.minVal.getX()
      val lenY: Double = _bBox.maxVal.getY() - _bBox.minVal.getY()
      if (lenX > 0)
        _aspectRatio = lenY / lenX
      else
        _aspectRatio = Double.PositiveInfinity
    }
    _aspectRatio
  }

  /** Does this make sense for a polygon or only for multi line */
  override def isClosed(): Boolean = {
    _closed
  }

  override def isDirty(): Boolean = {
    _dirty
  }

  override def setup(): Unit = {
  }

  def containsPoint(point: IPoint2D): Boolean = {
    _points.contains(point)
  }

  def containsLine(line: ILine2D): Boolean = {
    _lines.contains(line.asInstanceOf[CLine]) //XXX 
  }

  def addPoint(point: IPoint2D): Unit = {
    if (!containsPoint(point))
      _points.add(point)
  }

  /** this should not be used use addIndependentLine() instead */
  def addLine(point1: IPoint2D, point2: IPoint2D): CLine = {
    val line: CLine = CLine.makeUnordered(point1, point2)
    if (!containsLine(line))
      _lines.add(line)
    addPoint(point1)
    addPoint(point2)
    line
  }

  /**
   * this should not be used use addIndependentLine() instead
   *
   */
  @deprecated("Not sure what is bad about this", "2017-02-02")
  def addLine(line: CLine): CLine = {
    if (!containsLine(line)) {
      _lines.add(line)
      addPoint(line.getStart())
      addPoint(line.getEnd())
    }
    line
  }

  override def invoke(): Polygon = {
    _bBox = findBbox()
    findPointCount()
    _dirty = false
    this
  }

  def findBbox(): BBox = {
    if (_bBox == null)
      _bBox = new BBox()
    _points.foreach { (pointInPolygon: IPoint2D) =>
      _bBox.addPoint(pointInPolygon)
    }
    _bBox
  }

  /**
   * Return a cleaned up polygon
   *
   * @param onlyInt Change all coordinates to integers
   * @param procentage of diagonal of b box that should be considered as same point
   */
  def cleanUp(onlyInt: Boolean, procentage: Double): Polygon = {
    findBbox()
    val threshold: Double = _bBox.getDiameter() * procentage
    val roundedPoints = new ArrayBuffer[IPoint2D]()
    val pointMap = new HashMap[IPoint2D, IPoint2D]()
    _points.foreach { (point: IPoint2D) =>
      var roundedPoint: IPoint2D = point
      if (onlyInt) {
        roundedPoint = point.copy().round()
        if (roundedPoint.equals(point))
          roundedPoint = point //uses the same point, do not create new 
      }
      val roundPointIterator: Iterator[IPoint2D] = roundedPoints.iterator
      var moreRoundedPoints: Boolean = roundPointIterator.hasNext
      var foundPoint: IPoint2D = null
      while (moreRoundedPoints) {
        var point2: IPoint2D = roundPointIterator.next
        pointMap.put(point, point)
        if (roundedPoint.distance(point2) < threshold) {
          foundPoint = point2
          moreRoundedPoints = false
        } else
          moreRoundedPoints = roundPointIterator.hasNext
      }
      if (foundPoint == null) {
        roundedPoints.append(roundedPoint)
        pointMap.put(point, roundedPoint)
      } else {
        pointMap.put(point, foundPoint)
      }

    }
    replacePointsInMap(pointMap, null)
  }

  /** register a list of improvers and call them here */
  def improve(): Polygon = {
    if (_polygonImprovers == null)
      return this
    var result: Polygon = this
    _polygonImprovers.foreach { (improver: Improver[Polygon]) =>
      improver.setInput(result)
      result = improver.getValue()
    }
    result
  }

  override def getValue(): Polygon = {
    if (isDirty())
      invoke()
    this
  }

  def getVerticalLines() = { //List < CLine > 
    _lines.filter(_.isVertical)
  }

  def getHorizontalLines() = {
    _lines.filter(_.isHorizontal)
  }

  /** Find how many lines each point is part of by making a map */
  def getPointsCountMap(): Map[IPoint2D, Integer] = {
    if (_pointsCountMap == null) {
      _pointsCountMap = Map[IPoint2D, Integer]()
      _lines.foreach { (line: CLine) =>
        {
          var startCount: Integer = _pointsCountMap.getOrElse(line.getStart(), null)
          if (startCount == null)
            startCount = 1
          else
            startCount = startCount + 1
          _pointsCountMap.put(line.getStart(), startCount)
          if (!line.isPoint()) {
            var endCount: Integer = _pointsCountMap.getOrElse(line.getEnd(), null)
            if (endCount == null)
              endCount = 1
            else
              endCount = endCount + 1
            _pointsCountMap.put(line.getEnd(), endCount)
          }
        }
      }
    }
    _pointsCountMap
  }

  /** Find how many lines each point is part of by making a map */
  def getPointsToLineMap(): Map[IPoint2D, Set[CLine]] = {
    if (_pointsToLineMap == null) {
      _pointsToLineMap = new HashMap[IPoint2D, Set[CLine]]()
      _lines.foreach { (line: CLine) =>
        var lineSetForStartPoint: Set[CLine] = _pointsToLineMap.getOrElse(line.getStart(), null)
        if (lineSetForStartPoint == null) {
          lineSetForStartPoint = new HashSet[CLine]()
          _pointsToLineMap.put(line.getStart(), lineSetForStartPoint)
        }
        lineSetForStartPoint.add(line)
        var lineSetForEndPoint: Set[CLine] = _pointsToLineMap.getOrElse(line.getEnd(), null)
        if (lineSetForEndPoint == null) {
          lineSetForEndPoint = new HashSet[CLine]()
          _pointsToLineMap.put(line.getEnd(), lineSetForEndPoint)
        }
        lineSetForEndPoint.add(line)
      }
    }
    _pointsToLineMap
  }

  def findPointCount(): Int = {
    getPointsCountMap()
    val ONE: Integer = 1
    _endPointCount = 0
    _pointsCountMap.keys.toSeq.foreach { point =>
      {
        if (ONE.equals(_pointsCountMap.get(point)))
          _endPointCount = _endPointCount + 1
      }
    }
    _endPointCount
  }

  def getEndPointCount(): Int = {
    getValue()
    _endPointCount
  }

  def getLinesForPoint(point: IPoint2D): Set[CLine] = {
    val result = new HashSet[CLine]()
    if (point == null)
      return result
    getValue()
    if (!_points.contains(point))
      return result
    _lines.foreach { (line: CLine) =>
      if (line.getStart().equals(point) || line.getEnd().equals(point))
        result.add(line)
    }
    result
  }

  def getVersion(): Int = {
    _version
  }

  def setVersion(version: Int) = {
    _version = version
  }

  def startMultiLine(): Unit = {
    if (_currentMultiLine == null)
      _currentMultiLine = new MultiLine(this.getAnnotatedShape())
  }

  def addBeforeStart(newPoint: IPoint2D): Unit = {
    _currentMultiLine.addBeforeStart(newPoint)
  }

  def addAfterEnd(newPoint: IPoint2D): Unit = {
    _currentMultiLine.addAfterEnd(newPoint)
  }

  /** Add all the lines segments in the multi line to _lines */
  def endMultiLine(): Unit = {
    if (_currentMultiLine != null && _currentMultiLine.getPoints().size > 0)
      addMultiLine(_currentMultiLine)
    _currentMultiLine = null
  }

  def addMultiLine(multiLine: MultiLine): Unit = {
    var lastPoint: IPoint2D = null
    var linesAdded: Int = 0
    multiLine.getPoints().foreach {
      (point: IPoint2D) =>
        if (lastPoint == null) {
          lastPoint = point
        } else {
          addLine(lastPoint, point)
          linesAdded += 1
        }
        lastPoint = point
    }
    if (linesAdded == 0 && lastPoint != null)
      addLine(lastPoint, lastPoint)
  }

  def getCurrentMultiLine(): MultiLine = {
    _currentMultiLine
  }

  def getEndPointsClusters(): ArrayBuffer[Set[IPoint2D]] = {
    if (_endPointsClusters == null) {
      _endPointsClusters = new ArrayBuffer[Set[IPoint2D]]()
      getPoints().foreach { (point: IPoint2D) =>
        //  				inner_loop: 
        var stopInner = false
        _endPointsClusters.foreach { (cluster: Set[IPoint2D]) =>
          if (!stopInner && point.distance(cluster.iterator.next) <= MAX_DISTANCE_BETWEEN_CLUSTER_POINTS) {
            cluster.add(point)
            stopInner = true
          }
        }
        val cluster = new HashSet[IPoint2D]()
        cluster.add(point)
        _endPointsClusters.append(cluster)
      }
    }
    _endPointsClusters
  }

  def getEndPointsMultiClusters(): ArrayBuffer[Set[IPoint2D]] = {
    val result: ArrayBuffer[Set[IPoint2D]] = new ArrayBuffer[Set[IPoint2D]]()
    getEndPointsClusters().foreach { (cluster: Set[IPoint2D]) =>
      if (cluster.size > 1)
        result.append(cluster)
    }
    result
  }

  override def clone(): Object = {
    try {
      super.clone()
    } catch {
      case e: CloneNotSupportedException => {
        e.printStackTrace()
        null
      }
    }
  }

  /** No filtering in first version */

  override def replacePointsInMap(pointReplacementMap: Map[IPoint2D, IPoint2D],
    annotatedShape: AnnotatedShapeImplementation): Polygon = {
    val replacedPolygon: Polygon = new Polygon(annotatedShape)
    replacedPolygon.setup()
    _lines.foreach {
      (line: CLine) =>
        {
          val newLine = line.replacePointsInMap(pointReplacementMap, annotatedShape)
          if (!newLine.isPoint()) {
            replacedPolygon.addIndependentLine(newLine)
          }
        }
    }
    var annotationForOldPolygon: Set[Object] = null
    if (annotatedShape != null)
      annotationForOldPolygon = annotatedShape.getAnnotationForShapes(this)
    if (annotationForOldPolygon != null) {
      annotatedShape.putAllAnnotation(replacedPolygon, annotationForOldPolygon)
    }
    replacedPolygon
  }

  override def getCenter(): IPoint2D = {
    _bBox.getCenter()
  }

  override def getDiameter(): Double = {
    getBBox().getDiameter()
  }

  def setPolygonImprovers(improvers: ArrayBuffer[Improver[Polygon]]) {
    _polygonImprovers = improvers
  }

  /** To have the same interface as MultiLinePolygon */
  def addIndependentLine(point1: IPoint2D, point2: IPoint2D): CLine = {
    addLine(point1, point2)
  }

  /**
   * Most of the time this should not be used use the version taking input
   * points instead
   */
  def addIndependentLine(line: CLine): CLine = {
    addLine(line)
  }

  /** To have the same interface as MultiLinePolygon */
  def getIndependentLines(): Set[CLine] = {
    getLines()
  }

  /**
   * To have the same interface as MultiLinePolygon
   * returns null
   * since this and the independent lines are supposed to be all the lines
   */
  def getMultiLines(): ArrayBuffer[MultiLine] = {
    null
  }

  def getHoleCount(): Int = {
    getLines().size + 1 - getPoints().size
  }

  override def toString(): String = {
    val result = new StringBuffer()
    internalInfo(result)
    printAnnotation(result)
    result.toString()
  }

  def internalInfo(sb: StringBuffer): String = {
    sb.append("\n\n=====Class: ").append(getClass().getSimpleName()).append("=====\n")
    if (null != _bBox)
      sb.append(_bBox.toString())
    if (null != _currentMultiLine)
      _currentMultiLine.internalInfo(sb)
    else {
      sb.append("Lines:\n")
      getLines().foreach { (line: CLine) =>
        sb.append(line)
      }
      sb.append("\nPoints:\n")
      getPoints().foreach { (point: IPoint2D) =>
        sb.append(point)
      }
    }
    sb.toString()
  }

  def printAnnotation(result: StringBuffer): String = {
    result.append("\nAnnotations:\n")
    val annotationMap: Map[Object, Set[GeometricShape2D]] = getAnnotatedShape().getMap()
    annotationMap.foreach {
      case (key, value) =>
        result.append(key + ":\n" + value + "\n")
    }
    result.append("\naspectRatio: " + getBBox().getAspectRatio())
    result.toString()
  }

  def getPerimeter(): Double = {
    _perimeter
  }

  def setPerimeter(perimeter: Double) {
    _perimeter = perimeter
  }

}