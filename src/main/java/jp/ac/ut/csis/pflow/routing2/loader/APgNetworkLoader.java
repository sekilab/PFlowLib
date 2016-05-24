package jp.ac.ut.csis.pflow.routing2.loader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.LineString;
import org.postgis.MultiLineString;
import org.postgis.PGgeometry;

import jp.ac.ut.csis.pflow.dbi.PgLoader;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;

/**
 * Class for loading network data from PostgreSQL/PostGIS
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public abstract class APgNetworkLoader extends ANetworkLoader {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Logger */
	private static final Logger LOGGER = LogManager.getLogger(APgNetworkLoader.class);
	
	/** column name of LineString geometry (default: geom) */
	public static final String GEOMETRY_COLUMN      = System.getProperty("pflow.routing2.pgloader.geometry_column", "geom"  );
	/** column name of link id (default: gid) */
	public static final String LINK_ID_COLUMN       = System.getProperty("pflow.routing2.pgloader.link_id_column",  "gid"   );
	/** column name of source node (default: source) */
	public static final String SOURCE_NODE_COLUMN   = System.getProperty("pflow.routing2.pgloader.source_column",   "source");
	/** column name of target node (default: target) */
	public static final String TARGET_NODE_COLUMN   = System.getProperty("pflow.routing2.pgloader.target_column",   "target");
	/** column name of link cost (default: cost) */
	public static final String COST_COLUMN          = System.getProperty("pflow.routing2.pgloader.cost_column",     "cost"  );
	/** column name of reverse link cost (default: reverse_cost) */
	public static final String REVERSER_COST_COLUMN = System.getProperty("pflow.routing2.pgloader.reverse_cost_column","reverse_cost");
	
	
	
	/* ==============================================================
	 * static methods
	 * ============================================================== */	
	/**
	 * set link geometry from PostGIS LineString
	 * @param geomLineString LineString 
	 * @return point list
	 */
	public static List<LonLat> parseLineString(LineString geomLineString) {
		List<LonLat> linestring = new ArrayList<LonLat>();
		for(org.postgis.Point p:geomLineString.getPoints()) {
			linestring.add(new LonLat(p.getX(),p.getY()));
		}
		return linestring;
	}
	

	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** table name	*/	private String _tablename;
	

	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create instance with the indicated table
	 * @param tablename table name
	 */
	protected APgNetworkLoader(String tablename) { 
		super();
		// table name /////////////////////////////////////
		_tablename = tablename;
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get table name
	 * @return table name
	 */
	public String getTableName() {
		return _tablename;
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.ANetworkLoader#load(jp.ac.ut.csis.pflow.routing2.loader.ANetworkLoader.QueryCondition[]) */
	@Override
	public Network load(QueryCondition[] conds) {
		return load(new Network(),conds);
	}
	
	@Override
	public Network load(Network network,QueryCondition[] conds) {
		// variable declaration ///////////////////////////
		PgLoader loader  = new PgLoader();
		try (Connection con = loader.getConnection()) { // DBCPLoader.getPgSQLConnection()) {
			// generate DB connection =====================
			if( con == null ) { 
				network = null; 
			}
			// execute query ==============================
			else {
				network = load(con,conds);
			}
		}
		catch(SQLException exp) { 
			LOGGER.error("fail to load network",exp); 
		}
		finally { 
			loader.close(); 
		}
		// returns result /////////////////////////////////
		return network;
	}
	
	/**
	 * load network data within the indicated area
	 * @param con DB connection instance
	 * @param x0 minimum longitude
	 * @param y0 minimum latitude
	 * @param x1 maximum longitude
	 * @param y1 maximum latitude
	 * @param neeeGeom flag if include geometry
	 * @return network data, return NULL if failed
	 */
	public Network load(Connection con,double x0,double y0,double x1,double y1,boolean needGeom) {
		return load(con,new double[]{x0,y0,x1,y1},needGeom);	
	}
	
	/**
	 * load network data within the indicated areas
	 * @param con DB connection instance
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param neeeGeom flag if include geometry
	 * @return network data, return NULL if failed
	 */
	public Network load(Connection con,double[] rect,boolean needGeom) {
		return load(con,new QueryCondition(rect,DEFAULT_BUFFER_SIZE,needGeom));
	}	
	
	/**
	 * load network data within the indicated areas
	 * @param con DB connection instance
	 * @param rect rectangle region {x0,y0,x1,y1}
	 * @param bufSize buffer size in meter
	 * @param neeeGeom flag if include geometry
	 * @return network data, return NULL if failed
	 */
	public Network load(Connection con,double[] rect,double bufSize,boolean needGeom) {
		return load(con,new QueryCondition(rect,bufSize,needGeom));
	}

	/**
	 * load network data within the indicated areas
	 * @param con DB connection instance
	 * @param cond Query condition
	 * @param neeeGeom flag if include geometry
	 * @return network data, return NULL if failed
	 */
	public Network load(Connection con,QueryCondition cond) {
		return load(con,new QueryCondition[]{cond});
	}
	
	/**
	 * load network data within the indicated areas
	 * @param con DB connection instance
	 * @param cond Query condition
	 * @param neeeGeom flag if include geometry
	 * @return network data, return NULL if failed
	 */
	public Network load(Network network,Connection con,QueryCondition cond) {
		return load(network,con,new QueryCondition[]{cond});
	}
	
	/**
	 * load network data within the indicated areas
	 * @param con DB connection instance
	 * @param conds query conditions
	 * @param neeeGeom flag if include geometry
	 * @return network data, return NULL if failed
	 */
	public Network load(Connection con,QueryCondition[] conds) {
		return load(new Network(),con,conds);
	}
	
	/**
	 * load network data within the indicated areas
	 * @param network network instance to add data 
	 * @param con DB connection instance
	 * @param conds query conditions
	 * @param neeeGeom flag if include geometry
	 * @return network data, return NULL if failed
	 */
	public Network load(Network network,Connection con,QueryCondition[] conds) {
		// load data with the specified condition
		if( conds != null && conds.length > 0 ) {
			for(QueryCondition cond:conds) { 
				String sql = createQuery(cond); 
				network    = load(network,con,sql,cond.needGeom());
			}
		}
		// load all data
		else {
			String sql = createQuery(null);
			network = load(network,con,sql,true);
		}
		return network;
	}
	
	/**
	 * create SQL query from the indicated query condition
	 * @param qc query condition
	 * @return SQL query string
	 */
	protected abstract String createQuery(QueryCondition cond);
	
	/**
	 * load DRM road network from PostgreSQL DB
	 * @param network network instance to add data
	 * @param con DB connection
	 * @param sql SQL query command
	 * @param needGeom flag for involving link geometry
	 * @return updated network data
	 */
	public abstract Network load(Network network,Connection con,String sql,boolean needGeom);
	
	/**
	 * load DRM road network from PostgreSQL DB
	 * @param con DB connection
	 * @param sql SQL query command
	 * @param needGeom flag for involving link geometry
	 * @return updated network data
	 */
	public Network load(Connection con,String sql,boolean needGeom) {
		return load(new Network(),con,sql,needGeom);
	}
	
	/**
	 * fill geometries to node network
	 * @param con DB connection
	 * @param nodes node list (routing result)
	 * @return point list with geometry, return NULL if failed
	 */
	public List<LonLat> fillGeometry(Connection con,List<Node> nodes) { 
		List<LonLat> points = new ArrayList<LonLat>();
		
		try (Statement stmt = con.createStatement()){
			int  N  = nodes.size();
			Node n0 = nodes.get(0);
			for(int i=1;i<N;i++) { 
				Node   n1  = nodes.get(i);
				String sql = String.format("select %s,%s,%s from %s "
											+ "where (source='%s' and target='%s') OR (source='%s' and target='%s') ",
											SOURCE_NODE_COLUMN,TARGET_NODE_COLUMN,GEOMETRY_COLUMN,getTableName(),
											n0.getNodeID(),n1.getNodeID(),n1.getNodeID(),n0.getNodeID());

				try (ResultSet res = stmt.executeQuery(sql) ) {
					if( res.next() ) { 
						String     src  = res.getString(SOURCE_NODE_COLUMN);
						Geometry   geom = PGgeometry.class.cast(res.getObject(GEOMETRY_COLUMN)).getGeometry();
						LineString line = null; 
						if      ( geom instanceof LineString      ) { line = LineString.class.cast( geom );               }
						else if ( geom instanceof MultiLineString ) { line = MultiLineString.class.cast(geom).getLine(0); }
						
						if( src.equals(n0.getNodeID()) ) { points.addAll(parseLineString(line)); }
						else { points.addAll(parseLineString(line.reverse())); }
					}
				}
				catch(SQLException exp) { 
					LOGGER.error("fail to load geometry",exp); 
				}
				n0 = n1;
			}
		}
		catch(OutOfMemoryError | SQLException exp) { 
			LOGGER.error("fail to load geometry",exp); 
			points = null; 
		}
		finally { 
			System.gc(); 
		}
		return points;
	}
}
