package jp.ac.ut.csis.pflow.geom;

import java.util.Collection;
import java.util.List;

/**
 * Utility class for distance calculation
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public final class DistanceUtils {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** equator radius in WGS84			*/	private static final double	WGS84_EQUATOR_RADIUS = 6378137;
	/** polar radius in WGS84			*/	private static final double WGS84_POLAR_RADIUS   = 6356752.314245;
	/** square of eccentricity in WGS84 */	private static final double WGS84_ECCENTRICITY_2 = (WGS84_EQUATOR_RADIUS * WGS84_EQUATOR_RADIUS - 
																							WGS84_POLAR_RADIUS   * WGS84_POLAR_RADIUS  ) 
																							/ 
																							(WGS84_EQUATOR_RADIUS*WGS84_EQUATOR_RADIUS); 
	
	/* ==============================================================
	 * static methods
	 * ============================================================== */
	/**
     * get distance(m) between indicated points with Hubeny formula)
     * @param p0 LonLat Point(WGS84)
     * @param p1 LonLat Point(WGS84)
     * @return distance between 2 points(m)
     */
    public static <T extends LonLat,S extends LonLat> double distance(T p0,S p1) {
    	return distance(p0.getLon(),p0.getLat(),p1.getLon(),p1.getLat());
    }
    
    /**
     * get distance(m) between indicated points with Hubeny formula)
     * @param lon0 longitude of point 0(WGS84)
     * @param lat0 latitude of point 0(WGS84) 
     * @param lon1 longitude of point 1(WGS84) 
     * @param lat1 latitude of point 1(WGS84) 
     * @return distance between 2 points(m)
     */
    public static double distance(double lon0, double lat0, double lon1, double lat1) {
    	double a  = WGS84_EQUATOR_RADIUS;
		double e2 = WGS84_ECCENTRICITY_2;
		double dy = Math.toRadians(lat0 - lat1); // p0.getLat()  - p1.getLat());
		double dx = Math.toRadians(lon0 - lon1); // p0.getLon()  - p1.getLon());
		double cy = Math.toRadians((lat0 + lat1)/2d); // (p0.getLat() + p1.getLat()) / 2d);
		double m  = a * (1-e2);
		double sc = Math.sin(cy);
		double W  = Math.sqrt(1d-e2*sc*sc);
		double M  = m/(W*W*W);
		double N  = a/W;
		
		double ym = dy*M;
		double xn = dx*N*Math.cos(cy);
		
		return Math.sqrt(ym*ym + xn*xn);
    }
    
    /**
     * get distance from point(p) to line(p0,p1)
     * @param p0 start point of line segment
     * @param p1 end point of line segment
     * @param p isolated point
     * @return distance (m)
     */
    public static <T extends LonLat> double distance(T p0,T p1,T p) {
    	// get foot of a perpendicular 
    	LonLat foot = nearestPoint(p0,p1,p);
    	// get distance from the specified point to foot 
    	return distance(foot,p);
    }
    
    /**
     * get the nearest point on the line(p0,p1) from point(p). 
     * @param p0 start point of line segment
     * @param p1 end point of line segment
     * @param p isolated point
     * @return foot point of a perpendicular
     */
    public static <T extends LonLat> LonLat nearestPoint(T p0, T p1, T p) {
		// get parameters /////////////////////////////////
		double dx = p1.getLon() - p0.getLon();
		double dy = p1.getLat() - p0.getLat();
		double a  = dx*dx + dy*dy;
		double b  = dx*(p0.getLon()-p.getLon()) + dy*(p0.getLat()-p.getLat());
		// no line segment. just point ////////////////////
		if ( a == 0 ) { return new LonLat(p0.getLon(),p0.getLat());	}
		// 
		double t  = -b/a;
		if( t < 0d ) {      t = 0d; }
		else if( t > 1d ) { t = 1d; }
		// set nearest point //////////////////////////////
		double x = t*dx + p0.getLon();
		double y = t*dy + p0.getLat();
		return new LonLat(x,y);
	}
    
    /**
     * get the nearest distance from point(p) to line(point list)
     * @param line line
     * @param p point
     * @return distance(m)
     */
    public static <T extends LonLat,S extends LonLat> double distance(List<T> line,S p) {
    	// get foot of a perpendicular ////////////////////
    	LonLat foot = nearestPoint(line,p);
    	// get distance from the specified point to foot // 
    	return distance(foot,p);
    }
    
    /**
     * get the nearest point from point(p) to line(point list)
     * @param line line
     * @param p point
     * @return nearest point
     */
    public static <T extends LonLat,S extends LonLat> LonLat nearestPoint(List<T> line,S p) {
		int    len = line.size();
		LonLat p0  = line.get(0);
		LonLat res = null;
		double dis = Double.MAX_VALUE;
		
		for(int i=1;i<len;i++) {
			LonLat p1 = line.get(i);
			LonLat q  = nearestPoint(p0,p1,p);
			double d  = p.distance(q);
			if( d < dis ) {
				res = q;
				dis = d;
			}
			p0 = p1;
		}
		return res;
	}
    
    /**
	 * get Hausdorff Distance
	 * @param setA point set A
	 * @param setB point set B
	 * @return Hausedorff distance
	 */
	public static <T extends LonLat,S extends LonLat> double hausdorff(Collection<T> setA, Collection<S> setB) {
		// compare A to B
		double D0 = java.lang.Double.MIN_VALUE;
		for(T a:setA) {
			// looking for nearest distance
			double d = java.lang.Double.MAX_VALUE;
			for(S b:setB) { d = Math.min(d, distance(a,b)); }
			// looking for largest distance
			D0 = Math.max(D0, d);
		}
		// compare B to A
		double D1 = java.lang.Double.MIN_VALUE;
		for(S b:setB) {
			// looking for nearest distance
			double d = java.lang.Double.MAX_VALUE;
			for(T a:setA) { d = Math.min(d, distance(b,a)); }
			// looking for largest distance
			D1 = Math.max(D1, d);
		}
		// get larger value
		return Math.max(D0, D1);
	}	
}
