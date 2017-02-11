package org.shapelogic.sc.imageprocessing

import org.scalatest._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

class AbstractImageProcessingSpec extends FunSuite with BeforeAndAfterEach {
//	DirectionBasedVectorizer directionBasedVectorizer = new DirectionBasedVectorizer();
//	
//	String _dirURL
//	String _fileFormat
//	boolean _doPrint = true
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp()
//	}
//	
//	String filePath(String fileName) {
//		return _dirURL + "/" + fileName + _fileFormat
//	}
//	
//	String filePath(String fileName, String fileFormat) {
//		return _dirURL + "/" + fileName + fileFormat
//	}
//	
//	public void printLines(Polygon polygon) {
//		if (!_doPrint)
//			return
//		System.out.println("Print lines:")
//		for (CLine line: polygon.getLines()) {
//			System.out.println(line)
//		}
//	}
//
//	public void printPoints(Polygon polygon) {
//		if (!_doPrint)
//			return
//		System.out.println("Print points:")
//		for (IPoint2D point: polygon.getPoints()) {
//			System.out.println(point)
//		}
//	}
//	
//	public void printAnnotaions(Polygon polygon) {
//		if (!_doPrint)
//			return
//		System.out.println("Print annotations:")
//		Map<Object, Set<GeometricShape2D>> map = polygon.getAnnotatedShape().getMap()
//		for (Entry<Object, Set<GeometricShape2D>> entry: map.entrySet())
//			System.out.println(entry.getKey() +":\n" + entry.getValue())
//	}
//	
//	public void printPolygon(Polygon polygon) {
//		if (!_doPrint)
//			return
//		System.out.println("Print polygon:")
//		printLines(polygon)
//		printPoints(polygon)
//	}
//	
//	static public void assertEmptyCollection(Object obj){
//		if (obj != null) {
//			if (obj instanceof Collection) {
//				Collection coll = (Collection) obj
//				assertEquals(0, coll.size())
//			} 
//		}
//	}
//	
//	/** To prevent waring when running all tests. */
//	public void dummyTest() {
//		assertTrue(true)
//	}
}