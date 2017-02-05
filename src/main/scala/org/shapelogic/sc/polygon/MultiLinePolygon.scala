package org.shapelogic.sc.polygon

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashSet
import scala.collection.mutable.Map

/**
 * Not sure if I really need this or if I am going to move it into Polygon
 *
 * @author Sami Badawi
 *
 */
class MultiLinePolygon(annotatedShapeIn: AnnotatedShapeImplementation) extends Polygon(annotatedShapeIn) {

  val _multiLines: ArrayBuffer[MultiLine] = new ArrayBuffer[MultiLine]();
  val _independentLines: HashSet[CLine] = new HashSet[CLine]();

  override def addMultiLine(multiLine: MultiLine): Unit = {
    //    super.addMultiLine(multiLine) //XXX commented out
    _multiLines.append(multiLine);
  }

  override def endMultiLine(): Unit = {
    val simpleLine: CLine = _currentMultiLine.toCLine();
    if (simpleLine != null)
      addIndependentLine(simpleLine.getStart(), simpleLine.getEnd())
    else {
      if (_currentMultiLine != null && _currentMultiLine.getPoints().size > 0)
        addMultiLine(_currentMultiLine)
    }
  }

  override def addIndependentLine(point1: IPoint2D, point2: IPoint2D): CLine = {
    val line: CLine = addLine(point1, point2)
    _independentLines.add(line)
    line
  }

  override def getIndependentLines(): Set[CLine] = {
    return _independentLines;
  }

  override def getMultiLines(): ArrayBuffer[MultiLine] = {
    return _multiLines;
  }

  /**
   *  This is a little problematic
   */
  override def replacePointsInMap(
    pointReplacementMap: Map[IPoint2D, IPoint2D],
    annotatedShape: AnnotatedShapeImplementation): Polygon = {
    val replacedPolygon = new MultiLinePolygon(annotatedShape);
    replacedPolygon.setup();
    _independentLines.foreach { (line: CLine) =>
      val newLine: CLine = line.replacePointsInMap(pointReplacementMap, annotatedShape)
      if (!newLine.isPoint()) {
        replacedPolygon.addIndependentLine(newLine);
      }
    }
    _multiLines.foreach { (line: MultiLine) =>
      {
        val newMultiLine: MultiLine = line.replacePointsInMap(pointReplacementMap, annotatedShape);
        replacedPolygon.addMultiLine(newMultiLine);
      }
    }
    var annotationForOldPolygon: Set[Object] = null;
    if (annotatedShape != null)
      annotationForOldPolygon = annotatedShape.getAnnotationForShapes(this);
    if (annotationForOldPolygon != null) {
      annotatedShape.putAllAnnotation(replacedPolygon, annotationForOldPolygon);
    }
    return replacedPolygon
  }

  //  override 
  def internalInfo(sb: StringBuffer): String = {
    sb.append("\n\n=====Class: ").append(getClass().getSimpleName()).append("=====\n");
    _multiLines.foreach { (multiLine: MultiLine) =>
      {
        multiLine.internalInfo(sb)
      }
    }
    return sb.toString()
  }
}
