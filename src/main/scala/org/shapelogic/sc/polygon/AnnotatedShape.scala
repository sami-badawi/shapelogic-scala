package org.shapelogic.sc.polygon

/**
 * Interface of an annotated shape.
 *
 * Polygon, MultiLine and ChainCodeHander implement this.<p>
 *
 * Currently there are a few base classes that implements the functionality in this.
 * And the other subclass them .
 *
 * @author Sami Badawi
 *
 */

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set

trait AnnotatedShape {

  def getMap(): Map[Object, Set[GeometricShape2D]]
  def putAnnotation(shape: GeometricShape2D, annotation: Object): Unit
  def setup(): Unit
  def getAnnotatedShape(): AnnotatedShapeImplementation
  def getShapesForAnnotation(annotation: Object): Set[GeometricShape2D]
  def getAnnotationForShapes(shape: GeometricShape2D): Set[Object]
  def putAllAnnotation(shape: GeometricShape2D, annotationKeySet: Set[Object]): Unit
}
