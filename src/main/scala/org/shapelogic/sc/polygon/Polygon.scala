package org.shapelogic.sc.polygon

import scala.collection.mutable.Set
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object Polygon {
  val MAX_DISTANCE_BETWEEN_CLUSTER_POINTS: Double = 2;

}
class Polygon {
  import Polygon._
  var _bBox = new BBox()
  var _lines: Set[CLine] = Set()
  var _points: Set[IPoint2D] = Set()
  protected var _dirty = true;
  protected var _aspectRatio: Double = 0
  protected var _closed: Boolean = false
  protected var _endPointCount: Int = -1;
  protected val _pointsCountMap = Map[IPoint2D, Int]()
  protected val _pointsToLineMap = Map[IPoint2D, Set[CLine]]()
  protected var _version: Int = 0
  protected var _currentMultiLine: MultiLine = null
  protected val _endPointsClusters = ArrayBuffer[Set[IPoint2D]]()
  //I could make this lazy
  protected var _annotatedShape: AnnotatedShapeImplementation = null
  //  	protected val _polygonImprovers: ArrayBuffer[Improver[Polygon]] //XXX takes more imports
  protected var _perimeter: Double = 0

  //	public Polygon() {
  //		this(null);
  //	}
  //	
  //	public Polygon(AnnotatedShapeImplementation annotatedShape) {
  //		super(annotatedShape);
  //		setup();
  //		internalFactory();
  //	}
  //	
  //	/** All the objects that needs special version should be created here. */
  //	protected void internalFactory() {
  //		_polygonImprovers = new ArrayList<Improver<Polygon> >();
  //		_polygonImprovers.add(new FilterPolygonForSmallLines());
  //		_polygonImprovers.add(new PolygonAnnotator());
  //	}
  //	
  //	@Override
  //	public BBox getBBox() {
  //		getValue();
  //		return _bBox;
  //	}
  //
  //	@Override
  //	public Set<CLine> getLines() {
  //		return _lines;
  //	}
  //
  //	@Override
  //	public Set<IPoint2D> getPoints() {
  //		return _points;
  //	}
  //
  //	@Override
  //	public Double getAspectRatio() {
  //		getValue();
  //        if (_bBox.minVal!=null) { 
  //	        Double lenX = _bBox.maxVal.getX() - _bBox.minVal.getX();
  //	        Double lenY = _bBox.maxVal.getY() - _bBox.minVal.getY();
  //	        if (lenX > 0)
  //	            _aspectRatio = lenY / lenX;
  //	        else
  //	            _aspectRatio = Double.POSITIVE_INFINITY;
  //        }
  //		return _aspectRatio;
  //	}
  //
  //	@Override
  //	/** Does this make sense for a polygon or only for multi line */
  //	public Boolean isClosed() {
  //		return _closed;
  //	}
  //
  //	@Override
  //	public Boolean isDirty() {
  //		return _dirty;
  //	}
  //
  //	@Override
  //	public void setup() {
  //		_lines = new TreeSet<CLine>();
  //		_points = new TreeSet<IPoint2D>();
  //		_bBox = new BBox();
  //	}
  //
  //    public Boolean containsPoint(IPoint2D point) {
  //        return _points.contains(point);
  //    }
  //
  //    public Boolean containsLine(ILine2D line) {
  //        return _lines.contains(line);
  //    }
  //
  //    private void addPoint(IPoint2D point) {
  //        if (!containsPoint(point))
  //            _points.add(point);
  //    }
  //    
  //    /** this should not be used use addIndependentLine() instead */
  //    public CLine addLine(IPoint2D point1, IPoint2D point2) {
  //        CLine line = CLine.makeUnordered(point1, point2);
  //        if (!containsLine(line))
  //            _lines.add(line);
  //        addPoint(point1);
  //        addPoint(point2);
  //        return line;
  //    }
  //
  //    /** this should not be used use addIndependentLine() instead */
  //    @Deprecated
  //    protected CLine addLine(CLine line) {
  //        if (!containsLine(line)) {
  //            _lines.add(line);
  //            addPoint(line.getStart());
  //            addPoint(line.getEnd());
  //        }
  //        return line;
  //    }
  //    
  //	@Override
  //	public Polygon invoke() {
  //        _bBox = findBbox();
  //        findPointCount();
  //        _dirty = false;
  //        return this;
  //    }
  //
  //    private BBox findBbox() {
  //    	if(_bBox == null)
  //    		_bBox = new BBox();
  //        for (IPoint2D pointInPolygon : _points )
  //            _bBox.addPoint(pointInPolygon);
  //        return _bBox;
  //    }
  //    
  //    /** Return a cleaned up polygon 
  //     * 
  //     * @param onlyInt Change all coordinates to integers
  //     * @param procentage of diagonal of b box that should be considered as same point
  //     */
  //    public Polygon cleanUp(Boolean onlyInt, Double procentage) {
  //        findBbox();
  //        Double threshold = _bBox.getDiameter() * procentage;
  //        List<IPoint2D> roundedPoints = new ArrayList<IPoint2D>();
  //        Map<IPoint2D, IPoint2D> pointMap = new HashMap<IPoint2D, IPoint2D>();
  //        for (IPoint2D point : _points) {
  //        	IPoint2D roundedPoint = point;
  //        	if (onlyInt) {
  //        		roundedPoint = point.copy().round();
  //        		if (roundedPoint.equals(point) )
  //        			roundedPoint = point; //uses the same point, do not create new 
  //        	}
  //        	Iterator<IPoint2D> roundPointIterator = roundedPoints.iterator();
  //        	Boolean moreRoundedPoints = roundPointIterator.hasNext();
  //        	IPoint2D foundPoint = null;
  //        	while (moreRoundedPoints) {
  //        		IPoint2D point2 = roundPointIterator.next(); 
  //        		pointMap.put(point, point);
  //        		if (roundedPoint.distance(point2) < threshold) {
  //        			foundPoint = point2;
  //        			moreRoundedPoints = false;
  //        		}
  //        		else
  //        			moreRoundedPoints = roundPointIterator.hasNext();
  //        	}
  //        	if (foundPoint == null) {
  //        		roundedPoints.add(roundedPoint);
  //        		pointMap.put(point, roundedPoint);
  //        	}
  //        	else { 
  //        		pointMap.put(point, foundPoint);
  //        	}
  //          
  //        }
  //        return replacePointsInMap(pointMap,null);
  //    }
  //
  //    /** register a list of improvers and call them here */
  //    public Polygon improve() {
  //    	if (_polygonImprovers == null)
  //    		return this;
  //    	Polygon result = this;
  //    	for (Improver<Polygon> improver: _polygonImprovers) {
  //    		improver.setInput(result);
  //    		result = improver.getValue(); 
  //    	}
  //    	return result;
  //    }
  //    
  //	@Override
  //	public Polygon getValue() {
  //		if (isDirty())
  //			invoke();
  //		return this;
  //	}
  //	
  //	public List<CLine> getVerticalLines() {
  //		return CLine.filterVertical(_lines);
  //	}
  //	
  //	public List<CLine> getHorizontalLines() {
  //		return CLine.filterHorizontal(_lines);
  //	}
  //	
  //	/** Find how many lines each point is part of by making a map */
  //	public Map<IPoint2D,Integer> getPointsCountMap() {
  //		if (_pointsCountMap == null) {
  //			_pointsCountMap = new TreeMap<IPoint2D,Integer>();
  //			for (CLine line: _lines) {
  //				Integer startCount = _pointsCountMap.get(line.getStart());
  //				if (startCount == null)
  //					startCount = 1;
  //				else
  //					startCount++;
  //				_pointsCountMap.put(line.getStart(),startCount);
  //				if (line.isPoint())
  //					continue;
  //				Integer endCount = _pointsCountMap.get(line.getEnd());
  //				if (endCount == null)
  //					endCount = 1;
  //				else
  //					endCount++;
  //				_pointsCountMap.put(line.getEnd(),endCount);
  //			}
  //		}
  //		return _pointsCountMap;
  //	}
  //	
  //	/** Find how many lines each point is part of by making a map */
  //	public Map<IPoint2D,Set<CLine>> getPointsToLineMap() {
  //		if (_pointsToLineMap == null) {
  //			_pointsToLineMap = new TreeMap<IPoint2D,Set<CLine>>();
  //			for (CLine line: _lines) {
  //				Set<CLine> lineSetForStartPoint = _pointsToLineMap.get(line.getStart());
  //				if (lineSetForStartPoint == null) {
  //					lineSetForStartPoint = new TreeSet<CLine>();
  //					_pointsToLineMap.put(line.getStart(),lineSetForStartPoint);
  //				}
  //				lineSetForStartPoint.add(line);
  //				Set<CLine> lineSetForEndPoint = _pointsToLineMap.get(line.getEnd());
  //				if (lineSetForEndPoint == null) {
  //					lineSetForEndPoint = new TreeSet<CLine>();
  //					_pointsToLineMap.put(line.getEnd(),lineSetForEndPoint);
  //				}
  //				lineSetForEndPoint.add(line);
  //			}
  //		}
  //		return _pointsToLineMap;
  //	}
  //	
  //	public Int findPointCount() {
  //		getPointsCountMap();
  //		Integer ONE = 1;
  //		_endPointCount = 0;
  //		for (IPoint2D point: _pointsCountMap.keySet()) {
  //			if (ONE.equals(_pointsCountMap.get(point) ))
  //				_endPointCount++;
  //		}
  //		return _endPointCount;
  //	}
  //
  //	public Int getEndPointCount() {
  //		getValue();
  //		return _endPointCount;
  //	}
  //	
  //	public Collection<CLine> getLinesForPoint(IPoint2D point) {
  //		TreeSet<CLine> result = new TreeSet<CLine>();  
  //		if (point == null)
  //			return result;
  //		getValue();
  //		if (!_points.contains(point))
  //			return result;
  //		for (CLine line: _lines) {
  //			if (line.getStart().equals(point) || line.getEnd().equals(point))
  //			result.add(line);
  //		}
  //		return result;
  //	}
  //
  //	public Int getVersion() {
  //		return _version;
  //	}
  //
  //	public void setVersion(Int version) {
  //		_version = version;
  //	}
  //
  //	public void startMultiLine() {
  //		_currentMultiLine = new MultiLine(this.getAnnotatedShape());
  //	}
  //	
  //	public void addBeforeStart(IPoint2D newPoint) {
  //		_currentMultiLine.addBeforeStart(newPoint);
  //	}
  //
  //	public void addAfterEnd(IPoint2D newPoint) {
  //		_currentMultiLine.addAfterEnd(newPoint);
  //	}
  //
  //	/** Add all the lines segments in the multi line to _lines */
  //	public void endMultiLine() {
  //		if (_currentMultiLine != null && _currentMultiLine.getPoints().size() > 0)
  //			addMultiLine(_currentMultiLine);
  //		_currentMultiLine = null;
  //	}
  //
  //	public void addMultiLine(MultiLine multiLine) {
  //		IPoint2D lastPoint = null;
  //		Int linesAdded = 0;
  //		for (IPoint2D point: multiLine.getPoints()) {
  //			if (lastPoint == null) {
  //				lastPoint = point;
  //			}
  //			else {
  //				addLine(lastPoint, point);
  //				linesAdded++;
  //			}
  //			lastPoint = point;
  //		}
  //		if (linesAdded == 0 && lastPoint != null)
  //			addLine(lastPoint, lastPoint);
  //	}
  //
  //	public MultiLine getCurrentMultiLine() {
  //		return _currentMultiLine;
  //	}
  //
  //	public List<Set<IPoint2D>> getEndPointsClusters() {
  //		if (_endPointsClusters == null) {
  //			_endPointsClusters = new ArrayList<Set<IPoint2D>>(); 
  //			for (IPoint2D point: getPoints()) {
  //				inner_loop: for (Set<IPoint2D> cluster : _endPointsClusters) {
  //					if (point.distance(cluster.iterator().next()) <= MAX_DISTANCE_BETWEEN_CLUSTER_POINTS) {
  //						cluster.add(point);
  //						break inner_loop;
  //					}
  //				}
  //				TreeSet<IPoint2D> cluster = new TreeSet<IPoint2D>();
  //				cluster.add(point);
  //				_endPointsClusters.add(cluster);
  //			}
  //		}
  //		return _endPointsClusters;
  //	}
  //
  //	public List<Set<IPoint2D>> getEndPointsMultiClusters() {
  //		List<Set<IPoint2D>> result = new ArrayList<Set<IPoint2D>>();
  //		for (Set<IPoint2D> cluster: getEndPointsClusters()) {
  //			if (cluster.size()>1)
  //				result.add(cluster);
  //		}
  //		return result;
  //	}
  //	
  //	@Override
  //	public Object clone() {
  //		try {
  //			return super.clone();
  //		} catch (CloneNotSupportedException e) {
  //			e.printStackTrace();
  //			return null;
  //		}
  //	}
  //	
  //	/** No filtering in first version */
  //	@Override
  //	public Polygon replacePointsInMap(Map<IPoint2D,IPoint2D> pointReplacementMap,
  //			AnnotatedShapeImplementation annotatedShape) {
  //		Polygon replacedPolygon = new Polygon(annotatedShape);
  //		replacedPolygon.setup();
  //	    for (CLine line : _lines) {
  //	    	CLine newLine = line.replacePointsInMap(pointReplacementMap,annotatedShape);
  //			if (!newLine.isPoint()) {
  //				replacedPolygon.addLine(newLine);
  //			}
  //		}
  //		Set<Object> annotationForOldPolygon = null; 
  //		if (annotatedShape != null)
  //			annotationForOldPolygon = annotatedShape.getAnnotationForShapes(this);
  //		if (annotationForOldPolygon != null) {
  //			annotatedShape.putAllAnnotation(replacedPolygon, annotationForOldPolygon); 
  //		}
  //		return replacedPolygon;
  //	}
  //
  //	@Override
  //	public IPoint2D getCenter() {
  //		return _bBox.getCenter();
  //	}
  //
  //	@Override
  //	public Double getDiameter() {
  //		return getBBox().getDiameter();
  //	}
  //	
  //	public void setPolygonImprovers(List<Improver<Polygon>> improvers) {
  //		_polygonImprovers = improvers;
  //	}
  //
  //	/** To have the same interface as MultiLinePolygon */
  //	public CLine addIndependentLine(IPoint2D point1, IPoint2D point2) {
  //		return addLine(point1, point2);
  //	}
  //
  //    /** Most of the time this should not be used use the version taking input 
  //     * points instead */
  //    protected CLine addIndependentLine(CLine line) {
  //        return addLine(line);
  //    }
  //    
  //	/** To have the same interface as MultiLinePolygon */
  //	public Set<CLine> getIndependentLines() {
  //		return getLines();
  //	}
  //
  //	/** To have the same interface as MultiLinePolygon 
  //	 * returns null 
  //	 * since this and the independent lines are supposed to be all the lines
  //	 * */
  //	public List<MultiLine> getMultiLines() {
  //		return null;
  //	}
  //	
  //	public <Element> Collection<Element> filter(IFilter<Polygon, Element> filterObject) {
  //		filterObject.setParent(this);
  //		return filterObject.filter();
  //	}

  //	public <Element> Collection<Element> filter(String inputExpression) {
  //		IFilter<Polygon, Element> filterObject = FilterFactory.makeTreeFilter(inputExpression);
  //		filterObject.setParent(this);
  //		return filterObject.filter();
  //	}

  //	public Int getHoleCount() {
  //		return getLines().size() + 1 - getPoints().size();
  //	}

  //	@Override
  //	public String toString() {
  //		StringBuffer result = new StringBuffer();
  //        internalInfo(result);
  //        printAnnotation(result);
  //		return result.toString();
  //	}

  //    public String internalInfo(StringBuffer sb) {
  //        sb.append("\n\n=====Class: ").append(getClass().getSimpleName()).append("=====\n");
  //        if (null != _bBox)
  //            sb.append(_bBox.toString());
  //        if (null != _currentMultiLine)
  //            _currentMultiLine.internalInfo(sb);
  //        else {
  //            sb.append("Lines:\n");
  //            for (CLine line: getLines()) {
  //                sb.append(line);
  //            }
  //            sb.append("\nPoints:\n");
  //            for (IPoint2D point: getPoints()) {
  //                sb.append(point);
  //            }
  //        }
  //        return sb.toString();
  //    }

  //    public String printAnnotation(StringBuffer result) {
  //		result.append("\nAnnotations:\n");
  //		Map<Object, Set<GeometricShape2D>> map = getAnnotatedShape().getMap();
  //		for (Entry<Object, Set<GeometricShape2D>> entry: map.entrySet())
  //			result.append(entry.getKey() +":\n" + entry.getValue() + "\n");
  //		result.append("\naspectRatio: " + getBBox().getAspectRatio());
  //		return result.toString();
  //    }

  //	public Double getPerimeter() {
  //		return _perimeter;
  //	}

  //	public void setPerimeter(Double perimeter) {
  //		_perimeter = perimeter;
  //	}

}