package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.image.BufferImage
import java.awt.Rectangle
import scala.collection.mutable.ArrayBuffer

/**
 * Image segmentation
 * Ported from ShapeLogic Java
 */
class SBSegmentation(_slImage: BufferImage[Byte], roi: Option[Rectangle]) extends Iterator[Seq[SBPendingVertical] ] 
{

  val _vPV: ArrayBuffer[SBPendingVertical] = new ArrayBuffer()
	/** Dimensions of ROI. */
	val _min_x: Int = roi.map(_.x).getOrElse(0)
	val _max_x: Int = roi.map(_.width).getOrElse(_slImage.width)
	val _min_y: Int = roi.map(_.y).getOrElse(0)
	val _max_y: Int = roi.map(_.height).getOrElse(_slImage.width)
	
//	private SBPixelCompare _pixelCompare;
//	
//	protected ValueAreaFactory _segmentAreaFactory;
//	protected IColorAndVariance _currentSegmentArea; 
//
//	private String _status = "";
//	private boolean _slowTestMode = false;
//	private boolean _farFromReferenceColor = false;
//		
  var _nextX: Int = 0;
  var _nextY: Int = 0;
  var _currentList: Seq[SBPendingVertical]  = Seq()
//    private int _currentArea;
//	private int _referenceColor;
//	private int _paintColor = -1;
//
//	public SBSegmentation() {
//		_vPV = new ArrayList<SBPendingVertical>();
//	}
//	
//	/** Conviniens method to get the offset from the start of the image
//	 * array to the first pixel of a line, at the edge of the image
//	 * not the edge of to ROI.
//	 * 
//	 * @param y
//	 * @return
//	 */
//	private int offsetToLineStart(int y)
//	{
//		int width = _slImage.getWidth();
//		int offset = y * width;		
//		return offset;
//	}
//
//	int pointToIndex(int x, int y){
//		return _slImage.getLineStride() * y + x;
//	}
//	
//	/** Given a point find the longest line vertical line similar to the chosen colors. 
//	 * <br />
//	 * If the start point does not match return null.<br /> 
//	 * 
//	 * @param x
//	 * @param y
//	 * @return this does not contain any up or down information
//	 */
//	private SBPendingVertical expandSBPendingVertical(SBPendingVertical lineIn)
//	{
//		int offset = offsetToLineStart(lineIn.y);
//		if (!_pixelCompare.newSimilar(offset + lineIn.xMin) ||
//			!_pixelCompare.newSimilar(offset + lineIn.xMax))
//			return lineIn; // this should never happen
//		int i_low;
//		for (i_low = lineIn.xMin-1; _min_x <= i_low; i_low--) {
//			if (!_pixelCompare.newSimilar(offset + i_low)) {
//				i_low++;
//				break;
//			}
//		}
//		int i_high;
//		for (i_high = lineIn.xMax+1; _max_x >= i_high; i_high++) {
//			if (!_pixelCompare.newSimilar(offset + i_high)) {
//				i_high--;
//				break;
//			}
//		}
//		int x1 = Math.max(_min_x,i_low);
//		int x2 = Math.min(_max_x,i_high);
//		SBPendingVertical newLine = new SBPendingVertical(x1,x2,lineIn.y, lineIn.isSearchUp());
//		return newLine;
//	}
//
//	public void segmentAll()
//	{
//		for (int x=_min_x;x<=_max_x;x++) {
//			for (int y=_min_y;y<=_max_y;y++) {
//				if (!_pixelCompare.isHandled(pointToIndex(x, y))) {
//					_pixelCompare.grabColorFromPixel(x, y);
//					segment(x, y, false);
//				}
//			}
//		}
//	}
//	
//    /** Set every pixel that has the input color, regardless of connectivity.<br />
//     * 
//     * @param color
//     */
//	public void segmentAll(int color)
//	{
//        _referenceColor = color;
//        _pixelCompare.setCurrentColor(_referenceColor);
//		for (int y=_min_y;y<=_max_y;y++) {
//			int lineStart = pointToIndex(0, y);;
//    		for (int x=_min_x;x<=_max_x;x++) {
//                int index = lineStart + x;
//				if (!_pixelCompare.isHandled(index) &&
//                        _pixelCompare.similar(index)) {
//					segment(x, y, true);
//				}
//			}
//		}
//	}
//	
//	/** Start segmentation by selecting a point
//	 * 
//	 * Use the color of that point at your goal color
//	 * 
//	 * @param x
//	 * @param y
//	 */
//	public void segment(int x, int y, boolean useReferenceColor)
//	{
//        _currentList = new ArrayList();
//        _currentArea = 0;
//		int index = pointToIndex(x,y);
//        int effectiveColor = _referenceColor;
//        if (!useReferenceColor)
//            effectiveColor = _pixelCompare.getColorAsInt(index);
//		if (_segmentAreaFactory != null)
//			_currentSegmentArea = _segmentAreaFactory.makePixelArea(x,y, effectiveColor);
//		if (!_pixelCompare.newSimilar(index)){
//			_status = "First pixel did not match. Segmentation is empty.";
//			return;
//		}
//		SBPendingVertical firstLine = expandSBPendingVertical(new SBPendingVertical(x,y));
//		if (firstLine == null)
//			return;
//		storeLine(firstLine);
//		storeLine(SBPendingVertical.opposite(firstLine));
//		final int maxIterations = 1000 + _slImage.getPixelCount()/10;
//		int i;
//		for (i =1; i <= maxIterations; i++) {
//			if (_vPV.size() == 0) 
//				break;
//			Object obj = _vPV.remove(_vPV.size()-1);
//			SBPendingVertical curLine = (SBPendingVertical) obj;
//			fullLineTreatment(curLine);
//		}
////        if (useReferenceColor)
////            paintSegment(_currentList,_paintColor);
//		_pixelCompare.getNumberOfPixels();
//	}
//	
//	
//	/** line is at the edge of image and pointing away from the center	 */
//	public void init()
//	{
//		Rectangle r = _slImage.getRoi();
//		
//		if (r == null) {
//			_min_x = 0;
//			_max_x = _slImage.getWidth()-1;
//			_min_y = 0;
//			_max_y = _slImage.getHeight()-1;
//		}
//		else {
//			_min_x = r.x;
//			_max_x = r.x + r.width -1;
//			_min_y = r.y;
//			_max_y = r.y + r.height -1;
//		}
//	}
//
//	/** line is at the edge of image and pointing away from the center	 */
//	boolean atEdge(SBPendingVertical curLine)
//	{
//		if (curLine.y == _max_y && curLine.isSearchUp())
//			return true;
//		if (curLine.y == _min_y && !curLine.isSearchUp())
//			return true;
//		return false;
//	}
//
//	boolean isExpandable(SBPendingVertical curLine)
//	{
//		int offset = offsetToLineStart(curLine.y);		
//		if (_min_x <= curLine.xMin-1) {
//			int indexLeft = offset + curLine.xMin-1;
//			if (_pixelCompare.newSimilar(indexLeft)) {
//				return true;
//			}
//		}
//		if (_max_x >= curLine.xMax+1) {
//			int indexRight = offset + curLine.xMax+1;
//			if (_pixelCompare.newSimilar(indexRight)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	/** If the whole line is handled */
//	boolean isHandled(SBPendingVertical curLine)
//	{
//		int offset = offsetToLineStart(curLine.y);		
//		for (int i = curLine.xMin; i <= curLine.xMax; i++) {
//			if (!_pixelCompare.isHandled(offset + i)) {
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	/** Call action on the line itself and then setHandled, so it will not 
//	 * be run again.
//	 * 
//	 * @param curLine, containing the current line, that is already found
//	 */
//	void handleLine(SBPendingVertical curLine)
//	{
//		int offset = offsetToLineStart(curLine.y);
//		int y = curLine.y;
//		for (int i = curLine.xMin; i <= curLine.xMax; i++) {
//			if (_pixelCompare.isHandled(offset + i))
//				continue;
//			if (!_pixelCompare.isHandled(offset + i)) {
//				_pixelCompare.action(offset + i);
//                _currentArea++;
//				_pixelCompare.setHandled(offset + i);
//				if (_currentSegmentArea != null)
//					_currentSegmentArea.putPixel(i,y,_pixelCompare.getColorAsInt(offset + i));
//			}
//		}
//	}
//
//	/** After handling a line continue in the same direction.
//	 * 
//	 * @param curLine
//	 */
//	void handleNextLine(SBPendingVertical curLine)
//	{
//		if (atEdge(curLine))
//			return;
//		boolean insideSimilar = false;
//		int lowX=0;
//		int direction = -1; //down
//		if (curLine.isSearchUp())
//			direction = 1;
//		int yNew = curLine.y+direction;
//		if (!(_min_y <= yNew && yNew <= _max_y))
//			return;
//		int offset = offsetToLineStart(yNew);
//		for (int i = curLine.xMin; i <= curLine.xMax; i++) {
//			boolean curSimilar = _pixelCompare.newSimilar(offset + i);
//			if (!insideSimilar && curSimilar) { //enter
//				lowX = i;
//				insideSimilar = true;
//			}
//  			else if (insideSimilar && !curSimilar) { //leave
//				SBPendingVertical newLine = new SBPendingVertical(lowX,i-1,yNew,
//						curLine.isSearchUp());
//				storeLine(newLine);
//				insideSimilar = false;
//			}
//		}
//		if (insideSimilar) {
//			SBPendingVertical newLine = new SBPendingVertical(lowX,curLine.xMax,yNew,
//					curLine.isSearchUp());
//			storeLine(newLine);
//		}
//	}
//
//	void fullLineTreatment(SBPendingVertical curLine)
//	{
//		if (curLine == null)
//			return;
//		if (isExpandable(curLine)) {
//			SBPendingVertical expanded = expandSBPendingVertical(curLine);
//			//check that new line is still good
//			if (!_slowTestMode || !checkLine(expanded)) {
//				expanded = expandSBPendingVertical(curLine);
//			}
//			curLine = expanded;
//			storeLine(SBPendingVertical.opposite(expanded));
//		}
//		handleLine(curLine);
//		try {
//			handleNextLine(curLine);
//		}
//		catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//	
//	/**
//	 * @param ip The ip to set.
//	 */
//	public void setSLImage(SLImage ip) {
//		this._slImage = ip;
//        Rectangle roi = ip.getRoi();
//        if (roi != null) {
//            _min_x = roi.x;
//            _max_x = roi.x + roi.width;
//            _min_y = roi.y;
//            _max_y = roi.x + roi.width;
//        }
//        else {
//            _min_x = 0;
//            _max_x = ip.getWidth() - 1;
//            _min_y = 0;
//            _max_y = ip.getHeight() - 1;
//        }
//        _nextX = _max_x;
//        _nextY = _min_y-1;
//	}
//
//	public SLImage getSLImage() {
//		return _slImage;
//	}
//	
//	/**
//	 * @param pixelCompare The pixelCompare to set.
//	 */
//	public void setPixelCompare(SBPixelCompare pixelCompare) {
//		this._pixelCompare = pixelCompare;
//	}
//	/**
//	 * @return Returns the status.
//	 */
//	public String getStatus() {
//		if (_status == null || "".equals(_status) ) 
//			_status = findStatus();
//		return _status;
//	}
//	
//	public String findStatus() {
//		String status = "";
//		if (_segmentAreaFactory != null) {
//			int areas = _segmentAreaFactory.getStore().size();
//			status += "Numbers of areas = " + areas;
//            if (0 < areas)
//    			status += "\nPixels per area = " + _slImage.getPixelCount() / areas; 
//            else 
//                status += ", segmentation was not run.";
//		}
//		return status;
//	} 
//
//	/** Make sure that every point on curLine is similar the the chosen color */
//	boolean checkLine(SBPendingVertical curLine)
//	{
//		int offset = offsetToLineStart(curLine.y);
//		boolean problem = false;
//		for (int i = curLine.xMin; i <= curLine.xMax; i++) {
//			if (_pixelCompare.similar(offset + i))
//				continue;
//			else {
//				boolean handledBefore = _pixelCompare.similar(offset + i);//for debugging
//				problem = true;
//			}
//		}
//		return ! problem;
//	}
//	
//	void storeLine(SBPendingVertical curLine){
//		if (_slowTestMode && !checkLine(curLine))
//			checkLine(curLine); //for debugging
//        _currentList.add(curLine);
//		_vPV.add(curLine);
//	}
//
//	public void setSegmentAreaFactory(ValueAreaFactory areaFactory) {
//		_segmentAreaFactory = areaFactory;
//	}
//
//	public ValueAreaFactory getSegmentAreaFactory() {
//		return _segmentAreaFactory;
//	}
//
//    public void setMaxDistance(int maxDistance) {
//        _pixelCompare.setMaxDistance(maxDistance);
//    }
//
//    public boolean isFarFromReferencColor() {
//		return _farFromReferenceColor;
//	}
//
//	public void setFarFromReferencColor(boolean farFromColor) {
//		_farFromReferenceColor = farFromColor;
//		_pixelCompare.setFarFromReferencColor(farFromColor);
//	}
//
    override def hasNext(): Boolean = {
        if (_nextY < _max_y)  
          true
        else if (_nextY == _max_y && _nextX < _max_x)  
          true
        else 
          false;
    }

    override def next(): Seq[SBPendingVertical] =  {
      return Seq()
//        while (true) {
//            if (!hasNext())
//                return null;
//            if (_nextX <  _max_x)
//                _nextX++;
//            else {
//                _nextY++;
//                _nextX = _min_x;
//            }
//            if (!_pixelCompare.isHandled(pointToIndex(_nextX, _nextY) ) ) {
//                segment(_nextX, _nextY, true);
//                return _currentList;
//            }
//        }
    }

//    public void remove() {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//
//    public void setReferenceColor(int referenceColor) {
//        _referenceColor = referenceColor;
//    }
//
//    public int getCurrentArea() {
//        return _currentArea;
//    }
//
//    public void paintSegment(ArrayList<SBPendingVertical> lines, int paintColor) {
//        if (null != lines) {
//            for (SBPendingVertical line: lines) {
//                for (int i = line.xMin; i <= line.xMax; i++ ) {
//                    _slImage.set(i, line.y, paintColor);
//                }
//            }
//        }
//    }
//
//    public boolean pixelIsHandled(int index) {
//        return _pixelCompare.isHandled(index);
//    }
//  
}