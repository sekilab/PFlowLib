package jp.ac.ut.csis.pflow.routing2.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

import jp.ac.ut.csis.pflow.geom.DistanceUtils;
import jp.ac.ut.csis.pflow.geom.GeometryUtils;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader;

/**
 * Class for link/node network
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Network {
	/* ==============================================================
	 * static fields 0.012/1000d
	 * ============================================================== */
	/** approximate 1 km in degree */
	private static final double APPROX_1KM = INetworkLoader.APPROX_1KM; // 0.012/1000d;

	
	/* ==============================================================
	 * instance fields 
	 * ============================================================== */
	/** list for nodes	*/	private Map<String,Node> _nodes;
	/** list for links	*/	private Map<String,Link> _links;
	/** index for nodes	*/	private SpatialIndex     _nodeIndex;
	/** index for links	*/	private SpatialIndex     _linkIndex;
	

	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create new network with default setting(for route search)
	 */
	public Network() {
		this(true,true);
	}
	
	/**
	 * create new network with index settings
	 * @param makeNodeIndex flag for spatial index of road nodes
	 * @param makeLinkIndex flag for spatial index of road links 
	 */
	public Network(boolean makeNodeIndex,boolean makeLinkIndex) {
		_nodes     = new HashMap<String,Node>();
		_links     = new HashMap<String,Link>();
		_nodeIndex = makeNodeIndex ? new STRtree() : null;
		_linkIndex = makeLinkIndex ? new STRtree() : null;
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * check if network has a node or a link
	 * @return result
	 */
	public boolean isEmpty() { 
		return _nodes.isEmpty() || _links.isEmpty();
	}
	
	/**
	 * returns node list
	 * @return node list
	 */
	public List<Node> listNodes() {
		return new ArrayList<Node>(_nodes.values());
	}
	
	/**
	 * add link
	 * @param link link
	 */
	public void addLink(Link link) {
		Node tail = link.getTailNode();
		Node head = link.getHeadNode();
		if(!_nodes.containsKey(tail.getNodeID())) { _nodes.put(tail.getNodeID(),tail); addIndex(tail); }  
		if(!_nodes.containsKey(head.getNodeID())) { _nodes.put(head.getNodeID(),head); addIndex(head); }
		
		if(!_links.containsKey(link.getLinkID())) { _links.put(link.getLinkID(),link); addIndex(link); } 
	}
	
	/**
	 * add node 
	 * @param node node
	 */
	public void addNode(Node node) { 
		if(!_nodes.containsKey(node.getNodeID())) { _nodes.put(node.getNodeID(),node); addIndex(node); }  
	}
	
	/**
	 * add index 
	 * @param node node
	 */
	private void addIndex(Node node) { 
		if( _nodeIndex != null && node.isValid() ) {
			Point            point    = GeometryUtils.createPoint(node.getLon(),node.getLat());
			PreparedGeometry prepgeom = PreparedGeometryFactory.prepare(point);
			Envelope         envelope = prepgeom.getGeometry().getEnvelopeInternal();
			_nodeIndex.insert(envelope,node);
		}
	}
	
	/**
	 * query nodes within the indicated bound
	 * @param x0 min x
	 * @param y0 min y
	 * @param x1 max x 
	 * @param y1 max y
	 * @return node list
	 * @deprecated use {@link #queryNode(double, double, double, double)}
	 */
	public List<Node> query(double x0,double y0,double x1,double y1) {
		return queryNode(x0,y0,x1,y1);
	}
	
	/**
	 * query nodes within the indicated bound
	 * @param x0 min x
	 * @param y0 min y
	 * @param x1 max x 
	 * @param y1 max y
	 * @return node list
	 */
	@SuppressWarnings("unchecked")	
	public List<Node> queryNode(double x0,double y0,double x1,double y1) {
		// case index unavailable
		if( _nodeIndex == null ) {
			List<Node>  nodes  = new ArrayList<Node>();
			Polygon     bounds = GeometryUtils.createPolygon(new LonLat[]{
																new LonLat(x0,y0),
																new LonLat(x0,y1),
																new LonLat(x1,y1),
																new LonLat(x1,y0),
																new LonLat(x0,y0)
															});
			for(Node node:listNodes()) { 
				Point point = GeometryUtils.createPoint(node.getLon(),node.getLat());
				if( bounds.intersects(point) ) {
					nodes.add(node);
				}
			}
			return nodes;
		}
		// case index available
		else {
			Envelope   search = new Envelope(x0,x1,y0,y1);	// CAUTION: parameter order
			List<Node> nodes  = _nodeIndex.query(search);
			
			return nodes;
		}
	}
	
	/**
	 * query nodes within the indicated radius
	 * @param x longitude
	 * @param y latitude
	 * @param r radius
	 * @return nodes within radius
	 * @deprecated see {@link #queryNode(double, double, double, double)}
	 */
	public List<Node> query(double x,double y,double r) { 
		return queryNode(x,y,r);
	}
	
	/**
	 * query nodes within the indicated radius
	 * @param x longitude
	 * @param y latitude
	 * @param r radius
	 * @return nodes within radius
	 */
	public List<Node> queryNode(double x,double y,double r) { 
		double w  = r*APPROX_1KM;
		double h  = r*APPROX_1KM;
		
		LonLat         cntr  = new LonLat(x,y);
		List<Node>     nodes = queryNode(x-w,y-h,x+w,y+h);
		Iterator<Node> itr   = nodes.iterator();
		while(itr.hasNext()) {
			Node node = itr.next();
			if( DistanceUtils.distance(cntr,node) > r ) { itr.remove(); }
		}
		return nodes;
	}
	
	/**
	 * add index 
	 * @param link link
	 */
	private void addIndex(Link link) { 
		if( _linkIndex != null ) {
			LineString linestring = null;
			// when link has geometry /////////////////////
			if( link.hasGeometry() ) {
				linestring = GeometryUtils.createLineString(link.getLineString());
			}

			// add link geometry to spatial index /////////
			if( linestring != null ) { 
				PreparedGeometry prepgeom   = PreparedGeometryFactory.prepare(linestring);
				Envelope         envelope   = prepgeom.getGeometry().getEnvelopeInternal();
				_linkIndex.insert(envelope,link);
			}
		}
	}
	
	/**
	 * query links with the indicated bounds
	 * @param x0 left lower longitude of bounding rectangle
	 * @param y0 left lower latitude of bounding rectangle
	 * @param x1 right upper longitude of bounding rectangle
	 * @param y1 right upper latitude of bounding rectangle
	 * @return links
	 */
	@SuppressWarnings("unchecked")
	public List<Link> queryLink(double x0,double y0,double x1,double y1) {
		// case spatial indexing unavailable //////////////
		if( _linkIndex == null ) {
			List<Link>  links  = new ArrayList<Link>();
			Polygon     bounds = GeometryUtils.createPolygon(new LonLat[]{
																new LonLat(x0,y0),
																new LonLat(x0,y1),
																new LonLat(x1,y1),
																new LonLat(x1,y0),
																new LonLat(x0,y0)
															});
			for(Link link:listLinks()) { 
				LineString line = GeometryUtils.createLineString(link.getLineString());
				if( bounds.intersects(line) ) {
					links.add(link);
				}
			}
			return links;
		}
		// case spatial indexing available ////////////////
		else {
			Envelope   search = new Envelope(x0,x1,y0,y1);	// CAUTION: parameter order
			List<Link> links  = _linkIndex.query(search);
			
			return links;
		}
	}
	
	/**
	 * query links within the indicated radius
	 * @param x center longitude
	 * @param y center latitude
	 * @param r radius
	 * @return links
	 */
	public List<Link> queryLink(double x,double y,double r) { 
		double w  = r*APPROX_1KM;
		double h  = r*APPROX_1KM;
		
		LonLat         cntr  = new LonLat(x,y);
		List<Link>     links = queryLink(x-w,y-h,x+w,y+h);
		Iterator<Link> itr   = links.iterator();
		while(itr.hasNext()) {
			Link link = itr.next();
			if( DistanceUtils.distance(link.getLineString(),cntr) > r ) { itr.remove(); }
		}
		return links;
	}
	
	/**
	 * returns link list
	 * @return link list
	 */
	public List<Link> listLinks() {
		return new ArrayList<Link>(_links.values());
	}
	
	/**
	 * clear all network contents
	 */
	public void clear() { 
		_links.clear();
		_nodes.clear();
		if( _nodeIndex != null ) { _nodeIndex = new STRtree(); }
		if( _linkIndex != null ) { _linkIndex = new STRtree(); }
	}
	
	/**
	 * remove node. in/out flow links are removed as well
	 * @param node target node to remove
	 */
	public void remove(Node node) {
		// remove in-flow links ///////////////////////////
		for(Link link:node.listInLinks()) { 
			_links.remove(link.getLinkID()); //removeIndex(link);
		}
		// remove out-flow links //////////////////////////
		for(Link link:node.listOutLinks()) { 
			_links.remove(link.getLinkID()); //removeIndex(link);
		}
		// remove the node ////////////////////////////////
		_nodes.remove(node.getNodeID()); //removeIndex(node);
	}

	/**
	 * remove link. head/tail nodes are removed as well
	 * @param link target link to remove
	 */
	public void remove(Link link) {
		// remove in-flow link from head node /////////////
		Node head = link.getHeadNode();
		head.removeInLink(link);
		if( !link.isOneWay() ) {  head.removeOutLink(link); }
		if( head.isIsolated() ) { _nodes.remove(head.getNodeID()); } // removeIndex(head); }
		
		// remove out-flow link from tail node //////////// 
		Node tail = link.getTailNode();
		tail.removeOutLink(link);
		if( !link.isOneWay() ) {  tail.removeInLink(link); }
		if( tail.isIsolated() ) { _nodes.remove(tail.getNodeID()); } // removeIndex(tail); }
		
		// remove the link ////////////////////////////////
		_links.remove(link.getLinkID()); // removeIndex(link);
	}
	
	/**
	 * get Node with the specified ID
	 * @param id node ID
	 * @return returns node instance if exists, otherwise null
	 */
	public Node getNode(String id) {
		return _nodes.get(id);
	}
	
	/**
	 * check if a node with the specified ID exists.
	 * @param id node id
	 * @return returns true if exists, otherwise false
	 */
	public boolean hasNode(String id) {
		return _nodes.containsKey(id);
	}
	
	/**
	 * get link with the specified ID
	 * @param id link ID
	 * @return returns link instance if exists, otherwise null
	 */
	public Link getLink(String id) { 
		return _links.get(id);
	}
	
	/**
	 * get link with the specified nodes
	 * @param tail origin/tail node
	 * @param head destination/head node
	 * @return link if the node pair exists, otherwise null
	 */
	public Link getLink(Node tail,Node head) {
		for(Link link:tail.listOutLinks()) {
			Node h = link.getHeadNode();
			Node t = link.getTailNode();
			if( h.equals(head) || (t.equals(head) && !link.isOneWay()) ) { return link; }
		}
		return null;
	}
}
