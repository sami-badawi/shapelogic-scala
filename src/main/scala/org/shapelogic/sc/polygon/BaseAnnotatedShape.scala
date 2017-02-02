package org.shapelogic.sc.polygon

import scala.collection.mutable.Map
import scala.collection.mutable.Set

/**
 * This is an adapter class for AnnotatedShape, working as an abstract base
 * class for classes that need to implement AnnotatedShape
 *
 * Can make lazy init later
 * @author Sami Badawi
 *
 */
abstract class BaseAnnotatedShape(annotatedShape: AnnotatedShapeImplementation) extends AnnotatedShape {
  val _annotatedShape: AnnotatedShapeImplementation = if (annotatedShape != null)
    annotatedShape
  else
    new AnnotatedShapeImplementation(null)

  override def getMap(): Map[Object, Set[GeometricShape2D]] = {
    return _annotatedShape.getMap()
  }

  override def putAnnotation(shape: GeometricShape2D, annotationKey: Object): Unit = {
    _annotatedShape.putAnnotation(shape, annotationKey);
  }

  override def putAllAnnotation(shape: GeometricShape2D, annotationKeySet: Set[Object]): Unit = {
    _annotatedShape.putAllAnnotation(shape, annotationKeySet);
  }

  override def getAnnotatedShape(): AnnotatedShapeImplementation = {
    return _annotatedShape
  }

  override def setup(): Unit = {
    _annotatedShape.setup()
  }

  override def getShapesForAnnotation(annotation: Object): Set[GeometricShape2D] = {
    return _annotatedShape.getShapesForAnnotation(annotation)
  }

  override def getAnnotationForShapes(shape: GeometricShape2D): Set[Object] = {
    return _annotatedShape.getAnnotationForShapes(shape);
  }

}
