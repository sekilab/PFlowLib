package jp.ac.ut.csis.pflow.routing2.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;
import jp.ac.ut.csis.pflow.routing2.res.Route;

/**
 * Shortest path search with Dijkstra
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Dijkstra extends ARoutingLogic {
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * initialization
	 * @param minDist minimum search distance
	 * @param linkcost link cost operator
	 */
	public Dijkstra(double minDist,LinkCost linkcost) { 
		super(1,minDist,linkcost);
	}
	
	/**
	 * initialization
	 * @param linkcost link cost operator
	 */
	public Dijkstra(LinkCost linkcost) { 
		this(MIN_DIST,linkcost);
	}
	
	/**
	 * initialization
	 */
	public Dijkstra() {
		this(null);
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing.logic.IRoutingLogic#getName() */
	@Override
	public String getName() {
		return "Dijkstra";
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoutes(jp.ac.ut.csis.pflow.routing2.res.Network, jp.ac.ut.csis.pflow.routing2.res.Node, jp.ac.ut.csis.pflow.routing2.res.Node, int) */
	@Override
	public List<Route> getRoutes(Network network,Node depnode,Node arrnode,int N) { 
		// prepare priority Queue /////////////////////////
		PriorityQueue<Knot> queue = new PriorityQueue<Knot>(network.listNodes().size(),new Comparator<Knot>() {
			public int compare(Knot knot1,Knot knot2) {	// make a lower cost knot first 
				return new Double(knot1.getCost()).compareTo(new Double(knot2.getCost()));
			}
		});
		
		Knot           knot  = new Knot(depnode);
		Map<Node,Knot> knots = new Hashtable<Node,Knot>();
		knots.put(depnode,knot);
		queue.add(knot);
		while( !queue.isEmpty() ) { 
			knot = queue.poll();
			knot.fix(true);
			if( knot.getNode().equals(arrnode) ) { break; }
			// check connecting links =====================
			for(Link link:knot.getNode().listOutLinks()) {
				boolean rev  = knot.getNode().equals(link.getHeadNode());
				Node    n    = rev ? link.getTailNode() : link.getHeadNode();
				double  cost = rev ? getLinkCost().getReverseCost(link) : getLinkCost().getCost(link);
				// update cost ++++++++++++++++++++++++++++
				if( knots.containsKey(n) ) { knots.get(n).update(knot,cost); }
				else {
					Knot k = new Knot(n,knot,cost);
					knots.put(n,k);
					queue.add(k);
				}	
			}
		}
		// extract routes(not null) ///////////////////////
		return !knot.getNode().equals(arrnode) ? new ArrayList<Route>() : Arrays.asList(knot.getRoute());
	}
		
	/**
	 * retrieve reachable routes from the position within the indicated cost
	 * @param network network data
	 * @param lon origin longitude
	 * @param lat origin latitude
	 * @param cost distance constrain in meter
	 * @return list of reachable routes
	 */
	public List<Route> getReachableRoutes(Network network,double lon,double lat,double cost) {
		// get the nearest node from the position /////////
		Node org = getNearestNode(network,lon,lat);
		// retrieve reachable routes. /////////////////////
		return org == null ? new ArrayList<Route>() : getReachableRoutes(network,org,cost);
	}
	
	/**
	 * retrieve reachable routes from the position within the indicated cost
	 * @param network network data
	 * @param depnode origin node
	 * @param cost distance constrain in meter
	 * @return list of reachable routes
	 */
	public List<Route> getReachableRoutes(Network network,Node depnode,double cost) {
		// for results //////////////////////////////////// 
		List<Route> res = new ArrayList<Route>();
		
		// get costs in network ///////////////////////////
		Map<Node,Knot> knots = getCost(network,depnode,cost);	// calculate cost to all nodes within the indicated cost
		List<Node>     nodes = new ArrayList<Node>(knots.keySet());
		while( !nodes.isEmpty() ) {
			Node  node  = nodes.get(nodes.size()-1);
			Knot  knot  = knots.get(node);
			Route route = knot.getRoute();
			for(Node n:route.listNodes()) { nodes.remove(n); }
			res.add(route);
		}
		return res;
	}
	
	/**
	 * by Dijkstra, calculate minimum costs to all nodes in the indicated network
	 * @param network network data
	 * @param depnode origin node
	 * @return result (key=Node, Value=knot including cost and routes)
	 */
	protected Map<Node,Knot> getCost(Network network,Node depnode) {
		return getCost(network,depnode,-1);
	}

	/**
	 * by Dijkstra, calculate minimum costs to all nodes in the indicated network with maximum cost constrain
	 * @param network network data
	 * @param depnode origin node
	 * @param cost cost constrain
	 * @return result (key=Node, Value=knot including cost and routes)
	 */
	protected Map<Node,Knot> getCost(Network network,Node depnode,double cost) {
		// prepare priority Queue /////////////////////////
		PriorityQueue<Knot> queue = new PriorityQueue<Knot>(network.listNodes().size(),new Comparator<Knot>() {
			public int compare(Knot knot1,Knot knot2) { 
				return new Double(knot1.getCost()).compareTo(new Double(knot2.getCost()));
			}
		});
		
		Knot           knot  = new Knot(depnode);
		Map<Node,Knot> knots = new LinkedHashMap<Node,Knot>();
		knots.put(depnode,knot);
		queue.add(knot);
		while( !queue.isEmpty() ) { 
			knot = queue.poll();
			knot.fix(true);
			if( 0 < cost && cost < knot.getCost()  ) { break; } 
			// check connecting links =====================
			for(Link link:knot.getNode().listOutLinks()) {
				boolean rev  = knot.getNode().equals(link.getHeadNode());
				Node    n    = rev ? link.getTailNode() : link.getHeadNode(); 
				double  cst  = rev ? getLinkCost().getReverseCost(link) : getLinkCost().getCost(link);
				// update cost ============================
				if( knots.containsKey(n) ) { knots.get(n).update(knot,cst); }
				else {
					Knot k = new Knot(n,knot,cst);
					knots.put(n,k);
					queue.add(k);
				}	
			}
		}
		// remove all unfixed nodes ///////////////////////
		List<Node> keys = new ArrayList<Node>(knots.keySet());
		for(int i=keys.size()-1;i>=0;i--) {
			Node key = keys.get(i);
			Knot val = knots.get(key);
			if( !val.isFixed() || (0 < cost && cost < val.getCost()) ) { knots.remove(key); }
		}
		return knots;
	}
}
