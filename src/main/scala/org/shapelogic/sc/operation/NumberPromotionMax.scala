package org.shapelogic.sc.operation

/**
 * In order to make more generic image processing operations
 * more things need to be known
 * They are not always very uniform
 * You could imagine having one type of Float images that go between 0.0 and 1.0
 * and another one that goes from 0.0 to 255.0 so normal Byte range with bigger precision
 * or one that take up the whole float range.
 * 
 * This is one reason that number promotion is handy
 */
trait NumberPromotionMax[I] extends NumberPromotion[I] {
  def max: Out
}