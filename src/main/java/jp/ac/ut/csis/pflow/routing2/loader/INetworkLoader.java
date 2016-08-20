package jp.ac.ut.csis.pflow.routing2.loader;

import jp.ac.ut.csis.pflow.routing2.res.Network;

/**
 * interface for network data loader
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public interface INetworkLoader {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** approximate 1 km in degree */	public static final double APPROX_1KM = 0.012/1000d;	// tentative

	/** default buffer size (meter)*/	public static final double DEFAULT_BUFFER_SIZE = 3000d;
	/** minimum buffer size (meter)*/	public static final double MINIMUM_BUFFER_SIZE = 1000d;
	/** maximum buffer size (meter)*/	public static final double MAXIMUM_BUFFER_SIZE = 5000d;
	
	
	/* ==============================================================
	 * abstract methods
	 * ============================================================== */
	/**
	 * load network data within the indicated area
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @return network data
	 */
	public Network load(double x0,double y0,double x1,double y1);

	/**
	 * load network data within the indicated area
	 * @param network network instance to add data 
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @return network data
	 */
	public Network load(Network network,double x0,double y0,double x1,double y1);
	
	
	/**
	 * load network data within the indicated areas
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @return network data
	 */
	public Network load(double[] rect);
	
	/**
	 * load network data within the indicated areas
	 * @param network network instance to add data 
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @return network data
	 */
	public Network load(Network network,double[] rect);
	
	
	/**
	 * load network data within the indicated area
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @param bufSize buffer size in meter
	 * @return network data
	 */
	public Network load(double x0,double y0,double x1,double y1,double bufSize);

	/**
	 * load network data within the indicated area
	 * @param network network instance to add data 
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @param bufSize buffer size in meter
	 * @return network data
	 */
	public Network load(Network network,double x0,double y0,double x1,double y1,double bufSize);

	
	/**
	 * load network data within the indicated areas
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @return network data
	 */
	public Network load(double[] rect,double bufSize);

	/**
	 * load network data within the indicated areas
	 * @param network network instance to add data 
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @return network data
	 */
	public Network load(Network network,double[] rect,double bufSize);

	
	/**
	 * load network data within the indicated area
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @param neeeGeom flag if include geometry
	 * @return network data
	 */
	public Network load(double x0,double y0,double x1,double y1,boolean needGeom);
	
	/**
	 * load network data within the indicated area
	 * @param network network instance to add data 
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @param neeeGeom flag if include geometry
	 * @return network data
	 */
	public Network load(Network network,double x0,double y0,double x1,double y1,boolean needGeom);
	
	
	/**
	 * load network data within the indicated areas
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param neeeGeom flag if include geometry
	 * @return network data
	 */
	public Network load(double[] rect,boolean needGeom);
	
	/**
	 * load network data within the indicated areas
	 * @param network network instance to add data 
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param neeeGeom flag if include geometry
	 * @return network data
	 */
	public Network load(Network network,double[] rect,boolean needGeom);
	
	
	/**
	 * load network data within the indicated area
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @param bufSize buffer size in meter
	 * @param needGeom flag if include geometry
	 * @return network data
	 */
	public Network load(double x0,double y0,double x1,double y1,double bufSize,boolean needGeom);
	
	/**
	 * load network data within the indicated area
	 * @param network network instance to add data 
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @param bufSize buffer size in meter
	 * @param needGeom flag if include geometry
	 * @return network data
	 */
	public Network load(Network network,double x0,double y0,double x1,double y1,double bufSize,boolean needGeom);

	
	/**
	 * load network data within the indicated areas
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @param neeeGeom flag if include geometry
	 * @return network data
	 */
	public Network load(double[] rect,double bufSize,boolean needGeom);
	
	/**
	 * load network data within the indicated areas
	 * @param network network instance to add data 
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @param neeeGeom flag if include geometry
	 * @return network data
	 */
	public Network load(Network network,double[] rect,double bufSize,boolean needGeom);

	
	/**
	 * load network data with the indicated query condition
	 * @param qc query condition
	 * @return network data
	 */
	public Network load(QueryCondition qc);
	
	/**
	 * load network data with the indicated query condition
	 * @param network network instance to add data 
	 * @param qc query condition
	 * @return network data
	 */
	public Network load(Network network,QueryCondition qc);
	
	
	/**
	 * load network data with the indicated query conditions
	 * @param qc query conditions
	 * @return network data
	 */
	public Network load(QueryCondition[] qcs);
	
	/**
	 * load network data with the indicated query conditions
	 * @param network network instance to add data 
	 * @param qc query conditions
	 * @return network data
	 */
	public Network load(Network network,QueryCondition[] qcs);
	
	
	/**
	 * load all network data. <br />
	 * [CAUTION] take care of network volume and memory size
	 * @return network data
	 */
	public Network load();
	
	/**
	 * load all network data. <br />
	 * [CAUTION] take care of network volume and memory size
	 * @param network network instance to add data 
	 * @return network data
	 */
	public Network load(Network network);
}
