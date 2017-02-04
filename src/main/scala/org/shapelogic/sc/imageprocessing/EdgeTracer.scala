package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants.DOWN
import org.shapelogic.sc.util.Constants.LEFT
import org.shapelogic.sc.util.Constants.RIGHT
import org.shapelogic.sc.util.Constants.UP

//import org.shapelogic.sc.color.ColorFactory
//import org.shapelogic.sc.color.IColorDistanceWithImage;
//import org.shapelogic.sc.imageutil.SLImage;
import org.shapelogic.sc.polygon.CPointInt;
import org.shapelogic.sc.polygon.Polygon;
import org.shapelogic.sc.util.Constants;
import org.shapelogic.sc.image.BufferImage

/**
 * Edge Tracer. <br />
 *
 * The first version is based on Wand from ImageJ 1.38.<br />
 *
 * It traces with a 2 x 2 square that put the top left pixels inside the
 * particle and the bottom right outside.<br />
 *
 * Might be replaced with a version that has all the pixels inside.<br />
 *
 * @author Sami Badawi
 *
 */
class EdgeTracer(image: BufferImage[Byte], _maxDistance: Double) extends IEdgeTracer {

//  var _colorDistanceWithImage: IColorDistanceWithImage = null
  lazy val width: Int = image.width
  var height: Int = image.height

  //	private boolean _traceCloseToColor;
  //	private boolean[] _dirs = new boolean[Constants.DIRECTIONS_AROUND_POINT]; 
  //	public static final int STEP_SIZE_FOR_4_DIRECTIONS = 2;
  //	
  //	/** Constructs a Wand object from an ImageProcessor. */
  //	public EdgeTracer(SLImage image, int referenceColor, double maxDistance, boolean traceCloseToColor) {
  //		_colorDistanceWithImage = ColorFactory.makeColorDistanceWithImage(image);
  //		_colorDistanceWithImage.setReferenceColor(referenceColor);
  //		_maxDistance = maxDistance;
  //		_traceCloseToColor = traceCloseToColor;
  //		width = image.getWidth();
  //		height = image.getHeight();
  //	}
  //	
  //	/** Use XOR to either handle colors close to reference color or far away. */
  //	private boolean inside(int x, int y) {
  //		if (x < 0 || y < 0)
  //			return false;
  //		if (width <= x || height <= y)
  //			return false;
  //		return _traceCloseToColor ^ (_maxDistance < _colorDistanceWithImage.distanceToReferenceColor(x, y));
  //	}
  //
  //	/** Traces the boundary of an area of uniform color, where
  //		'startX' and 'startY' are somewhere inside the area. 
  //		A 16 entry lookup table is used to determine the
  //		direction at each step of the tracing process. */
  def autoOutline(startX: Int, startY: Int): Polygon = {
    null
    //		int x = startX;
    //		int y = startY;
    //		//Find top point inside
    //		do {
    //			y--;
    //		} while (inside(x,y));
    //		y++;
    //		//Find leftmost top point inside
    //		do {
    //			x--;
    //		} while (inside(x,y));
    //		x++;
    //		return traceEdge(x, y, 2);
    //	}
    //	
    //	int nextDirection(int x, int y, int lastDirection, boolean clockwise) {
    //		boolean[] directions = makeDirections(x, y, true);
    //		int lastDirectionReleativeCurrent = lastDirection + Constants.DIRECTIONS_AROUND_POINT/2;
    //		int stepSize = STEP_SIZE_FOR_4_DIRECTIONS;
    //		for (int i=2; i <= Constants.DIRECTIONS_AROUND_POINT; i+=stepSize) {
    //			int step = i;
    //			if (!clockwise)
    //				step = Constants.DIRECTIONS_AROUND_POINT -i;
    //			int real_direction = (lastDirectionReleativeCurrent + step) 
    //			% Constants.DIRECTIONS_AROUND_POINT;
    //			//Return first point that is inside
    //			if (directions[real_direction])
    //				return real_direction;
    //		}
    //		return -1; //Not found
  }
  //
  //	private boolean[] makeDirections(int x, int y, boolean only4points) {
  //		int stepSize = 1;
  //		if (only4points)
  //			stepSize = STEP_SIZE_FOR_4_DIRECTIONS;
  //		for (int i=0; i < Constants.DIRECTIONS_AROUND_POINT; i+=stepSize) {
  //			_dirs[i] = inside(x + Constants.CYCLE_POINTS_X[i], y + Constants.CYCLE_POINTS_Y[i]);
  //		}
  //		return _dirs;
  //	}
  //		
  //	Polygon traceEdge(int xstart, int ystart, int startingDirection) {
  //		Polygon polygon = new Polygon();
  //		polygon.startMultiLine();
  //		ChainCodeHandler chainCodeHandler = new ChainCodeHandler(polygon.getAnnotatedShape());
  //		chainCodeHandler = new ChainCodeHandler(polygon.getAnnotatedShape());
  //		chainCodeHandler.setup();
  //		chainCodeHandler.setMultiLine(polygon.getCurrentMultiLine());
  //		chainCodeHandler.setFirstPoint(new CPointInt(xstart,ystart));
  //		int x = xstart;
  //		int y = ystart;
  //		startingDirection = BaseVectorizer.oppesiteDirection((byte)nextDirection(x,y,startingDirection-2, false));
  //		int direction = startingDirection;
  //		int count = 0;
  //		do {
  //			count++;
  //			direction = nextDirection(x,y,direction, true);
  //			if (-1 == direction)
  //				break;
  //			switch (direction) {
  //				case UP:
  //					y = y-1;
  //					break;
  //				case DOWN:
  //					y = y + 1;
  //					break;
  //				case LEFT:
  //					x = x-1;
  //					break;
  //				case RIGHT:
  //					x = x + 1;
  //					break;
  //			}
  //			//If the chain becomes too long just give up
  //			if (!chainCodeHandler.addChainCode((byte)direction))
  //				break;
  ////		} while ((x!=xstart || y!=ystart));
  //		//Original clause causes termination problems
  //		} while ((x!=xstart || y!=ystart || direction!=startingDirection));
  //		chainCodeHandler.getValue();
  //		polygon.setPerimeter(chainCodeHandler.getPerimeter());
  //		polygon.getValue();
  //		polygon.getBBox().add(chainCodeHandler._bBox);
  //		return polygon;
  //	}
}
