package org.shapelogic.sc.mathematics

/**
 * Find min, max, mean, standard deviation, variance.<br />
 *
 * This exist in Apache Commons Math, but each statistic would have to be done
 * individually.
 *
 * @author Sami Badawi
 *
 */
class StorelessDiscriptiveStatistic {
  var _min: Double = Double.PositiveInfinity
  var _max: Double = Double.NegativeInfinity
  var _total: Double = 0.0
  var _totalSquare: Double = 0.0
  var _count: Int = 0

  /** Add an extra element to be part of the input. */
  def increment(input: Double): Unit = {
    if (input < _min)
      _min = input
    if (_max < input)
      _max = input
    _total += input
    _totalSquare += input * input
    _count += 1
  }

  /** Reset the object for reuse. */
  def clear(): Unit = {
    _min = Double.PositiveInfinity
    _max = Double.NegativeInfinity
    _total = 0.0
    _totalSquare = 0.0
    _count = 0
  }

  def getMin(): Double = {
    return _min
  }

  def getMax(): Double = {
    return _max
  }

  def getTotal(): Double = {
    return _total
  }

  def getTotalSquare(): Double = {
    return _totalSquare
  }

  def getCount(): Int = {
    return _count
  }

  def getMean(): Double = {
    return _total / _count
  }

  /** The biased estimator for Standard Deviation. */
  def getStandardDeviation(): Double = {
    if (_count == 0)
      return Double.NaN
    return Math.sqrt(getVariance())
  }

  /** The biased estimator for variance. */
  def getVariance(): Double = {
    if (_count == 0)
      return Double.NaN
    val mean = getMean()
    return _totalSquare / _count - mean * mean
  }

  def merge(input: StorelessDiscriptiveStatistic): Unit = {
    _total += input.getTotal()
    _totalSquare += input.getTotalSquare()
    _count += input.getCount()
    _min = Math.min(_min, input.getMin())
    _max = Math.max(_max, input.getMax())
  }
}
