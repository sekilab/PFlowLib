package jp.ac.ut.csis.pflow.routing2.res;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for explored route
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Route implements Cloneable{
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** route consisting of Node list */	private List<Node> _route;
	/** total cost of route	          */	private double     _cost;

	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create empty route
	 */
	public Route() {
		_route = new ArrayList<Node>();
		_cost = 0d;
	}
	
	/**
	 * create route with the specified parameters
	 * @param list Node list
	 * @param cost total cost
	 */
	public Route(List<Node> points,double cost) {
		_route = points;
		_cost  = cost;
	}
	

	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * add point to the end of route, with cost
	 * @param node node
	 * @param cost cost
	 */
	public void add(Node node,double cost) {
		_route.add(node);
		_cost += cost;
	}
	
	/**
	 * get total cost
	 * @return total cost
	 */
	public double getCost() {
		return _cost;
	}
	
	/**
	 * get the number of nodes in the route
	 * @return the number of nodes
	 */
	public int numNodes() {
		return _route.size();
	}
	
	/**
	 * get route as link list. please take care of link direction(head-tail) 
	 * @return link list
	 */
	public List<Link> listLinks() { 
		List<Link> list = new ArrayList<Link>();
		
		Node n0  = _route.get(0);
		int  len = _route.size();
		for(int i=1;i<len;i++) { 
			Node n1 = _route.get(i);
			for(Link L:n0.listAllLinks()) {
				if( L.getTailNode().equals(n1) || L.getHeadNode().equals(n1) ) { 
					list.add(L); 
					break;
				}
			}
			n0 = n1;
		}
		return list;
	}
	
	/**
	 * get route as node list
	 * @return node list
	 */
	public List<Node> listNodes() {
		return _route;
	}
	
	/**
	 * get node from the specified index
	 * @param idx index
	 * @return node
	 */
	public Node getNode(int idx) {
		return _route.get(idx);
	}
	
	/**
	 * check if the node is included in this route
	 * @param node node
	 * @return returns true if the node is included in the route, otherwise false
	 */
	public boolean contains(Node node) {
		return _route.contains(node);
	}
	
	/* @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof Route ) { 
			return _route.equals( Route.class.cast(obj).listNodes()); 
		}
		else {
			return super.equals(obj);
		}
	}
}
