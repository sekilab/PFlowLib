package jp.ac.ut.csis.pflow.routing2.loader;

import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.LineString;
import org.postgis.MultiLineString;
import org.postgis.PGgeometry;
import org.postgis.Point;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;
import jp.ac.ut.csis.pflow.routing2.res.OsmLink;

/**
 * Class for loading OSM network data(by osm2po) from PostgreSQL/PostGIS<br />
 * 
 * <b>default table scheme created from OSM2PO</b>
 * <pre>
CREATE TABLE osm.road(
  id integer NOT NULL primary key,
  osm_id bigint,
  osm_name character varying,
  osm_meta character varying,
  osm_source_id bigint,
  osm_target_id bigint,
  clazz integer,
  flags integer,
  source integer,
  target integer,
  km double precision,
  kmh integer,
  cost double precision,			// time duration in hour
  reverse_cost double precision,	// time duration in hour
  x1 double precision,
  y1 double precision,
  x2 double precision,
  y2 double precision,
  geom_way geometry(LineString,4326)
);
 * </pre>
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class PgOsmLoader extends APgNetworkLoader {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Logger */
	private static final Logger LOGGER = LogManager.getLogger(PgOsmLoader.class);
	
	/** default link table(default: osm.japan_road).<br/> 
	 * system property key="pflow.routing2.pgloader.osm_network_table" **/	
	public static final String NETWORK_TABLE = System.getProperty("pflow.routing2.pgloader.osm_network_table","osm.japan_road");
	

	/** column name of link id (default: gid) */
	public static final String OSM_LINK_ID_COLUMN       = System.getProperty("pflow.routing2.pgloader.osm_link_id_column",     LINK_ID_COLUMN);
	/** column name of source node (default: source) */
	public static final String OSM_SOURCE_NODE_COLUMN   = System.getProperty("pflow.routing2.pgloader.osm_source_column",      SOURCE_NODE_COLUMN);
	/** column name of target node (default: target) */
	public static final String OSM_TARGET_NODE_COLUMN   = System.getProperty("pflow.routing2.pgloader.osm_target_column",      TARGET_NODE_COLUMN);
	/** column name of link cost (default: cost) */
	public static final String OSM_COST_COLUMN          = System.getProperty("pflow.routing2.pgloader.osm_cost_column",        COST_COLUMN);
	/** column name of reverse link cost (default: reverse_cost) */
	public static final String OSM_REVERSER_COST_COLUMN = System.getProperty("pflow.routing2.pgloader.osm_reverse_cost_column",REVERSER_COST_COLUMN);
	/** column name of LineString geometry (default: geom_way) */
	public static final String OSM_GEOMETRY_COLUMN      = System.getProperty("pflow.routing2.pgloader.osm_geometry_column",    "geom_way");

	/** column name of length (default: km) */
	public static final String OSM_LEGNTH_COLUMN        = System.getProperty("pflow.routing2.pgloader.osm_length_column",   "km");
	/** column name of velocity (default: kmh) */
	public static final String OSM_VELOCITY_COLUMN      = System.getProperty("pflow.routing2.pgloader.osm_velocity_column", "spd");
	/** column name of road type (default: clazz) */
	public static final String OSM_ROAD_TYPE_COLUMN     = System.getProperty("pflow.routing2.pgloader.osm_road_type_column","clazz");
	
	

	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create instance
	 */
	public PgOsmLoader() { 
		this(NETWORK_TABLE);
	}
	
	/**
	 * create instance with the indicated network table
	 * @param tablename table name of network data
	 */
	public PgOsmLoader(String tablename) { 
		super(tablename);
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing2.loader.APgNetworkLoader#createQuery(jp.ac.ut.csis.pflow.routing2.loader.QueryCondition) */
	@Override
	protected String createQuery(QueryCondition qc) { 
		// create SQL where clause ////////////////////////
		String cond = String.format("WHERE %s<>%s",OSM_SOURCE_NODE_COLUMN,OSM_TARGET_NODE_COLUMN);
		if( qc != null ) {
			// get bounds =============================
			Rectangle2D bounds = qc.getRects();
			
			// error handle & create query ============
			if( bounds != null ) {
				cond += String.format(" AND ST_Intersects("
										+ "%s,"
										+ "ST_SetSRID("
										+   "GEOMETRY("
										+     "ST_Buffer("
										+       "GEOGRAPHY("
										+         "ST_MakeBox2D(ST_MakePoint(%f,%f),ST_MakePoint(%f,%f))"
										+       "),"
										+     "%f)"
										+   "),"
										+ "4326)"
										+ ")",
										OSM_GEOMETRY_COLUMN,bounds.getMinX(),bounds.getMinY(),bounds.getMaxX(),bounds.getMaxY(),qc.getBuffer());
			}
		}
		// add road type //////////////////////////////////
		int roadTypes[] = qc instanceof OsmQueryCondition ? OsmQueryCondition.class.cast(qc).getRoadTypes() : null;
		if( roadTypes != null && roadTypes.length > 0 ) {
			cond += String.format(" AND %s in (%s)",OSM_ROAD_TYPE_COLUMN,StringUtils.join(ArrayUtils.toObject(roadTypes),","));
		}
		
		// constitute SQL for pgRouting table /////////////
		return String.format("select * from %s %s",getTableName(),cond);
	}

	/* @see jp.ac.ut.csis.pflow.routing2.loader.APgNetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, java.sql.Connection, java.lang.String, boolean) */
	@Override
	public Network load(Network network,Connection con,String sql,boolean needGeom) {
		// declaration of necessary instances /////////////
		try (Statement stmt = con.createStatement(); 
			 ResultSet res  = stmt.executeQuery(sql) )
		{
			while( res.next()) {
				int    gid  = res.getInt(OSM_LINK_ID_COLUMN);
				String src  = String.valueOf(res.getInt(OSM_SOURCE_NODE_COLUMN));
				String tgt  = String.valueOf(res.getInt(OSM_TARGET_NODE_COLUMN));
				double cst  = res.getDouble(OSM_LEGNTH_COLUMN);	// res.getDouble(OSM_COST_COLUMN);
				double rcst = res.getDouble(OSM_LEGNTH_COLUMN);	// res.getDouble(OSM_REVERSER_COST_COLUMN);
				int    spd  = res.getInt(OSM_VELOCITY_COLUMN);
				int    clz  = res.getInt(OSM_ROAD_TYPE_COLUMN);	// road type:: details are in osm2po.config 
				boolean way = false;
				
				Geometry   geom = PGgeometry.class.cast(res.getObject(GEOMETRY_COLUMN)).getGeometry();	// default column name created by osm2po is "geom_way" 
				LineString line = null; 
				if      ( geom instanceof LineString      ) { line = LineString.class.cast( geom );               }
				else if ( geom instanceof MultiLineString ) { line = MultiLineString.class.cast(geom).getLine(0); }
				
				// build network data =====================
				// nodes
				Point p0 = line.getPoint(0);
				Point p1 = line.getPoint(line.numPoints()-1);
				List<LonLat> list = needGeom ? parseLineString(line) : null;
				
				Node n0 = network.hasNode(src) ? network.getNode(src) : new Node(src,p0.getX(),p0.getY());
				Node n1 = network.hasNode(tgt) ? network.getNode(tgt) : new Node(tgt,p1.getX(),p1.getY());
				
				// link 
				OsmLink link = new OsmLink(String.valueOf(gid),n0,n1,cst,rcst,way,clz,spd,list);
				network.addLink(link);
			}
		}
		catch(OutOfMemoryError | SQLException exp) {
			LOGGER.error("fail to load network",exp); 
			network = null; 
		}
		return network;
	}
}
