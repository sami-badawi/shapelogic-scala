package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.calculation.CalcInvoke
import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.polygon.Calculator2D
import org.shapelogic.sc.util.DoubleCalculations
import org.shapelogic.sc.util.LineType
import org.shapelogic.sc.util.LineType.LineType
import scala.collection.mutable.Set
import scala.collection.mutable.HashSet

/**
 * LineProperties contains properties that are important for a line
 * when you are dealing with curved multi line.
 *
 * @author Sami Badawi
 *
 * Properties to keep track of
 * Min and max point
 * Number of positive, almost zero and negative pixels
 * Sum of positive and sum of negative distances
 * If direction change for the 2 adjacent point is different sign
 *
 * Assumptions
 * I do not think that this class is going be reused
 *
 */
class LineProperties extends CalcInvoke[Set[LineType]] {
  val STRAIGHT_LIMIT = 50 //straight 

  var pixelsWithPositiveDistance = 0
  var pixelsWithNegativeDistance = 0
  var pixelsWithAlmostZeroDistance = 0
  var areaPositiveDistance = 0
  var areaNegativeDistance = 0
  var _allPixels: Int = 0
  var maxPositiveDist: Int = 0
  var maxPositiveIndex: Int = 0
  var maxPositivePoint = new CPointInt(null)
  var maxNegativeDist: Int = 0
  var maxNegativeIndex: Int = 0
  var maxNegativePoint = new CPointInt(null)
  var angle: Double = 0
  var _dirty: Boolean = true
  var _lineType: LineType = null
  val _value: Set[LineType] = new HashSet[LineType]()
  var startPoint: CPointInt = null
  var relativeVector: CPointInt = null
  var orthogonalVector: CPointInt = null // orthogonal
  var lengthOfDistanceUnit: Double = 0

  /** a box that is 10% of the length  */
  var straightAreaLimit: Int = 0
  var archAreaLimit: Int = 0

  var lastDist: Double = 0
  var nextDist: Double = 0

  var inflectionPoint: Boolean = false //inflection

  override def setup(): Unit = {
  }

  def preCalc(): Unit = {
    _allPixels = pixelsWithPositiveDistance + pixelsWithNegativeDistance +
      pixelsWithAlmostZeroDistance
    val lineLength: Double = Math.max(relativeVector.distanceFromOrigin(), _allPixels * 0.5)

    straightAreaLimit = (lengthOfDistanceUnit * lineLength * Math.max(1, lineLength * 0.03)).toInt
    archAreaLimit = straightAreaLimit / 16
  }

  override def invoke(): Set[LineType] = {
    preCalc()
    calcLineType()
    if (isConcaveArch()) {
      _value.add(LineType.CONCAVE_ARCH)
    }
    if (inflectionPoint) {
      _value.add(LineType.INFLECTION_POINT)
    }
    _value
  }

  override def getValue(): Set[LineType] = {
    if (isDirty())
      invoke()
    _value
  }

  /**
   * The main LineType for a line there are 3 options: straight, arch, wave.
   */
  def calcLineType(): LineType = {
    if (isStraight()) _lineType = LineType.STRAIGHT
    else if (isCurveArch()) _lineType = LineType.CURVE_ARCH
    else _lineType = LineType.WAVE
    _dirty = false
    _value.add(_lineType)
    _lineType
  }

  /**
   * Same unnormalized point distance to line used in splitting line.
   *
   * @param point an input
   * @return unnormalized distance to the line from the star to end point
   */
  def distanceToPoint(point: CPointInt): Double = {
    if (orthogonalVector == null)
      orthogonalVector = Calculator2D.hatPoint(relativeVector).asInstanceOf[CPointInt]
    val distanceOfStartPoint: Double = Calculator2D.dotProduct(orthogonalVector, startPoint)
    val distanceOfPoint: Double = Calculator2D.dotProduct(orthogonalVector, startPoint)
    distanceOfPoint - distanceOfStartPoint
  }

  //Getter and setter part

  /**
   * To be straight
   * the diagonal variation can only be 0.1 time the length of the line.
   *
   * This could done by
   * 1: Max distance
   * 2: Average area outside the line
   *
   * What is better?
   * 2 should be more robust, let me start by doing that
   *
   * So I need to know the length of the line and the length of the hat vector that should be the same
   *
   */
  def isStraight(): Boolean = {
    areaNegativeDistance + areaPositiveDistance < straightAreaLimit
  }

  /**
   * To be an curve arch
   * only one side can be represented.
   *
   */
  def isCurveArch(): Boolean = {
    (areaNegativeDistance <= archAreaLimit) ^ (areaPositiveDistance <= archAreaLimit)
  }

  /** lineType needs to be set first. */
  def isConcaveArch(): Boolean = {
    if (_lineType != LineType.CURVE_ARCH)
      return false
    if (!DoubleCalculations.sameSign(lastDist, nextDist))
      return true
    if (areaPositiveDistance > areaNegativeDistance) {
      return DoubleCalculations.sameSign(areaPositiveDistance, lastDist)
    } else {
      return DoubleCalculations.sameSign(areaNegativeDistance, lastDist)
    }
  }

  override def isDirty(): Boolean = {
    _dirty
  }
}
