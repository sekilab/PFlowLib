package jp.ac.ut.csis.pflow.dbi;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DB Connection Pool for PostgreSQL
 * 
 * <pre>
 * [Command Sample for Win]
 * ## all system properties assigned in the following command are default and can be omitted
 * java -Xmx1G -classpath .;lib\*;bin ^ 
 *      -Dpflow.pgloader.host=localhost ^  
 *      -Dpflow.pgloader.port=5432 ^
 *      -Dpflow.pgloader.userid=postgres ^
 *      -Dpflow.pgloader.userpw=postgres ^
 *      -Dpflow.pgloader.dbname=pflowdb ^
 *      -Dpflow.pgloader.encoding=UTF-8 ^
 *      jp.ac.ut.csis.pflow.dbi.PgLoader
 * </pre>
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public final class PgLoader {	
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Logger */
	private static final Logger LOGGER = LogManager.getLogger(PgLoader.class);
	
	
	/** JDBC Driver for PostgreSQL */
	public static final String PGSQL_DRIVER = "org.postgresql.Driver";	 
	
	/** Database host. System Property(key=pflow.pgloader.host, default value=localhost) */	
	public static final String DB_HOST 		= System.getProperty("pflow.pgloader.host",    "localhost");
	
	/** Database login user. System Property(key=pflow.pgloader.userid, default value=postgres) */	
	public static final String DB_USER 		= System.getProperty("pflow.pgloader.userid",  "postgres" );
	
	/** Database login password. System Property(key=pflow.pgloader.userpw, default value=postgres)  */	
	public static final String DB_PASS 		= System.getProperty("pflow.pgloader.userpw",  "postgres" );
	
	/** Database name. System Property(key=pflow.pgloader.dbname, default value=pflowdb) */
	public static final String DB_NAME		= System.getProperty("pflow.pgloader.dbname",  "pflowdb" );
	
	/** Database access port. System Property(key=pflow.pgloader.port, default value=5432) */	
	public static final int    DB_PORT		= Integer.getInteger("pflow.pgloader.port",    5432       );
	
	/** Database encoding. System Property(key=pflow.pgloader.encoding, default value=UTF-8) */
	public static final String DB_ENCODING  = System.getProperty("pflow.pgloader.encoding","UTF-8"    );

	/** Pooling connection size. System Property(key=pflow.pgloader.pool_size, default value=100) */
	public static final int    POOL_SIZE    = Integer.getInteger("pflow.pgloader.pool_size",100);

	
	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** 
	 * connection pool for PostgreSQL	
	 */	
	private BasicDataSource _dbPool = null;
	
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * constructor (with default property values)
	 */
	public PgLoader() {
		this(DB_HOST,DB_PORT,DB_USER,DB_PASS,DB_NAME,DB_ENCODING,POOL_SIZE);
	}
	
	/**
	 * constructor
	 * @param host host name
	 * @param id user id
	 * @param pw user password
	 * @param dbname database name
	 */
	public PgLoader(String host,String id,String pw,String dbname) {
		this(host,DB_PORT,id,pw,dbname,DB_ENCODING,POOL_SIZE);
	}

	/**
	 * constructor
	 * @param host host name
	 * @param port port number
	 * @param id user id
	 * @param pw user password
	 * @param dbname database name
	 */
	public PgLoader(String host,int port,String id,String pw,String dbname) {
		this(host,port,id,pw,dbname,DB_ENCODING,POOL_SIZE);
	}

	/**
	 * constructor
	 * @param host host name
	 * @param port port number
	 * @param id user id
	 * @param pw user password
	 * @param dbname database name
	 * @param encoding database encoding
	 */
	public PgLoader(String host,int port,String id,String pw,String dbname,String encoding) {
		this(host,port,id,pw,dbname,encoding,POOL_SIZE);
	}
	
	/**
	 * constructor
	 * @param host host name
	 * @param port port number
	 * @param id user id
	 * @param pw user password
	 * @param dbname database name
	 * @param encoding database encoding
	 * @param maxNum maximum number of pools
	 */
	public PgLoader(String host,int port,String id,String pw,String dbname,String encoding,int maxNum) {
		// initialization /////////////////////////////////
		_dbPool = new BasicDataSource();
		_dbPool.setDriverClassName(PGSQL_DRIVER);
		_dbPool.setUsername(id);
		_dbPool.setPassword(pw);
		_dbPool.setUrl(String.format("jdbc:postgresql://%s:%d/%s?charSet=%s",host,port,dbname,encoding));
		_dbPool.setMaxActive(maxNum);
	}
	
	/**
	 * constructor 
	 * @param dbPool DB connection pool instance for PostgreSQL
	 */
	public PgLoader(BasicDataSource dbPool) { 
		_dbPool = dbPool;
	}

	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	
	/**
	 * make connection to PostgreSQL
	 * @return DB connection
	 */
	public synchronized Connection getConnection() {
		// require initialization first ///////////////////
		if( _dbPool == null ) {
			LOGGER.error("you should initialize connection pool first"); 
			return null;
		}
		
		// get connection /////////////////////////////////
		Connection con = null;
		try {
			con = _dbPool.getConnection(); 
		}
		catch(SQLException exp) { 
			LOGGER.error("fail to get DB connection",exp);
			con = null;
		}
		return con;
	}
	
	/**
	 * Close connection pool
	 */
	public void close() {
		try {
			if( _dbPool != null ) { _dbPool.close(); } 
		}
		catch(SQLException exp) { LOGGER.error("fail to close DB connection",exp); }
	}
}
