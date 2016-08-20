package jp.ac.ut.csis.pflow.routing2.loader;

import static jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader.DEFAULT_BUFFER_SIZE;
import static jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader.APPROX_1KM;

import java.awt.geom.Rectangle2D;

/**
 * Class for query condition of network data loader
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class QueryCondition { 
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** rectangle region {x0,y0,x1,y1}	*/	private double[] _rect;
	/** buffer size in meter			*/	private double   _buffer;
	/** flag for line geometry			*/	private boolean  _needGeom;
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * initialization
	 */
	public QueryCondition() { 
		this(null,DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 */
	public QueryCondition(double[] rect,double bufSize) { 
		this(rect,bufSize,false);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @param needGeom flag for  line geometry
	 */
	public QueryCondition(double[] rect,double bufSize,boolean needGeom) { 
		_rect     = rect;
		_buffer   = bufSize;
		_needGeom = needGeom;
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	
	/**
	 * get rectangle region without buffer
	 * @return rectangle region
	 */
	public Rectangle2D getRects() { 
		// check if available
		if (_rect == null) { return null; }
		
		// calculate position of each corner
		double xmin = Math.min(_rect[0],_rect[2]);
		double xmax = Math.max(_rect[0],_rect[2]);
		double ymin = Math.min(_rect[1],_rect[3]);
		double ymax = Math.max(_rect[1],_rect[3]);

		// create bounding rectangle
		return new Rectangle2D.Double(xmin,ymin,xmax-xmin,ymax-ymin);
	}
	
	/**
	 * get bounding rectangle for extraction
	 * @return bounding rectangle
	 */
	public Rectangle2D getBounds() {
		// check if available
		if (_rect == null) { return null; }
		
		// calculate position of each corner
		double buf  = _buffer * APPROX_1KM; // 0.012d / 1000d;
		double xmin = Math.min(_rect[0],_rect[2]) - buf;
		double xmax = Math.max(_rect[0],_rect[2]) + buf;
		double ymin = Math.min(_rect[1],_rect[3]) - buf;
		double ymax = Math.max(_rect[1],_rect[3]) + buf;

		// create bounding rectangle
		return new Rectangle2D.Double(xmin,ymin,xmax-xmin,ymax-ymin);
	}
	
	/**
	 * get buffer size
	 * @return buffer size in meter
	 */
	public double getBuffer() { 
		return _buffer;
	}
	
	/**
	 * get flag for line geometry
	 * @return flag for line geometry
	 */
	public boolean needGeom() { 
		return _needGeom;
	}
}
