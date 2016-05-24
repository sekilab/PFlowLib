package jp.ac.ut.csis.pflow.routing2.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.DistanceUtils;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.loader.INetworkLoader;
import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;
import jp.ac.ut.csis.pflow.routing2.res.Route;

/**
 * Abstract class for routing logic
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public abstract class ARoutingLogic implements IRoutingLogic {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** default route count				*/	
	public static final int     ROUTE_NUM  = Integer.getInteger("pflow.routing2.logic.route_num",5);
	
	/** default search distance in meter*/	
	public static final double  MIN_DIST   = Double.parseDouble(System.getProperty("pflow.routing2.logic.min_dist","3000"));	
	
	/** approximate 1 km in degree */
	private static final double APPROX_1KM = INetworkLoader.APPROX_1KM;
	
	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** the number of explore routes */	private int      _routeNum;
	/** the minimum search distance  */	private double   _minDist;
	/** link cost calculator         */	private LinkCost _linkcost;
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create instance with route count and minimum search distance
	 * @param routeNum route count to query
	 * @param minDist minimum search distance(meter)
	 * @param linkcost link cost operator
	 */
	protected ARoutingLogic(int routeNum,double minDist,LinkCost linkcost) { 
		_routeNum = routeNum;
		_minDist  = minDist;
		_linkcost = linkcost == null ? new LinkCost() : linkcost;
	}
	
	/**
	 * create instance with route count and minimum search distance
	 * @param routeNum route count to query
	 * @param minDist minimum search distance(meter)
	 */
	protected ARoutingLogic(int routeNum,double minDist) {
		this(routeNum,minDist,null);
	}
	
	/**
	 * create instance with the specified number of explore routes
	 * @param routeNum the default number of explore routes
	 */
	protected ARoutingLogic(int routeNum) {
		this(routeNum,MIN_DIST);
	}
	
	/**
	 * create instance with default values(routeNum=1, minDist=1)
	 */
	protected ARoutingLogic() {
		this(ROUTE_NUM);
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoutes(jp.ac.ut.csis.pflow.routing2.res.Network, java.lang.String, java.lang.String, int) */
	@Override
	public List<Route> getRoutes(Network network,String depnodeid,String arrnodeid,int n) {
		Node dep = network.getNode(depnodeid);
		Node arr = network.getNode(arrnodeid);
		return getRoutes(network,dep,arr,n);
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoutes(jp.ac.ut.csis.pflow.routing2.res.Network, java.lang.String, java.lang.String) */
	@Override
	public List<Route> getRoutes(Network network,String depnodeid,String arrnodeid) {
		return getRoutes(network,depnodeid,arrnodeid,getRouteNum());
	}

	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoute(jp.ac.ut.csis.pflow.routing2.res.Network, java.lang.String, java.lang.String) */
	@Override
	public Route getRoute(Network network,String depnodeid,String arrnodeid) {
		List<Route> routes = getRoutes(network,depnodeid,arrnodeid,1);
		return routes == null || routes.isEmpty() ? null : routes.get(0);
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoutes(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double, double, int) */
	@Override
	public List<Route> getRoutes(Network network,double depx,double depy,double arrx,double arry,int n) {
		Node n0 = getNearestNode(network,depx,depy);
		Node n1 = getNearestNode(network,arrx,arry);
		return n0 == null || n1 == null ? new ArrayList<Route>() : getRoutes(network,n0,n1,n);
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoutes(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double, double) */
	@Override
	public List<Route> getRoutes(Network network,double depx,double depy,double arrx,double arry) {
		return getRoutes(network,depx,depy,arrx,arry,getRouteNum());
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoute(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double, double) */
	@Override
	public Route getRoute(Network network,double depx,double depy,double arrx,double arry) {
		List<Route> routes = getRoutes(network,depx,depy,arrx,arry,1);
		return routes == null || routes.isEmpty() ? null : routes.get(0);
	}

	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoutes(jp.ac.ut.csis.pflow.routing2.res.Network, jp.ac.ut.csis.pflow.routing2.res.Node, jp.ac.ut.csis.pflow.routing2.res.Node) */
	@Override
	public List<Route> getRoutes(Network network,Node depnode,Node arrnode) {
		return getRoutes(network,depnode,arrnode,getRouteNum());
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoute(jp.ac.ut.csis.pflow.routing2.res.Network, jp.ac.ut.csis.pflow.routing2.res.Node, jp.ac.ut.csis.pflow.routing2.res.Node) */
	@Override
	public Route getRoute(Network network,Node depnode,Node arrnode) {
		List<Route> routes = getRoutes(network,depnode,arrnode,1);
		return routes == null || routes.isEmpty() ? null : routes.get(0);
	}

	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getNearestNode(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double) */
	@Override
	public Node getNearestNode(Network network, double x, double y,double mindist) {
		Node   node = null;
		double dist = mindist;
		double w    = mindist*APPROX_1KM;
		double h    = mindist*APPROX_1KM;
		for(Node n:network.queryNode(x-w,y-h,x+w,y+h)) { 
			double d = DistanceUtils.distance(x,y,n.getLon(),n.getLat());
			if( d < dist  ) {
				node = n;
				dist = d;
			}
		}
		return node;
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getNearestNode(jp.ac.ut.csis.pflow.routing2.res.Network, double, double) */
	@Override
	public Node getNearestNode(Network network,double x,double y) {
		return getNearestNode(network,x,y,getSearchDistance());
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getNearestLink(jp.ac.ut.csis.pflow.routing2.res.Network, double, double, double) */
	@Override
	public Link getNearestLink(Network network, double x, double y,double mindist) {
		Link   link  = null;
		LonLat point = new LonLat(x,y); 
		double dist  = mindist;
		for(Link L:network.listLinks()) {
			List<LonLat> geom = L.getLineString();
			double       d    = geom == null ? DistanceUtils.distance(L.getTailNode(),L.getHeadNode(),point) :
				                               DistanceUtils.distance(geom,point);
			if( d < dist  ) {
				link = L;
				dist = d;
			}
		}
		return link;
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getNearestLink(jp.ac.ut.csis.pflow.routing2.res.Network, double, double) */
	@Override
	public Link getNearestLink(Network network,double x,double y) {
		return getNearestLink(network,x,y,Double.MAX_VALUE);
	}
	
	/**
	 * get default number of routes to explore
	 * @return number of routes
	 */
	public int getRouteNum() {
		return _routeNum;
	}
	
	/**
	 * set default number of routes to explore
	 * @param routeNum number of routes
	 */
	public void setRouteNum(int routeNum) {
		_routeNum = routeNum;
	}
	
	/**
	 * get minimum search distance in meter
	 * @return search distance
	 */
	public double getSearchDistance() { 
		return _minDist;
	}
	
	/**
	 * set minimum search distance in meter
	 * @param minDist minimum search distance 
	 */
	public void setSearchDistance(double minDist) { 
		_minDist = minDist;
	}
	
	/**
	 * get link cost
	 * @param link link cost instance
	 * @return link cost
	 */
	public LinkCost getLinkCost() { 
		return _linkcost; 
	}
	/**
	 * set link cost operator
	 * @param linkcost link cost operator
	 */
	public void setLinkCost(LinkCost linkcost) { 
		_linkcost = linkcost;
	}
	
	/**
	 * assign line geometry to node list of routing result
	 * @param network network used in route search
	 * @param route route search result
	 * @return route lien geometry. return null if failed
	 */
	public List<LonLat> fillRouteGeometry(Network network,Route route) {
		// error handle ///////////////////////////////////
		List<LonLat> output = new ArrayList<LonLat>();
		if( route == null || route.listNodes().isEmpty() ) { return null; }
		// initialize /////////////////////////////////////
		List<Node> nodes = route.listNodes();
		int        N     = nodes.size();
		Node       n0    = nodes.get(0);
		for(int i=1;i<N;i++) {
			Node    n1   = nodes.get(i);
			Link    link = network.getLink(n0,n1);
			// constitute geometry ========================
			List<LonLat> line = new ArrayList<LonLat>(link.getLineString());
			if( !link.getHeadNode().equals(n1) ) { Collections.reverse(line); }
			output.addAll(line);
			n0 = n1;
		}
		return output;
	}

	
	/* ==============================================================
	 * inner classes 
	 * ============================================================== */
	/** temporary class for route search */
	protected class Knot {
		/* instance fields ---------------------- */
		/** node		*/	private Node    __node;
		/** from node	*/	private Knot    __from;
		/** cost		*/	private double  __cost;
		/** fixed		*/	private boolean __fixed;
		/* constructors ------------------------- */
		/**
		 * initialization
		 * @param node
		 */
		protected Knot(Node node) {
			this(node,null,0d);
		}
		/**
		 * initialization
		 * @param node node
		 * @param from previous knot
		 * @param cost link cost from the previous knot
		 */
		protected Knot(Node node,Knot from,double cost) {
			__node  = node;
			__from  = from;
			__cost  = from == null ? cost : from.getCost() + cost;
			__fixed = false;
		}
		/* instance methods --------------------- */
		/**
		 * check if the cost of this node is fixed
		 * @return result
		 */
		protected boolean isFixed() {
			return __fixed;
		}
		/**
		 * update cost and route 
		 * @param knot previous knot
		 * @param link link from the previous knot
		 */
		protected void update(Knot knot,double linkcost) {
			if( (knot.getCost() + linkcost) < getCost() ) {
				__from = knot;
				__cost = knot.getCost()+linkcost;
			}
		}
		/**
		 * update status
		 * @param flag set true if this node is fixed
		 */
		protected void fix(boolean flag) {
			__fixed = flag;
		}
		/**
		 * get route to this knot
		 * @return route
		 */
		protected Route getRoute() {
			List<Node> list = new ArrayList<Node>();
			list.add(getNode());
			
			Knot knot = __from;
			while(knot!=null) {
				list.add(0,knot.getNode());
				knot = knot.getFrom(); 
			}			
			return new Route(list,getCost());
		}
		/**
		 * get the previous knot
		 * @return previous knot
		 */
		protected Knot getFrom() {
			return __from;
		}
		/**
		 * get the cost to this knot
		 * @return cost
		 */
		protected double getCost() {
			return __cost;
		}
		/**
		 * get node 
		 * @return node
		 */
		protected Node getNode() {
			return __node;
		}
	}
}
