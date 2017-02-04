package org.shapelogic.sc.polygon

import org.scalatest._
import scala.reflect.ClassTag
import scala.specialized
import spire.math.Numeric

class AnnotatedShapeImplementationSpec extends FunSuite with BeforeAndAfterEach {

  val ANNOTATED_SHAPE = "annotatedShape";
  val annotatedShape: AnnotatedShape = new AnnotatedShapeImplementation();
  //	RootTask rootTask;

  def setUp(): Unit = {
    annotatedShape.setup();
    //		rootTask = RootTask.getInstance();
  }

//  test("testBasicSetAndGet") {
//    setUp()
//    val p1 = new CPointInt(1, 2);
//    val p2 = new CPointInt(2, 2);
//    val ONE = new Integer(1);
//    annotatedShape.putAnnotation(p1, ONE);
//    AbstractImageProcessingTests.assertEmptyCollection(annotatedShape.getShapesForAnnotation(Constants.ZERO));
//    assertResult(1) { annotatedShape.getShapesForAnnotation(ONE).size() }
//    annotatedShape.putAnnotation(p2, 1);
//    assertResult(2) { annotatedShape.getShapesForAnnotation(1).size() }
//  }

  //XXX needs rootTask probably not going to make this
  //	public void testAnnotations() {
  //		CPointInt p1 = new CPointInt(1,2); 
  //		annotatedShape.putAnnotation(p1, PointType.END_POINT);
  //		assertEquals(1,annotatedShape.getShapesForAnnotation(PointType.END_POINT).size());
  //		assertEquals(1,annotatedShape.getMap().size());
  //		rootTask.setNamedValue(ANNOTATED_SHAPE, annotatedShape);
  //		assertEquals(annotatedShape,rootTask.findNamedValue(ANNOTATED_SHAPE));
  //		String annoationCount = "annotatedShape.getMap().size()";
  //		assertEquals(1,rootTask.findNamedValue(annoationCount));
  //		assertEquals(PointType.END_POINT, Enum.valueOf(PointType.class, "END_POINT"));
  //		String pointCount = "annotatedShape.getShapesForAnnotation(\"PointType.END_POINT\").size()";
  //		assertEquals(1,rootTask.findNamedValue(pointCount));
  //		String pointCountQuotes = "annotatedShape.getShapesForAnnotation('PointType.END_POINT').size()";
  //		assertEquals(1,rootTask.findNamedValue(pointCountQuotes));
  //	}

}