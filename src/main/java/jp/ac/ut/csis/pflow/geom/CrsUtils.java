package jp.ac.ut.csis.pflow.geom;


import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/**
 * Utility class for CRS(Coordinate Reference System)
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class CrsUtils {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** CRS for WGS84(EPSG:4326) 		*/	public static CoordinateReferenceSystem WGS84        = null; 
	/** CRS for JGD2000(EPSG:4612)		*/	public static CoordinateReferenceSystem JGD2000      = null;
	/** CRS for Tokyo(EPSG:4301)		*/	public static CoordinateReferenceSystem TOKYO        = null;
//	/** CRS for Web Mercator(EPSG:3857)	*/	public static CoordinateReferenceSystem WEB_MERCATOR = null;	// invalid
	
	static {	// initialization
		try {
			WGS84        = CRS.decode("EPSG:4326");
			TOKYO        = CRS.decode("EPSG:4301");
			JGD2000      = CRS.decode("EPSG:4612");
//			WEB_MERCATOR = CRS.decode("EPSG:3857"); // 900913 or 3857
		} 
		catch(FactoryException exp) { exp.printStackTrace(); }
	}
	

	/* ==============================================================
	 * static methods
	 * ============================================================== */	
	/**
	 * conduct CRS transformation(Tokyo[EPSG:4301] to WGS84[EPSG:4326])
	 * @param point point under source CRS
	 * @return CRS-transformed point list
	 */
	public static LonLat transformTokyo2Wgs(LonLat point) {
		return transform(point,TOKYO,WGS84);
	}
	
	/**
	 * conduct CRS transformation(WGS84[EPSG:4326] to Tokyo[EPSG:4301])
	 * @param point point under source CRS
	 * @return CRS-transformed point list
	 */
	public static LonLat transformWgs2Tokyo(LonLat point) {
		return transform(point,WGS84,TOKYO);
	}
	
	/*
	 * conduct CRS transformation(WebMercator[EPSG:3857] to WGS84[EPSG:4326])
	 * @param point point under source CRS
	 * @return CRS-transformed point list
	 *
	public static LonLat transformWM2Wgs(LonLat point) {
		return transform(point,WEB_MERCATOR,WGS84);
	}*/
	
	/*
	 * conduct CRS transformation(WGS84[EPSG:4326] to WebMercator[EPSG:3857])
	 * @param point point under source CRS
	 * @return CRS-transformed point list
	 *
	public static LonLat transformWgs2WM(LonLat point) {
		return transform(point,WGS84,WEB_MERCATOR);
	}*/
	
	/**
	 * conduct CRS transformation(JGD2000[EPSG:4612] to WGS84[EPSG:4326])
	 * @param point point under source CRS
	 * @return CRS-transformed point list
	 */
	public static LonLat transformJgd2Wgs(LonLat point) {
		return transform(point,JGD2000,WGS84);
	}
	
	/**
	 * conduct CRS transformation(WGS84[EPSG:4326] to JGD2000[EPSG:4612] )
	 * @param point point under source CRS
	 * @return CRS-transformed point list
	 */
	public static LonLat transformWgs2Jgd(LonLat point) {
		return transform(point,WGS84,JGD2000);
	}
	
	/**
	 * conduct CRS transformation
	 * @param point point under source CRS
	 * @param sourceCRS source CRS
	 * @param targetCRS target CRS
	 * @return CRS-transformed point
	 */
	private static LonLat transform(LonLat point,CoordinateReferenceSystem sourceCRS,CoordinateReferenceSystem targetCRS) {
		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
		Geometry        geomIn  = factory.createPoint(new Coordinate(point.getLat(),point.getLon()));	// axis order must be (LAT,LON)
		Point           geomOut = Point.class.cast(transform(geomIn,sourceCRS,targetCRS));
		
		boolean rot = CRS.getAxisOrder(sourceCRS).equals(CRS.getAxisOrder(targetCRS));
		
		return rot ? new LonLat(geomOut.getY(),geomOut.getX()) : new LonLat(geomOut.getX(),geomOut.getY());	// axis order must be (LAT,LON)
	}
	
	
	/*
	 * conduct CRS transformation(WebMercator[EPSG:3857] to WGS84[EPSG:4326])
	 * @param points point list under source CRS
	 * @return CRS-transformed point list
	 *
	public static List<LonLat> transformWM2Wgs(List<LonLat> points) {
		return transform(points,WEB_MERCATOR,WGS84);
	}*/
	
	/*
	 * conduct CRS transformation(WGS84[EPSG:4326] to WebMercator[EPSG:3857])
	 * @param points point list under source CRS
	 * @return CRS-transformed point list
	 *
	public static List<LonLat> transformWgs2WM(List<LonLat> points) {
		return transform(points,WGS84,WEB_MERCATOR);
	}
	*/
	
	/**
	 * conduct CRS transformation(Tokyo[EPSG:4301] to WGS84[EPSG:4326])
	 * @param points point list under source CRS
	 * @return CRS-transformed point list
	 */
	public static List<LonLat> transformTokyo2Wgs(List<LonLat> points) {
		return transform(points,TOKYO,WGS84);
	}
	
	/**
	 * conduct CRS transformation(WGS84[EPSG:4326] to Tokyo[EPSG:4301])
	 * @param points point list under source CRS
	 * @return CRS-transformed point list
	 */
	public static List<LonLat> transformWgs2Tokyo(List<LonLat> points) {
		return transform(points,WGS84,TOKYO);
	}
	
	/**
	 * conduct CRS transformation(JGD2000[EPSG:4612] to WGS84[EPSG:4326])
	 * @param points point list under source CRS
	 * @return CRS-transformed point list
	 */
	public static List<LonLat> transformJgd2Wgs(List<LonLat> points) {
		return transform(points,JGD2000,WGS84);
	}
	
	/**
	 * conduct CRS transformation(WGS84[EPSG:4326] to JGD2000[EPSG:4612])
	 * @param points point list under source CRS
	 * @return CRS-transformed point list
	 */
	public static List<LonLat> transformWgs2Jgd(List<LonLat> points) {
		return transform(points,WGS84,JGD2000);
	}
	
	/**
	 * conduct CRS transformation
	 * @param points point list under source CRS
	 * @param sourceCRS source CRS
	 * @param targetCRS target CRS
	 * @return CRS-transformed point list
	 */
	private static List<LonLat> transform(List<LonLat> points,CoordinateReferenceSystem sourceCRS,CoordinateReferenceSystem targetCRS) {
		int             N       = points.size();
		List<LonLat>    output  = new ArrayList<LonLat>(N);
		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
		
		// create multi-point coordinate //////////////////
		int          i=0;
		Coordinate[] coords  = new Coordinate[N];
		for(LonLat p:points) { 
			coords[i++] = new Coordinate(p.getLat(),p.getLon()); 	// axis order must be (LAT,LON)
		}	
		
		// conduct CRS transformation /////////////////////
		Geometry   geomIn  = factory.createMultiPoint(coords);
		MultiPoint geomOut = MultiPoint.class.cast(transform(geomIn,sourceCRS,targetCRS));
		boolean    rot     = CRS.getAxisOrder(sourceCRS).equals(CRS.getAxisOrder(targetCRS));

		// re-create point list ///////////////////////////
		for(Coordinate coord:geomOut.getCoordinates()) { 
			output.add(rot ? new LonLat(coord.y,coord.x) : new LonLat(coord.x,coord.y));	// axis order must be (LAT,LON)
		}
		return output;
	}
	
	/**
	 * conduct CRS transformation
	 * @param geom JTS geometry
	 * @param sourceCRS source CRS
	 * @param targetCRS target CRS
	 * @return CRS-transformed JTS geometry
	 */
	private static Geometry transform(Geometry geom,CoordinateReferenceSystem sourceCRS,CoordinateReferenceSystem targetCRS) {
		Geometry outputGeom = null;
		try { 
			MathTransform trans = CRS.findMathTransform(sourceCRS,targetCRS,false);
			outputGeom = JTS.transform(geom,trans);
		}
		catch(FactoryException | TransformException exp) { exp.printStackTrace(); }
		
		return outputGeom;
	}
}
