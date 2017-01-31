package org.shapelogic.sc.imageprocessing

import spire.implicits._
import org.shapelogic.sc.image.BufferImage
import java.awt.Rectangle
import scala.collection.mutable.ArrayBuffer
import org.shapelogic.sc.color.ValueAreaFactory
import org.shapelogic.sc.color.IColorAndVariance

/**
 * Image segmentation
 * Ported from ShapeLogic Java
 */
class SBSegmentation(_slImage: BufferImage[Byte], roi: Option[Rectangle]) extends Iterator[Seq[SBPendingVertical]] {

  val _vPV: ArrayBuffer[SBPendingVertical] = new ArrayBuffer()
  /** Dimensions of ROI. */
  val _min_x: Int = roi.map(_.x).getOrElse(0)
  val _max_x: Int = roi.map(_.width).getOrElse(_slImage.width - 1)
  val _min_y: Int = roi.map(_.y).getOrElse(0)
  val _max_y: Int = roi.map(_.height).getOrElse(_slImage.height - 1)

  val _pixelCompare: SBPixelCompare = null //XXX fix

  var _segmentAreaFactory: ValueAreaFactory = null
  var _currentSegmentArea: IColorAndVariance = null

  var _status: String = ""
  var _slowTestMode: Boolean = false;
  var _farFromReferenceColor: Boolean = false;

  var _nextX: Int = _max_x
  var _nextY: Int = _min_y - 1

  var _currentList: ArrayBuffer[SBPendingVertical] = new ArrayBuffer()
  var _currentArea: Int = 0
  var _referenceColor: Int = 0
  var _paintColor: Int = -1

  /**
   * Conviniens method to get the offset from the start of the image
   * array to the first pixel of a line, at the edge of the image
   * not the edge of to ROI.
   *
   * @param y
   * @return
   */
  def offsetToLineStart(y: Int): Int = {
    val width: Int = _slImage.width
    val offset = y * width
    offset
  }

  def pointToIndex(x: Int, y: Int): Int = {
    _slImage.stride * y + x
  }

  /**
   * Given a point find the longest line vertical line similar to the chosen colors.
   * <br />
   * If the start point does not match return null.<br />
   *
   * @param x
   * @param y
   * @return this does not contain any up or down information
   */
  def expandSBPendingVertical(lineIn: SBPendingVertical): SBPendingVertical = {
    val offset = offsetToLineStart(lineIn.y)
    if (!_pixelCompare.newSimilar(offset + lineIn.xMin) ||
      !_pixelCompare.newSimilar(offset + lineIn.xMax))
      return lineIn; // this should never happen
    var i_low: Int = lineIn.xMin - 1
    var stopMin = false
    while (_min_x <= i_low) {
      if (!_pixelCompare.newSimilar(offset + i_low)) {
        i_low += 1
        stopMin = true
      }
      i_low -= 1
    }
    var i_high: Int = lineIn.xMax + 1
    var stopMax = false
    while (_max_x >= i_high && !stopMax) {
      if (!_pixelCompare.newSimilar(offset + i_high)) {
        i_high -= 1
        stopMax = true
      }
      i_high += 1
    }
    val x1 = Math.max(_min_x, i_low)
    val x2 = Math.min(_max_x, i_high)
    val newLine: SBPendingVertical = new SBPendingVertical(x1, x2, lineIn.y, lineIn.searchUp)
    newLine
  }

  def segmentAll(): Unit = {
    cfor(_min_x)(_ <= _max_x, _ + 1) { x =>
      cfor(_min_y)(_ <= _max_y, _ + 1) { y =>
        if (!_pixelCompare.isHandled(pointToIndex(x, y))) {
          _pixelCompare.grabColorFromPixel(x, y);
          segment(x, y, false);
        }
      }
    }
  }

  /**
   * Set every pixel that has the input color, regardless of connectivity.<br />
   *
   * @param color
   */
  def segmentAll(color: Int): Unit = {
    _referenceColor = color
    _pixelCompare.setCurrentColor(_referenceColor)
    cfor(_min_y)(_ <= _max_y, _ + 1) { y =>
      var lineStart = pointToIndex(0, y)
      cfor(_min_x)(_ <= _max_x, _ + 1) { x =>
        var index = lineStart + x
        if (!_pixelCompare.isHandled(index) &&
          _pixelCompare.similar(index)) {
          segment(x, y, true)
        }
      }
    }
  }

  /**
   * Start segmentation by selecting a point
   *
   * Use the color of that point at your goal color
   *
   * @param x
   * @param y
   */
  def segment(x: Int, y: Int, useReferenceColor: Boolean): Unit = {
    _currentList = new ArrayBuffer()
    _currentArea = 0
    var index = pointToIndex(x, y)
    var effectiveColor = _referenceColor;
    if (!useReferenceColor)
      effectiveColor = _pixelCompare.getColorAsInt(index);
    if (_segmentAreaFactory != null)
      _currentSegmentArea = _segmentAreaFactory.makePixelArea(x, y, effectiveColor)
    if (!_pixelCompare.newSimilar(index)) {
      _status = "First pixel did not match. Segmentation is empty.";
      return
    }
    val firstLine: SBPendingVertical = expandSBPendingVertical(new SBPendingVertical(x, y))
    if (firstLine == null)
      return ;
    storeLine(firstLine)
    storeLine(SBPendingVertical.opposite(firstLine));
    val maxIterations = 1000 + _slImage.pixelCount / 10
    if (_vPV.size != 0) {
      cfor(1)(_ <= maxIterations, _ + 1) { i =>
        val obj = _vPV.last
        _vPV.remove(_vPV.size - 1)
        val curLine: SBPendingVertical = obj.asInstanceOf[SBPendingVertical]
        fullLineTreatment(curLine)
      }
    }
    //        if (useReferenceColor)
    //            paintSegment(_currentList,_paintColor);
    _pixelCompare.getNumberOfPixels();
  }
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
  /** line is at the edge of image and pointing away from the center	 */
  def atEdge(curLine: SBPendingVertical): Boolean = {
    if (curLine.y == _max_y && curLine.searchUp)
      return true;
    if (curLine.y == _min_y && !curLine.searchUp)
      return true;
    false;
  }

  def isExpandable(curLine: SBPendingVertical): Boolean = {
    val offset = offsetToLineStart(curLine.y)
    if (_min_x <= curLine.xMin - 1) {
      val indexLeft = offset + curLine.xMin - 1;
      if (_pixelCompare.newSimilar(indexLeft)) {
        return true
      }
    }
    if (_max_x >= curLine.xMax + 1) {
      val indexRight = offset + curLine.xMax + 1;
      if (_pixelCompare.newSimilar(indexRight)) {
        return true
      }
    }
    false
  }

  /** If the whole line is handled */
  def isHandled(curLine: SBPendingVertical): Boolean = {
    val offset = offsetToLineStart(curLine.y);
    cfor(curLine.xMin)(_ <= curLine.xMax, _ + 1) { i =>
      if (!_pixelCompare.isHandled(offset + i)) {
        return false;
      }
    }
    true
  }

  /**
   * Call action on the line itself and then setHandled, so it will not
   * be run again.
   *
   * @param curLine, containing the current line, that is already found
   */
  def handleLine(curLine: SBPendingVertical): Unit = {
    val offset = offsetToLineStart(curLine.y);
    var y = curLine.y
    var stop = false
    cfor(curLine.xMin)(!stop && _ <= curLine.xMax, _ + 1) { i =>
      if (_pixelCompare.isHandled(offset + i))
        stop = true
      else {
        if (!_pixelCompare.isHandled(offset + i)) {
          _pixelCompare.action(offset + i)
          _currentArea += 1
          _pixelCompare.setHandled(offset + i)
          if (_currentSegmentArea != null)
            _currentSegmentArea.putPixel(i, y, _pixelCompare.getColorAsInt(offset + i))
        }
      }
    }
  }

  /**
   * After handling a line continue in the same direction.
   *
   * @param curLine
   */
  def handleNextLine(curLine: SBPendingVertical): Unit = {
    if (atEdge(curLine))
      return ;
    var insideSimilar = false;
    var lowX = 0;
    var direction = -1; //down
    if (curLine.searchUp)
      direction = 1;
    val yNew = curLine.y + direction;
    if (!(_min_y <= yNew && yNew <= _max_y))
      return ;
    var offset = offsetToLineStart(yNew);
    cfor(curLine.xMin)(_ <= curLine.xMax, _ + 1) { i =>
      val curSimilar = _pixelCompare.newSimilar(offset + i)
      if (!insideSimilar && curSimilar) { //enter
        lowX = i;
        insideSimilar = true;
      } else if (insideSimilar && !curSimilar) { //leave
        val newLine = new SBPendingVertical(lowX, i - 1, yNew,
          curLine.searchUp);
        storeLine(newLine);
        insideSimilar = false;
      }
    }
    if (insideSimilar) {
      val newLine = new SBPendingVertical(lowX, curLine.xMax, yNew,
        curLine.searchUp);
      storeLine(newLine);
    }
  }

  def fullLineTreatment(curLineIn: SBPendingVertical): Unit = {
    if (curLineIn == null)
      return
    var curLine = curLineIn
    if (isExpandable(curLine)) {
      var expanded: SBPendingVertical = expandSBPendingVertical(curLine)
      //check that new line is still good
      if (!_slowTestMode || !checkLine(expanded)) {
        expanded = expandSBPendingVertical(curLine)
      }
      curLine = expanded
      storeLine(SBPendingVertical.opposite(expanded))
    }
    handleLine(curLine)
    try {
      handleNextLine(curLine);
    } catch {
      case ex: Throwable => {
        ex.printStackTrace()
      }
    }
  }

  def getSLImage(): BufferImage[Byte] = {
    return _slImage;
  }
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
  //			Int areas = _segmentAreaFactory.getStore().size();
  //			status += "Numbers of areas = " + areas;
  //            if (0 < areas)
  //    			status += "\nPixels per area = " + _slImage.getPixelCount() / areas; 
  //            else 
  //                status += ", segmentation was not run.";
  //		}
  //		return status;
  //	} 
  //
  /** Make sure that every point on curLine is similar the the chosen color */
  def checkLine(curLine: SBPendingVertical): Boolean =
    {
      val offset = offsetToLineStart(curLine.y)
      var problem = false
      var stop = false
      cfor(curLine.xMin)(_ <= curLine.xMax & !stop, _ + 1) { i =>
        if (_pixelCompare.similar(offset + i))
          stop = true
        else {
          val handledBefore = _pixelCompare.similar(offset + i); //for debugging
          problem = true;
        }
      }
      return !problem;
    }

  def storeLine(curLine: SBPendingVertical): Unit = {
    if (_slowTestMode && !checkLine(curLine))
      checkLine(curLine); //for debugging
    _currentList.+=(curLine)
    _vPV.append(curLine)
  }

  def setSegmentAreaFactory(areaFactory: ValueAreaFactory): Unit = {
    _segmentAreaFactory = areaFactory;
  }

  def getSegmentAreaFactory(): ValueAreaFactory = {
    _segmentAreaFactory
  }

  def setMaxDistance(maxDistance: Int): Unit = {
    _pixelCompare.setMaxDistance(maxDistance)
  }

  def isFarFromReferencColor(): Boolean = {
    return _farFromReferenceColor;
  }

  def setFarFromReferencColor(farFromColor: Boolean): Unit = {
    _farFromReferenceColor = farFromColor;
    _pixelCompare.setFarFromReferencColor(farFromColor);
  }

  override def hasNext(): Boolean = {
    if (_nextY < _max_y)
      true
    else if (_nextY == _max_y && _nextX < _max_x)
      true
    else
      false;
  }

  override def next(): Seq[SBPendingVertical] = {
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
  //    public void setReferenceColor(Int referenceColor) {
  //        _referenceColor = referenceColor;
  //    }
  //
  //    public Int getCurrentArea() {
  //        return _currentArea;
  //    }
  //
  //    public void paintSegment(ArrayList<SBPendingVertical> lines, Int paintColor) {
  //        if (null != lines) {
  //            for (SBPendingVertical line: lines) {
  //                for (Int i = line.xMin; i <= line.xMax; i++ ) {
  //                    _slImage.set(i, line.y, paintColor);
  //                }
  //            }
  //        }
  //    }
  //
  //    public Boolean pixelIsHandled(Int index) {
  //        return _pixelCompare.isHandled(index);
  //    }
  //  
}