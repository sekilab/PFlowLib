package jp.ac.ut.csis.pflow.routing2.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;
import jp.ac.ut.csis.pflow.routing2.res.Route;

/**
 * Class for Penalty Method
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Penalty extends ARoutingLogic {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** default increase ratio(0.1)	*/	public static final double INCREASE_RATIO = 0.1d;
	
	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** increase ratio	*/	private double _ratio;
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create instance with default ratio
	 */
	public Penalty() {
		this(INCREASE_RATIO);
	}
	
	/**
	 * create instance with the indicated parameters
	 * @param ratio increase ratio
	 */
	public Penalty(double ratio) {
		this(null,ratio);
	}

	/**
	 * create instance with the indicated parameters
	 * @param linkcost link cost operator
	 */
	public Penalty(LinkCost linkcost) {
		this(ROUTE_NUM,linkcost,INCREASE_RATIO);
	}
	
	/**
	 * create instance with the indicated parameters
	 * @param linkcost link cost operator
	 * @param ratio increase ratio
	 */
	public Penalty(LinkCost linkcost,double ratio) {
		this(ROUTE_NUM,linkcost,ratio);
	}
	
	/**
	 * create instance with the indicated parameters
	 * @param routeNum number of routes
	 * @param linkcost link cost operator
	 * @param ratio increase ratio
	 */
	public Penalty(int routeNum,LinkCost linkcost,double ratio) {
		this(routeNum,MIN_DIST,linkcost,ratio);
	}
	
	/**
	 * create instance with the indicated parameters
	 * @param routeNum number of routes 
	 * @param minDist minimum search distance
	 * @param linkcost link cost operator
	 * @param ratio increase ratio
	 */
	public Penalty(int routeNum,double minDist,LinkCost linkcost,double ratio) {
		super(routeNum,minDist,linkcost);
		_ratio = ratio;
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getName() */
	@Override
	public String getName() {
		return "Penalty";
	}
	
	/**
	 * get increase ratio
	 * @return increase ratio
	 */
	public double getRatio() {
		return _ratio;
	}
	
	/**
	 * set increase ratio
	 * @param ratio increase ratio
	 */
	public void setRatio(double ratio) {
		_ratio = ratio;
	}

	/* @see jp.ac.ut.csis.pflow.routing2.logic.IRoutingLogic#getRoutes(jp.ac.ut.csis.pflow.routing2.res.Network, jp.ac.ut.csis.pflow.routing2.res.Node, jp.ac.ut.csis.pflow.routing2.res.Node, int) */
	@Override
	public List<Route> getRoutes(Network network,Node depnode,Node arrnode,int n) {
		List<Route> routes = new ArrayList<Route>();
		
		Map<String,double[]> costs    = new Hashtable<String,double[]>();
		LinkCost             operator = getLinkCost(); 
		for(int i=0;i<n;i++) {
			// conduct routing with Dijkstra ==============
			Route result = getRoute(network,depnode,arrnode,costs);
			if( result == null ) { continue; }

			// store routing result =======================
			routes.add(result);
			
			// update link costs ==========================
			Node prev = depnode;
			for(Link link:result.listLinks()) {
				String  lid = link.getLinkID();
				boolean rev = prev.equals(link.getHeadNode());
				
				// update link costs where the result already goes through
				double  cst[] = costs.get(lid);
				if( cst == null ) { 
					costs.put(lid,cst = new double[]{operator.getCost(link),operator.getReverseCost(link)});
				}
				cst[rev?1:0] = cst[rev?1:0] * (1.0d + getRatio());
			}
		}
		return routes;
	}
	
	/**
	 * calculate route with the indicated link costs
	 * @param network road network
	 * @param depnode departure node
	 * @param arrnode arrival node
	 * @param costs updated costs
	 * @return result route
	 */
	private Route getRoute(Network network,Node depnode,Node arrnode,Map<String,double[]> costs) { 
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
				String  lid  = link.getLinkID();
				boolean rev  = knot.getNode().equals(link.getHeadNode());
				Node    n    = rev ? link.getTailNode() : link.getHeadNode();
				double  cost = rev ? getLinkCost().getReverseCost(link) : getLinkCost().getCost(link);
				
				// check link cost ++++++++++++++++++++++++
				double  cst  = costs.containsKey(lid) ? costs.get(lid)[rev?1:0] : cost;
				
				// update cost ++++++++++++++++++++++++++++
				if( knots.containsKey(n) ) { knots.get(n).update(knot,cst); }
				else {
					Knot k = new Knot(n,knot,cst);
					knots.put(n,k);
					queue.add(k);
				}	
			}
		}
		// extract routes(not null) ///////////////////////
		return !knot.getNode().equals(arrnode) ? null : knot.getRoute();
	}
}
