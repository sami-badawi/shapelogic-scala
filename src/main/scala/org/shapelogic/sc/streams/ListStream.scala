package org.shapelogic.sc.streams

/**
 * Universal calculation and logic unit.
 *
 * If this is an expansion of the calculation do I need a dirty?
 *
 * If there is only one value then a dirty makes sense.
 * So maybe I should just override this for single element.
 *
 * Should I assume that null means absent of value?
 * That seems a little harsh.
 *
 * I think that I can have a special index for single values.
 * So if that is different from null a calculation have been made.
 *
 * I could also have a isEmpty method.
 *
 * What about a setup or a init method?
 *
 * Maybe it should be a isDone method instead.
 * Or dirty can just mean the opposite of done.
 *
 * Let me try to sub interface this and see if I can make a single calculation
 * and a stream out of this.
 *
 * I think that maybe I should only extend the iterator and not the list.
 * Then override just the get method. Then there should be less trouble with
 * signatures.
 *
 * I might also extend or override the map.
 * So I would have both a get(int i) and a get(Object obj)
 *
 * Could I delegate the list specific work based on what is known?
 *
 * Maybe I would think that as far as a calculation model everything should
 * work the same.
 *
 * One problem might be a self reference.
 *
 * @author Sami Badawi
 *
 */
trait ListStream[E] extends NumberedStream[E] with Iterable[E] {

  /**
   * Get underlying list.
   *  Possibly a more general functor than Seq
   */
  def getList(): Seq[E]

  /** Get underlying list. */
  def setList(list: Seq[E]): Unit
}