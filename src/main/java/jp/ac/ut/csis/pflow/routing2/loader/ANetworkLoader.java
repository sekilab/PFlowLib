package jp.ac.ut.csis.pflow.routing2.loader;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.res.Network;

/**
 * Abstract class for network data loader
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public abstract class ANetworkLoader implements INetworkLoader {
	/* ==============================================================
	 * static methods
	 * ============================================================== */
	/**
	 * create array data of bounding box from LonLat 
	 * @param point LonLat point
	 * @return array of bounding rectangle
	 */
	public static <T extends LonLat> double[] createRect(T point) { 
		return createRect(Arrays.asList(point));
	}

	/**
	 * create array data of bounding box from LonLat set
	 * @param points LonLat set
	 * @return array of bounding rectangle
	 */
	public static <T extends LonLat > double[] createRect(Collection<T> points) { 
		Rectangle2D.Double rect = LonLat.makeMBR(points);
		return new double[]{rect.getMinX(),rect.getMinY(),rect.getMaxX(),rect.getMaxY()};
	}
		
	
	/* ==============================================================
	 * inherit methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double, double, double, double) */
	@Override
	public Network load(double x0,double y0,double x1,double y1) {
		return load(new Network(),x0,y0,x1,y1,DEFAULT_BUFFER_SIZE);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double, double) */
	@Override
	public Network load(Network network,double x0,double y0,double x1,double y1) {
		return load(network,x0,y0,x1,y1,DEFAULT_BUFFER_SIZE);
	}
	

	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double[]) */
	@Override
	public Network load(double[] rect) {
		return load(new Network(),rect,DEFAULT_BUFFER_SIZE);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double[]) */
	@Override
	public Network load(Network network,double[] rect) {
		return load(network,rect,DEFAULT_BUFFER_SIZE);
	}
	

	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double, double, double, double, double) */
	@Override
	public Network load(double x0,double y0,double x1,double y1,double bufSize) {
		return load(new Network(),x0,y0,x1,y1,bufSize,false);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double, double, double) */
	@Override
	public Network load(Network network,double x0,double y0,double x1,double y1,double bufSize) {
		return load(network,x0,y0,x1,y1,bufSize,false);
	}

	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double[], double) */
	@Override
	public Network load(double[] rect,double bufSize) {
		return load(new Network(),rect,bufSize,false);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double[], double) */
	@Override
	public Network load(Network network,double[] rect,double bufSize) {
		return load(network,rect,bufSize,false);
	}
	
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double, double, double, double, boolean) */
	@Override
	public Network load(double x0,double y0,double x1,double y1,boolean needGeom) {
		return load(new Network(),x0,y0,x1,y1,DEFAULT_BUFFER_SIZE,needGeom);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double, double, boolean) */
	@Override
	public Network load(Network network,double x0,double y0,double x1,double y1,boolean needGeom) {
		return load(network,x0,y0,x1,y1,DEFAULT_BUFFER_SIZE,needGeom);
	}
	
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double[], boolean) */
	@Override
	public Network load(double[] rect,boolean needGeom) {
		return load(new Network(),rect,DEFAULT_BUFFER_SIZE,needGeom);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double[], boolean) */
	@Override
	public Network load(Network network,double[] rect,boolean needGeom) {
		return load(network,rect,DEFAULT_BUFFER_SIZE,needGeom);
	}
	

	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double, double, double, double, double, boolean) */
	@Override
	public Network load(double x0,double y0,double x1,double y1,double bufSize,boolean needGeom) {
		return load(new Network(),new double[]{x0,y0,x1,y1},bufSize,needGeom);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double, double, double, boolean) */
	@Override
	public Network load(Network network,double x0,double y0,double x1,double y1,double bufSize,boolean needGeom) {
		return load(network,new double[]{x0,y0,x1,y1},bufSize,needGeom);
	}
	
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(double[], boolean) */
	@Override
	public Network load(double[] rect,double bufSize,boolean needGeom) {
		return load(new Network(),new QueryCondition(rect,bufSize,needGeom));
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, double[], double, boolean) */
	@Override
	public Network load(Network network,double[] rect,double bufSize,boolean needGeom) {
		return load(network,new QueryCondition(rect,bufSize,needGeom));
	}
	
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.loader.QueryCondition) */
	@Override
	public Network load(QueryCondition cond) { 
		return load(new Network(),new QueryCondition[]{cond});
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network, jp.ac.ut.csis.pflow.routing2.loader.QueryCondition) */
	@Override
	public Network load(Network network,QueryCondition cond) { 
		return load(network,new QueryCondition[]{cond});
	}
	
	
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load() */
	@Override
	public Network load() {
		QueryCondition[] conds = null;
		return load(new Network(),conds);
	}
	/* @see jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader#load(jp.ac.ut.csis.pflow.routing2.res.Network) */
	@Override
	public Network load(Network network) {
		QueryCondition[] conds = null;
		return load(network,conds);
	}
}
