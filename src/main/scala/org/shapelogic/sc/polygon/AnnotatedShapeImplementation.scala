package org.shapelogic.sc.polygon

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.collection.mutable.Set
import scala.collection.mutable.Set

//import org.shapelogic.sc.logic.RootTask;

/**
 * Instead putting all logic for AnnotatedShape in all classes implement this
 * Make them adapter for this class
 *
 * I can make the map a lazy init map
 *
 * @author Sami Badawi
 *
 */
class AnnotatedShapeImplementation(val annotatedShape: AnnotatedShape) extends AnnotatedShape {

  var _map: Map[Object, Set[GeometricShape2D]] =
    if (annotatedShape == null)
      new HashMap[Object, Set[GeometricShape2D]]()
    else
      annotatedShape.getMap()

  override def getMap(): Map[Object, Set[GeometricShape2D]] = {
    _map
  }

  override def putAnnotation(shape: GeometricShape2D, annotationKey: Object): Unit = {
    var set: Set[GeometricShape2D] = _map.getOrElse(annotationKey, null)
    if (set == null) {
      set = new HashSet[GeometricShape2D]()
      _map.put(annotationKey, set)
    }
    set.add(shape)
  }

  override def setup() = {
    _map.clear()
  }

  override def getAnnotatedShape(): AnnotatedShapeImplementation = {
    this
  }

  /**
   * Get a set of all shape that has an annotation
   * The annotation will usually be a enum.
   * If nothing is return then see if input is a string and try to parse it
   * into an enum value
   */
  override def getShapesForAnnotation(annotation: Object): Set[GeometricShape2D] = {
    var result: Set[GeometricShape2D] = _map.getOrElse(annotation, null)
    if (result == null) {
      //      if (annotation.isInstanceOf[String]) {
      //        val rootTask: RootTask = RootTask.getInstance()
      //        val obj: Object = rootTask.findEnumValue(annotation.asInstanceOf[String])
      //        if (obj != null)
      //          result = _map.get(obj)
      //      }
    }
    if (result == null) {
      Set()
    } else
      result
  }

  override def getAnnotationForShapes(shape: GeometricShape2D): Set[Object] = {
    val result: HashSet[Object] = new HashSet[Object]()
    _map.foreach(entry => {
      if (entry._2.contains(shape)) {
        result.add(entry._1)
      }
    })
    result
  }

  override def putAllAnnotation(shape: GeometricShape2D,
    annotationKeySet: Set[_ <: Object]): Unit = {
    if (annotationKeySet == null)
      return 
    annotationKeySet.foreach { key =>
      putAnnotation(shape, key)
    }
  }
}
