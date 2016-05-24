package jp.ac.ut.csis.pflow.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Utility class for JTS geometry. <br />
 * 
 * Default SRID is WGS84(4326)<br />
 * in order to update SRID value, <b>pflow.geometryutils.srid</b> property can be set
 *  
 * @author People Flow Project, CSIS, UTokyo.
 */
public class GeometryUtils {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** SRID property	*/	public static final int             SRID    = Integer.getInteger("pflow.geometryutils.srid",4326);
	/** geometry factory*/	public static final GeometryFactory GEOMFAC = new GeometryFactory(new PrecisionModel(),SRID);
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * create WKT string from the specified geometry(JTS)
	 * @param geom geometry instance
	 * @return WKT string
	 */
	public static String createWKTString(Geometry geom) {
		return new WKTWriter().write(geom);
	}
	
	/**
	 * parse WKT string and generate geometry instance 
	 * @param wktstring WKT string
	 * @return geometry instance. return null if failed
	 */
	public static Geometry parseWKT(String wktstring) {
		Geometry geom = null;
		try {
			geom = new WKTReader().read(wktstring);
		}
		catch(ParseException exp) {
			exp.printStackTrace(); geom = null;
		}
		return geom;
	}
	
	/**
	 * create WKB string from the specified geometry
	 * @param geom geometry instance 
	 * @return WKB hex string
	 */
	public static String createWKBString(Geometry geom) {
//		return WKBWriter.bytesToHex(new WKBWriter(2).write(geom));	// jts 1.8
		return WKBWriter.toHex(new WKBWriter(2,true).write(geom));	// jts 1.13
	}
	
	/**
	 * parse WKB hex string and generate geometry instance 
	 * @param wkbstring WKB hex string 
	 * @return geometry instance. return null if failed
	 */
	public static Geometry parseWKB(String wkbstring) {
		Geometry geom = null;
		try {
			geom = new WKBReader().read(WKBReader.hexToBytes(wkbstring));
		}
		catch(ParseException exp) {
			exp.printStackTrace(); geom = null; 
		}
		return geom;
	}
	
	/**
	 * create LonLat from a Point geometry
	 * @param p JTS geometry
	 * @return LonLat point
	 */
	public static LonLat createPoint(Point p) { 
		return new LonLat(p.getX(),p.getY());
	}
	
	/**
	 * create point list from a LineString geometry
	 * @param linestring JTS geometry
	 * @return LonLat list
	 */
	public static List<LonLat> createPointList(LineString linestring) {
		int          size   = linestring.getNumPoints();
		List<LonLat> points = new ArrayList<LonLat>(size);
		for(int i=0;i<size;i++) {
			Point point = linestring.getPointN(i);
			points.add( new LonLat(point.getX(),point.getY()));
		}
		return points;
	}

	/**
	 * create POINT geometry from the indicated position
	 * @param lon longitude
	 * @param lat latitude
	 * @return POINT geometry of JTS
	 */
	public static Point createPoint(double lon,double lat) { 
		return createPoint(new LonLat(lon,lat));
	}
	
	/**
	 * create POINT geometry from the specified position instance
	 * @param point position information
	 * @return POINT geometry of JTS
	 */
	public static <T extends LonLat> Point createPoint(T point) {
		return GEOMFAC.createPoint(new Coordinate(point.getLon(),point.getLat()));
	}
	
	/**
	 * create MultiPoint Geometry from the indicated point list
	 * @param points array of position information 
	 * @return MULTIPOINT geometry of JTS
	 */
	public static <T extends LonLat> MultiPoint createMultiPoint(T[] points) {
		return createMultiPoint(Arrays.asList(points));
	}
	
	
	/**
	 * create MultiPoint Geometry from the indicated point list
	 * @param points list of position information 
	 * @return MULTIPOINT geometry of JTS
	 */
	public static <T extends LonLat> MultiPoint createMultiPoint(List<T> points) {
		Coordinate[] coords = new Coordinate[points.size()];
		for(int i=points.size()-1;i>=0;i--) {
			T p = points.get(i);
			coords[i] = new Coordinate(p.getLon(),p.getLat());
		}		
		return GEOMFAC.createMultiPoint(coords);
	}
	
	/**
	 * create LINESTRING geometry from the specified point array
	 * @param points point array
	 * @return LINESTRING geometry
	 */
	public static <T extends LonLat> LineString createLineString(T points[]) {
		return createLineString(Arrays.asList(points));
	}
	
	/**
	 * create LINESTRING geometry from the specified point list
	 * @param points point list 
	 * @return LINESTRING geometry
	 */
	public static <T extends LonLat> LineString createLineString(List<T> points) {
		// make coordinate array //////////////////////////
		Coordinate coords[] = new Coordinate[points.size()];
		for(int i=points.size()-1;i>=0;i--) {
			T ll = points.get(i);
			coords[i] = new Coordinate(ll.getLon(),ll.getLat());
		}		
		return GEOMFAC.createLineString(coords);
	}
	
	/**
	 * create POLYGON geometry from the specified points array. <br />
	 * [CAUTION] the first and the last point in the array must be same to close the output polygon
	 * @param points point array
	 * @return POLYGON geometry
	 */
	public static <T extends LonLat> Polygon createPolygon(T points[]) {
		return createPolygon(Arrays.asList(points));
	}
	
	/**
	 * create POLYGON geometry from the specified points list. <br />
	 * [CAUTION] the first and the last point in the list must be same to close the output polygon
	 * @param points point list
	 * @return POLYGON geometry
	 */
	public static <T extends LonLat> Polygon createPolygon(List<T> points) {
		// make coordinate array //////////////////////////
		Coordinate coords[] = new Coordinate[points.size()];
		for(int i=points.size()-1;i>=0;i--) {
			T ll = points.get(i);
			coords[i] = new Coordinate(ll.getLon(),ll.getLat());
		}
		return GEOMFAC.createPolygon(GEOMFAC.createLinearRing(coords),null);	// no hole
	}
}
