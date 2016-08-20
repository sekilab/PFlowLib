package jp.ac.ut.csis.pflow.geom;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.MIN_VALUE;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collection;

/**
 * Class for geometry point(Longitude Latitude). WGS84 as default<br />
 * <ul>
 * <li>http://www.colorado.edu/geography/gcraft/notes/datum/edlist.html</li>
 * <li>http://yamadarake.jp/trdi/report000001.html</li>
 * </ul>
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class LonLat implements Serializable, Cloneable {   
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Serial */	private static final long serialVersionUID = -4164943786825374186L;

	
	/* ==============================================================
	 * static methods
	 * ============================================================== */
	/**
	 * create MBR(Minimum Bounding Rectangle) from the indicated point set
	 * @param set point set
	 * @return MBR 
	 */
	public static <T extends LonLat> Rectangle2D.Double makeMBR(Collection<T> set) {
		double xmin = MAX_VALUE;
		double xmax = MIN_VALUE;
		double ymin = MAX_VALUE;
		double ymax = MIN_VALUE;
		for(T c:set) {
			xmin = Math.min(xmin,c.getLon());
			xmax = Math.max(xmax,c.getLon());
			ymin = Math.min(ymin,c.getLat());
			ymax = Math.max(ymax,c.getLat());
		}
		return new Rectangle2D.Double(xmin,ymin,xmax-xmin,ymax-ymin);
	}
	
	
    /* ==============================================================
	 * instance fields
	 * ============================================================== */
    /** longitude	*/	private double _lon;
    /** latitude	*/	private double _lat;
    
    
    /* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
     * initialization
     */
    public LonLat() {
    	this(Double.NaN,Double.NaN);
    }
    
    /**
     * initialization with coordinate parameters.
     * @param lon longitude in decimal
     * @param lat latitude in decimal
     */
    public LonLat(double lon, double lat) {
    	_lon = lon;
    	_lat = lat;
    }
    
    
    /* ==============================================================
	 * instance methods
	 * ============================================================== */
    /**
	 * set latitude
	 * @param lat latitude
	 */
    public void setLat(double lat) {
    	_lat = lat;
    }
    
    /**
	 * set longitude 
	 * @param lon longitude
	 */
    public void setLon(double lon) {
    	_lat = lon;
    }
    
    /**
     * get latitude
     * @return latitude
     */
    public double getLat() {
    	return _lat;
    }
    
    /**
     * get longitude
     * @return longitude
     */
    public double getLon() {
    	return _lon;
    }
    
    /**
     * set position
     * @param lon longitude
     * @param lat latitude
     */
    public void setLocation(double lon,double lat) {
    	_lon = lon;
    	_lat = lat;
    }
    
    /**
     * set position
     * @param lonlat position
     */
    public void setLocation(LonLat lonlat) {
    	_lon = lonlat.getLon();
    	_lat = lonlat.getLat();
    }
    
    /**
     * check if position values are valid or not
     * @return result
     */
    public boolean isValid() { 
    	return !Double.isNaN(_lon) && !Double.isNaN(_lat);
    }

    /**
     * check if this position is north
     * @return true when north, otherwise false
     */
    public boolean isNorth() {
    	return getLat() > 0;
    }
    
    /**
     * check if this position is south
     * @return true when south, otherwise false
     */
    public boolean isSouth() {
    	return getLat() < 0;
    }
    
    /**
     * check if this position is east
     * @return true when east, otherwise false
     */
    public boolean isEast() {
    	return getLon() > 0;
    }
    
    /**
     *  check if this position is west
     * @return true when west, otherwise false
     */
    public boolean isWest() {
    	return getLon() < 0;
    }
    
    /**
     * get distance with the indicated point in meter
     * @param p target position
     * @return distance(m)
     * @see DistanceUtils#distance(LonLat, LonLat)
     */
    public double distance(LonLat p) {
    	return DistanceUtils.distance(p,this);
    }
    
    /* @see java.lang.Object#toString() */
    @Override
    public String toString() {
    	return String.format("(%.08f,%.08f)", getLon(), getLat());
    }
    
    /* @see java.lang.Object#clone() */
    @Override
    public LonLat clone() {
    	return new LonLat( getLon(), getLat() );
    }
}
