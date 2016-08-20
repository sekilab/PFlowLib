package jp.ac.ut.csis.pflow.routing2.loader;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import jp.ac.ut.csis.pflow.geom.GeometryUtils;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;
import jp.ac.ut.csis.pflow.routing2.res.OsmLink;

/**
 * Class for loading OSM network data(by osm2po) from CSV(WKB)
 * 
 * <ol>
 * <dt>csv format(based on osm2po outputs)</dt>
 * <li>gid(int)</li>
 * <li>osm_id(long)</li>
 * <li>osm_name(string)</li>
 * <li>osm_meta(string></li>
 * <li>osm_source_id(long)</li>
 * <li>osm_target_id(long)</li>
 * <li>clazz(int)</li>
 * <li>flags(int)</li>
 * <li>source(int)</li>
 * <li>target(int)</li>
 * <li>km(double)</li>
 * <li>km/h(int)</li>
 * <li>cost(double)</li>
 * <li>reverse_cost(double)</li>
 * <li>x1(double)</li>
 * <li>y1(double)</li>
 * <li>x2(double)</li>
 * <li>y2(double)</li>
 * <li>geom(WKB linestring)</li>
 * </ol>
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class CsvOsmLoader extends ACsvNetworkLoader {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Logger */
	private static final Logger LOGGER = LogManager.getLogger(CsvOsmLoader.class);
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create OSM network loader
	 * @param networkfile network csv data including line geometry as WKB
	 * @param hasHeader csv header flag
	 * @param delimiter delimiter {@link Delimiter}
	 */
	public CsvOsmLoader(File networkfile,boolean hasHeader,Delimiter delimiter) { 
		super(networkfile,hasHeader,delimiter);
	}
	
	/**
	 * create OSM network loader
	 * @param networkfile network csv data including line geometry as WKB
	 * @param hasHeader csv header flag
	 */
	public CsvOsmLoader(File networkfile,boolean hasHeader) { 
		super(networkfile,hasHeader);
	}
	
	/**
	 * create OSM network loader
	 * @param networkfile network csv data including line geometry as WKB
	 */
	public CsvOsmLoader(File networkfile) { 
		super(networkfile);
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing2.loader.ACsvNetworkLoader#parseLine(jp.ac.ut.csis.pflow.routing2.res.Network, java.lang.String) */
	@Override
	protected Link parseLine(Network network,String line) {
		OsmLink link = null;
		try { 
			// split line /////////////////////////////////////
			String[] tokens = getDelimiter().equals(Delimiter.CSV) ? 
								StrTokenizer.getCSVInstance(line).getTokenArray():	// csv
								StrTokenizer.getTSVInstance(line).getTokenArray();	// tsv
			
			// extract necessary columns //////////////////////
			String  gid  = tokens[0];	// gid
			String  src  = tokens[8];	// source
			String  tgt  = tokens[9];	// target
			int     spd  = Integer.parseInt(tokens[11]);
			double  cst  = Double.parseDouble(tokens[10]);
			double  rcst = Double.parseDouble(tokens[10]);
			int     clz  = Integer.parseInt(tokens[6]);
			String  wkb  = tokens[18];
			boolean way  = false;
			
			// parse geometry /////////////////////////////////
			LineString linestring = LineString.class.cast(GeometryUtils.parseWKB(wkb)); 
			Point p0 = linestring.getStartPoint();
			Point p1 = linestring.getEndPoint();
			List<LonLat> list = GeometryUtils.createPointList(linestring);
			
			// create network instance ////////////////////////
			Node n0 = network.hasNode(src) ? network.getNode(src) : new Node(src,p0.getX(),p0.getY());
			Node n1 = network.hasNode(tgt) ? network.getNode(tgt) : new Node(tgt,p1.getX(),p1.getY());
			
			// Create OSM link ////////////////////////////////
			link = new OsmLink(String.valueOf(gid),n0,n1,cst,rcst,way,clz,spd,list);
		}
		catch(Exception exp) { 
			LOGGER.error("fail to load network",exp);
		}
		
		return link;
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.ACsvNetworkLoader#validate(jp.ac.ut.csis.pflow.routing2.loader.QueryCondition[], jp.ac.ut.csis.pflow.routing2.res.Link) */
	@Override
	protected boolean validate(QueryCondition[] qcs,Link link) {
		// check bounds 
		boolean bounds = super.validate(qcs, link);
		
		// check road type
		for(QueryCondition qc:qcs) {
			if(qc instanceof OsmQueryCondition) {
				int[] types = OsmQueryCondition.class.cast(qc).getRoadTypes();
				if( bounds && ArrayUtils.contains(types,OsmLink.class.cast(link).getRoadClass()) ) {
					return true;
				}
			}
		}
		return false;
	}
}
