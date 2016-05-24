package jp.ac.ut.csis.pflow.geom;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;


/**
 * Class for Geohash
 * 
 * <dl>
 * <dt>references</dt>
 * <dd>geohash.org : <a href="http://geohash.org/">http://geohash.org/</a></dd>
 * <dd>geohash(wikipedia) : <a href="http://en.wikipedia.org/wiki/Geohash">http://en.wikipedia.org/wiki/Geohash</a></dd>
 * </dl>
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Geohash implements Serializable {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** serial              */	private static final long     serialVersionUID = -7305033420122736673L;
	
	/** latitude range      */	private static final double[] LAT_RANGE      = new double[]{ -90.0, 90.0};
	/** longitude range     */	private static final double[] LON_RANGE      = new double[]{-180.0,180.0};
	/** Base32 Digits       */	private static final String   DIGITS         = "0123456789bcdefghjkmnpqrstuvwxyz";
	/** Default hash length */	private static       int      GEOHASH_LENGTH = 7;	// approx. 120m x 150m
	

	/* ==============================================================
	 * static methods
	 * ============================================================== */	
	/**
	 * テスト用エントリポイント
	 * @param args なし
	 */
	public static void main(String args[]) throws IOException {
		Geohash inst0 = new Geohash(7,139.745447,35.65861);	// tokyo tower
		System.out.println(inst0);

		Geohash inst1 = new Geohash(inst0.getGeohash());
		System.out.println(inst1);
	}
	
	/**
	 * set default geohash length
	 * @param hashLength default geohash length
	 */
	public static void setDefaultGeohashLength(int hashLength) {
		// error handle /////////////////////////
		if( hashLength <= 0 ) { 
			throw new IllegalArgumentException("unavailable value: " + hashLength);
		}
		// set value ////////////////////////////
		GEOHASH_LENGTH = hashLength;
	}
	
	/**
	 * get default geohash length
	 * @return default geohash length
	 */
	public static int getDefaultGeohashLength() {
		return GEOHASH_LENGTH;
	}
	
	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** Geohash      */	private String	           _geohash;	
	/** input lonlat */	private LonLat             _lonlat;
	/** bounds       */	private Rectangle2D.Double _rect;

	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create Geohash instance with the indicated parameters
	 * @param precision precision
	 * @param lon longitude(decimal)
	 * @param lat latitude(decimal)
	 */
	public Geohash(int precision,double lon,double lat) {
		encode(precision,lon,lat);
	}
	
	/**
	 * create Geohash instance with the indicated parameters
	 * @param lon longitude(decimal)
	 * @param lat latitude(decimal)
	 */
	public Geohash(double lon, double lat) {
		this(GEOHASH_LENGTH, lon, lat);
	}
	
	/**
	 * create Geohash instance with the indicated parameters
	 * @param geohash Geohash string
	 */
	public Geohash(String geohash) {
		decode(geohash);
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get GeoHash string
	 * @return Geohash
	 */
	public String getGeohash() {
		return _geohash;
	}
	
	/**
	 * get geohash length
	 * @return geohash length
	 */
	public int getGeohashLength() {
		return _geohash.length();
	}
	
	/**
	 * get representative point
	 * @return representative point
	 */
	public LonLat getLonLat() {
		return _lonlat;
	}
	
	/**
	 * get rectangle representing Geohash region
	 * @return rectangle
	 */
	public Rectangle2D.Double getRect() {
		return _rect;
	}
	
	/**
	 * get centroid of Geohash
	 * @return centroid of Geohash.
	 */
	public LonLat getCenter() {
		return new LonLat(_rect.getCenterX(),_rect.getCenterY());
	}
	
	/**
	 * encode point into Geohash
	 * @param length Geohash length
	 * @param lon longitude
	 * @param lat latitude
	 */
	private void encode(int length,double lon,double lat) {
		// error handle ///////////////////////////////////
		if( length <= 0 || lon < LON_RANGE[0] || LON_RANGE[1] < lon || lat < LAT_RANGE[0] || LAT_RANGE[1] < lat ) {
			throw new IllegalArgumentException("unavailable value"); 
		}
		// set the number for iteration ///////////////////
		int          len   = length * 5;
		double       x[]   = new double[]{LON_RANGE[0],LON_RANGE[1]};
		double       y[]   = new double[]{LAT_RANGE[0],LAT_RANGE[1]};
		int          idx   = 0;
		int          buf   = 0;
		StringBuffer hash  = new StringBuffer();
		for(int i=0;i<len;i++) {
			// case even ////////////////////////
			if( i%2 == 0 ) {
				double mid = (x[0]+x[1])/2d;
				if( lon <= mid ) {
					buf = buf << 1;
					x[1] = mid;
				}
				else {
					buf = (buf << 1) | 1;
					x[0] = mid;
				}				
			}
			// case odd /////////////////////////
			else {
				double mid = (y[0]+y[1])/2d;
				if( lat <= mid ) { 
					buf = buf << 1;
					y[1] = mid;
				}
				else {
					buf = (buf << 1) | 1;
					y[0] = mid;
				}				
			}
			// constitute Geohash character
			if( ++idx == 5 ) {
				hash.append(DIGITS.charAt(buf));
				buf = idx = 0;
			}
		}
		// create geohash instance ////////////////////////
		_geohash   = hash.toString();
		_lonlat    = new LonLat(lon,lat);
		_rect      = new Rectangle2D.Double(x[0],y[0],x[1]-x[0],y[1]-y[0]);
	}
	
	/**
	 * decode Geohash string 
	 * @param hash Geohash string
	 */
	private void decode(String hash) {
		// error handle ///////////////////////////////////
		if( hash == null || hash.isEmpty() ) {
			throw new IllegalArgumentException("unavailable value"); 
		}
		// set initial values /////////////////////////////
		int    len = hash.length();
		double x[] = new double[]{LON_RANGE[0],LON_RANGE[1]};
		double y[] = new double[]{LAT_RANGE[0],LAT_RANGE[1]};
		// start checking each digit //////////////////////
		int idx = 0;
		for(int i=0;i<len;i++) {
			char c   = hash.charAt(i);
			int  dig = DIGITS.indexOf(c);
			int  mask= 16;	// start of 5 bits
			for(int j=0;j<5;j++) {
				// case even ////////////////////
				if( idx%2 == 0 ) {
					if( (dig & mask) == 0 ) {	x[1] = (x[0]+x[1])/2d; }
					else {						x[0] = (x[0]+x[1])/2d; }
				}
				// case odd /////////////////////
				else {
					if( (dig & mask) == 0 ) {	y[1] = (y[0]+y[1])/2d; }
					else {						y[0] = (y[0]+y[1])/2d; }					
				}
				mask >>= 1;
				idx += 1;
			}
		}
		// set instance values ////////////////////////////
		_geohash = hash;
		_rect    = new Rectangle2D.Double(x[0],y[0],x[1]-x[0],y[1]-y[0]);
		_lonlat  = new LonLat(_rect.getCenterX(),_rect.getCenterY());
	}
	
	/* @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return String.format("%s,%f,%f",_geohash,_lonlat.getLon(),_lonlat.getLat());
	}
	
	/* @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		// correspond to this ///////////////////
		if( obj == this ) { return true; }
		// geohash instance  ////////////////////
		else if( obj instanceof Geohash ) {
			String code1 = getGeohash();
			String code2 = Geohash.class.cast(obj).getGeohash();
			return code1.equals(code2);
		}
		return false;
	}
	
	/* @see java.lang.Object#clone() */
	@Override
	public Geohash clone() {
		LonLat pt = getLonLat();
		// create new instance
		return new Geohash(getGeohashLength(), pt.getLon(), pt.getLat());
	}
}
