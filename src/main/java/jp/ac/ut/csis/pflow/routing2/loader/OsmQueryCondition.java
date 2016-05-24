package jp.ac.ut.csis.pflow.routing2.loader;


import static jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader.DEFAULT_BUFFER_SIZE;
import static jp.ac.ut.csis.pflow.routing2.res.OsmLink.ROAD_TYPES;

import jp.ac.ut.csis.pflow.routing2.res.OsmLink;


/** 
 * Class for query condition of OSM road network
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class OsmQueryCondition extends QueryCondition { 
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** road type flags	*/	
	private int[] _roadTypes;
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * initialization with the indicated parameters
	 */
	public OsmQueryCondition() { 
		this(null,ROAD_TYPES);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param roadTypes road type {@link OsmLink#ROAD_TYPES}
	 */
	public OsmQueryCondition(int[] roadTypes) { 
		this(roadTypes,false);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param roadTypes road type {@link OsmLink#ROAD_TYPES}
	 * @param needGeom flag for line geometry
	 */
	public OsmQueryCondition(int[] roadTypes,boolean needGeom) { 
		this(null,DEFAULT_BUFFER_SIZE,roadTypes,needGeom);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param roadTypes road type {@link OsmLink#ROAD_TYPES}
	 */
	public OsmQueryCondition(double[] rect,int[] roadTypes) { 
		this(rect,DEFAULT_BUFFER_SIZE,roadTypes);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @param roadTypes road type {@link OsmLink#ROAD_TYPES}
	 */
	public OsmQueryCondition(double[] rect,double bufSize,int[] roadTypes) { 
		this(rect,bufSize,roadTypes,false);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param roadTypes road type {@link OsmLink#ROAD_TYPES}
	 * @param needGeom flag for line geometry
	 */
	public OsmQueryCondition(double[] rect,int[] roadTypes,boolean needGeom) { 
		this(rect,DEFAULT_BUFFER_SIZE,roadTypes,needGeom);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param needGeom flag for line geometry
	 */
	public OsmQueryCondition(double[] rect,boolean needGeom) { 
		this(rect,DEFAULT_BUFFER_SIZE,needGeom);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @param needGeom flag for line geometry
	 */
	public OsmQueryCondition(double[] rect,double bufSize,boolean needGeom) { 
		this(rect,bufSize,null,needGeom);
	}
	
	/**
	 * initialization with the indicated parameters
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @param roadTypes road type {@link OsmLink#ROAD_TYPES}
	 * @param needGeom flag for line geometry
	 */
	public OsmQueryCondition(double[] rect,double bufSize,int[] roadTypes,boolean needGeom) { 
		super(rect,bufSize,needGeom);
		_roadTypes = roadTypes;
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get road types
	 * @return road type
	 * @see {@link OsmLink#ROAD_TYPES}
	 */
	public int[] getRoadTypes() { 
		return _roadTypes;
	}
}
