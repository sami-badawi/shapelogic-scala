package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.image.BufferImage

/**
 * LineVectorizer is a vectorizer using short line of default length 5.
 *
 * You do a sequence of small lines, if 2 consecutive lines are close in direction you can merge them.
 * But if they are far away in angle you create a new line.
 * Also you can only have 2 direction within the same line, they are stored by cycle index.
 *
 * @author Sami Badawi
 *
 */
class LineVectorizer(imageIn: BufferImage[Byte]) extends ShortLineBasedVectorizer(imageIn) {

  /**
   * Test that the current direction is close to the last direction.
   */
  def multiLineHasGlobalFitness(): Boolean = {
    //do big test
    if (_pointsInCurrentShortLine >= _maxPointsInShortLine) {
      _currentVectorDirection = _currentPoint.copy().minus(_startOfShortLinePoint).asInstanceOf[CPointInt]
      val currentAngel: Double = _currentVectorDirection.angle()
      if (!currentAngel.isNaN)
        _currentCircleInterval.addClosestAngle(currentAngel)
      newShortLine()
      return currentAngel.isNaN || _currentCircleInterval.intervalLength() < _angleLimit
    }
    if (_currentDirection != _firstUsedDirection) {
      if (_secondUsedDirection == Constants.DIRECTION_NOT_USED) {
        _secondUsedDirection = _currentDirection
      } else if (_currentDirection != _secondUsedDirection) {
        return false
      }
    }
    true
  }

}
