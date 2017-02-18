package org.shapelogic.sc.streams

/**
 * The idea is that you can call a plugin in both active and lazy mode.
 * <br />
 *
 * How should this be instantiated?<br />
 * <br />
 * There should be a constructor where Imageprocessor is set.<br />
 *
 * @author Sami Badawi
 *
 */
trait LazyPlugInFilter[E] {

  /** Used when calling in lazy mode. */
  def getStream(): ListStream[E]

  /** Maybe getStreamName would be better. */
  def getStreamName(): String

  /** Maybe setStreamName would be better. */
  def setStreamName(name: String)

}
