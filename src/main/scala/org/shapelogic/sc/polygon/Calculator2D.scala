package org.shapelogic.sc.polygon

import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.util.DoubleCalculations._
import org.shapelogic.sc.util.DoubleCalculations

//import org.apache.commons.math3.linear.InvalidMatrixException
//import org.apache.commons.math3.linear.RealMatrix
//import org.apache.commons.math3.linear.RealMatrixImpl

import breeze.math._
import breeze.linalg._
import breeze.optimize.linear._

/**
 * Calculator for simple 2D.
 *
 * There are a lot of small calculations connected with 2D that should not be
 * thrown in Point and Line classes, but belong in a utility class.
 *
 * I might move more stuff into this.
 *
 * @author Sami Badawi
 *
 */
object Calculator2D {

  /**
   * Hat is really a vector operator, I consider a Point a vector here.
   *
   * This is maybe simple enough to be on Point classes
   */
  def hatPoint(inPoint: IPoint2D): IPoint2D = {
    val outPoint = inPoint.copy()
    outPoint.setLocation(-inPoint.getY(), inPoint.getX())
    outPoint
  }

  /** A cosine to the angle between times the 2 vector lengths. */
  def dotProduct(inPoint1: IPoint2D, inPoint2: IPoint2D): Double = {
    inPoint1.getX() * inPoint2.getX() + inPoint1.getY() * inPoint2.getY()
  }

  /**
   * A sine from the first to the second times the 2 vector lengths
   * Not a real cross product, but the length of the cross product vector
   */
  def crossProduct(inPoint1: IPoint2D, inPoint2: IPoint2D): Double = {
    dotProduct(hatPoint(inPoint1), inPoint2)
  }

  /** This is signed. */
  def distanceOfPointToLine(point: IPoint2D, line: CLine): Double = {
    val orthogonalVector: IPoint2D = hatPoint(line.relativePoint())
    dotProduct(orthogonalVector, point.copy().minus(line.getStart()))
  }

  def scaleLineFromStartPoint(line: CLine, length: Double): CLine = {
    val resultLine = new CLine(line.getStart(), line.getStart().copy().add(line.relativePoint().multiply(length)))
    resultLine
  }

  def pointToLine(point: IPoint2D): CLine = {
    new CLine(point.copy().minus(point), point)
  }

  def projectionOfPointOnLine(point: IPoint2D, line: CLine): IPoint2D = {
    val lineFromStartToPoint = new CLine(line.getStart(), point)
    val relLine = line.relativePoint()
    val dot = dotProduct(lineFromStartToPoint.relativePoint(), relLine)
    val projectedLine: CLine = scaleLineFromStartPoint(line, dot / line.distance())
    val projectedPoint: IPoint2D = projectedLine.getEnd()
    if (point.isInstanceOf[CPointInt])
      toCPointIntIfPossible(projectedPoint)
    else
      projectedPoint
  }

  def inverseLine(line: CLine): CLine = {
    new CLine(line.getEnd(), line.getStart())
  }

  def addLines(line1: CLine, line2: CLine): CLine = {
    new CLine(line1.getStart(), line1.getEnd().copy().add(line2.relativePoint()))
  }

  /**
   * What should I do about integer based points that does not have a.
   * @param point
   * @return the point scaled to unit length
   */
  def unitVector(point: IPoint2D): IPoint2D = {
    var result: IPoint2D = null
    if (point.isInstanceOf[CPointInt]) {
      if (point.isOnAxis())
        result = point.copy()
      else
        result = new CPointDouble(point.getX(), point.getY())
    }
    result.multiply(1 / point.distanceFromOrigin())
  }

  def isPointIntBased(point: IPoint2D): Boolean = {
    if (point.isInstanceOf[CPointInt])
      return true
    val x = point.getX().toInt
    val y = point.getY().toInt
    doubleEquals(point.getX(), x) && doubleEquals(point.getY(), y)
  }

  def toCPointDouble(point: IPoint2D): CPointDouble = {
    new CPointDouble(point.getX(), point.getY())
  }

  def toCPointInt(point: IPoint2D): CPointInt = {
    if (point.isInstanceOf[CPointInt])
      point.asInstanceOf[CPointInt]
    else
      new CPointInt(point.getX().toInt, point.getY().toInt)
  }

  def toCPointIntIfPossible(point: IPoint2D): IPoint2D = {
    if (point.isInstanceOf[CPointInt])
      point.asInstanceOf[CPointInt]
    else if (isPointIntBased(point))
      new CPointInt(point.getX().toInt, point.getY().toInt)
    else
      point
  }

  def linesParallel(line1: CLine, line2: CLine): Boolean = {
    doubleZero(dotProduct(line1.relativePoint(), hatPoint(line2.relativePoint())))
  }

  /**
   * This version is complicated and has a bug.
   *
   * Make a projection of the start point of line1 on line2
   *
   * This passes some tests
   *
   * XXX @Deprecated
   */
  def intersectionOfLinesGeometric(activeLine: CLine, projectionLine: CLine): IPoint2D = {
    if (activeLine.distance() == 0.0) {
      return projectionOfPointOnLine(activeLine.getStart(), projectionLine)
    } else if (projectionLine.distance() == 0.0) {
      return projectionOfPointOnLine(projectionLine.getStart(), activeLine)
    }
    val startActiveToProjection = new CLine(activeLine.getStart(), projectionLine.getStart())
    if (startActiveToProjection.distance() == 0.0)
      return activeLine.getStart() //XXX there can be more result points if they are parallel
    if (linesParallel(activeLine, projectionLine))
      return null
    val orthogonalProjection = hatPoint(projectionLine.relativePoint())
    var startActiveToProjectionDistanceuct = dotProduct(startActiveToProjection.relativePoint(), orthogonalProjection)
    val cosine = dotProduct(activeLine.relativePoint(), orthogonalProjection)
    startActiveToProjectionDistanceuct = startActiveToProjectionDistanceuct / projectionLine.distance()
    val start1ToProjectionOn2 = scaleLineFromStartPoint(activeLine, startActiveToProjectionDistanceuct / cosine)
    val intersectionPoint = start1ToProjectionOn2.getEnd()
    if (activeLine.getStart().isInstanceOf[CPointInt])
      toCPointIntIfPossible(intersectionPoint)
    else
      intersectionPoint
  }

  def intersectionOfLinesBreeze(line1: CLine, line2: CLine): IPoint2D = {
    var intersectionPoint: IPoint2D = null
    val vector1 = line1.relativePoint()
    val vector2 = line2.relativePoint()
    val hat1 = vector1.turn90()
    val hat2 = vector2.turn90()

    val coefficientsData: Array[Array[Double]] =
      Array(
        Array(hat1.getX(), hat1.getY()),
        Array(hat2.getX(), hat2.getY()))
    val coefficients: DenseMatrix[Double] = DenseMatrix(Array(hat1.getX(), hat1.getY()),
      Array(hat2.getX(), hat2.getY()))
    val constants: Array[Double] = Array(dotProduct(hat1, line1.getStart()),
      dotProduct(hat2, line2.getStart()))
    val constantVector: DenseVector[Double] = DenseVector(constants: _*)
    try {
      val solution = coefficients.\(constantVector)
      if (solution != null) {
        val x = solution(0)
        val y = solution(1)
        if (doubleIsInt(x) && doubleIsInt(y))
          return new CPointInt(x.toInt, y.toInt)
        else
          return new CPointDouble(x, y)
      } else {
        println("No solution found")
        null
      }
    } catch {
      case e: Throwable => {
        println("intersectionOfLinesBreeze: ${e.getMessage}")
        e.printStackTrace()
        null
      }
    }
  }

  /**
   * Very simple turn the 2 line into a line equation: a * x + b * y = c.
   *   So a and b is just the hat vector. While c is what you get when you put one
   *   of the point into this.
   */
  def intersectionOfLines(line1: CLine, line2: CLine): IPoint2D = {
    var intersectionPoint: IPoint2D = null
    val vector1 = line1.relativePoint()
    val vector2 = line2.relativePoint()
    val hat1 = vector1.turn90()
    val hat2 = vector2.turn90()
    if (vector1.isNull() && vector2.isNull()) { // 2 points
      if (line1.getStart().equals(line2.getStart())) // identical points
        return line1.getStart()
      else
        return null
    } else if (!vector1.isNull() && !vector2.isNull()) { // 2 lines
      return intersectionOfLinesBreeze(line1, line2)
    } else if (vector1.isNull() && !vector2.isNull()) {
      val point = line1.getStart()
      if (pointIsOnLine(point, line2))
        return point
      else
        return null
    } else if (!vector1.isNull() && vector2.isNull()) {
      val point = line2.getStart()
      if (pointIsOnLine(point, line1))
        return point
      else
        return null
    } else {
      return null
    }
  }

  def pointIsOnLine(point: IPoint2D, line: CLine): Boolean = {
    val lineVector: IPoint2D = line.relativePoint()
    if (lineVector.isNull()) {
      return point.equals(line.getStart())
    }
    val orthogonalVector: IPoint2D = lineVector.turn90()
    DoubleCalculations.doubleEquals(dotProduct(orthogonalVector, point),
      dotProduct(orthogonalVector, line.getStart()))
  }

  def directionBetweenNeighborPoints(startPoint: IPoint2D, endPoint: IPoint2D): Byte = {
    val directionVector = endPoint.copy().minus(startPoint)
    if (directionVector.isNull())
      return Constants.DIRECTION_NOT_USED
    if (Math.abs(directionVector.getX()) <= 1 &&
      Math.abs(directionVector.getY()) <= 1) {
      var result = Math.round(directionVector.angle() * 4 / Math.PI)
      if (result < 0)
        result += 8
      return result.toByte
    }
    Constants.DIRECTION_NOT_USED
  }

  def oppositeDirection(directionIn: Byte): Byte = {
    var direction: Byte = (directionIn + 4).toByte
    if (Constants.DIRECTIONS_AROUND_POINT <= direction)
      direction = (direction - Constants.DIRECTIONS_AROUND_POINT).toByte
    direction
  }

  /**
   * Takes to directions between 0 and 7, and gives the direction difference.
   * @return should be between -4 and 4
   */
  def directionDifference(direction1: Byte, direction2: Byte): Byte = {
    var diff: Byte = (direction2 - direction1).toByte
    if (4 < diff)
      diff = (diff - Constants.DIRECTIONS_AROUND_POINT).toByte
    else if (diff < 4)
      diff = (diff + Constants.DIRECTIONS_AROUND_POINT).toByte
    diff
  }

  def angleBetweenLines(firstAngle: Double, nextAngle: Double): Double = {
    var result = nextAngle - firstAngle
    if (result < -Math.PI)
      result += Math.PI * 2
    else if (Math.PI < result)
      result -= Math.PI * 2
    result
  }

  /**
   * Find a point on a line spanned by 2 other points.
   *
   * part 0 -> minVal, 1 -> maxVal, 0.5 -> middle point
   */
  def spannedPoint(point1: IPoint2D, point2: IPoint2D, fraction: Double): IPoint2D = {
    val spanned: IPoint2D = point2.copy().multiply(fraction).add(point1.copy().multiply(1 - fraction))
    spanned
  }
}
