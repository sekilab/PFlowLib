package jp.ac.ut.csis.pflow.routing2.logic;

import java.util.List;

import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;
import jp.ac.ut.csis.pflow.routing2.res.Route;

/**
 * Interface for route search logics
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public interface IRoutingLogic {
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get name of routing algorithm
	 * @return algorithm name
	 */
	public String getName();
	
	/**
	 * execute route search to get the number of routes
	 * @param network network data for search
	 * @param depnode departure node
	 * @param arrnode arrival node
	 * @param n the number of routes to explore
	 * @return route list, not always contain n routes
	 */
	public List<Route> getRoutes(Network network,Node depnode,Node arrnode,int n);
	
	/**
	 * execute route search to get the default number of routes
	 * @param network network data for search
	 * @param depnode departure node
	 * @param arrnode arrival node
	 * @return route list, not always contain n routes
	 */
	public List<Route> getRoutes(Network network,Node depnode,Node arrnode);
	
	/**
	 * execute route search to get 1 route
	 * @param network network data for search
	 * @param depnode departure node
	 * @param arrnode arrival node
	 * @return route list
	 */
	public Route getRoute(Network network,Node depnode,Node arrnode);
	
	/**
	 * execute route search to get the number of routes
	 * @param network network data for search
	 * @param depnodeid departure node ID
	 * @param arrnodeid arrival node ID
	 * @param n the number of routes to explore
	 * @return route list, not always contain n routes
	 */
	public List<Route> getRoutes(Network network,String depnodeid,String arrnodeid,int n);

	/**
	 * execute route search to get the default number of routes
	 * @param network network data for search
	 * @param depnodeid departure node ID
	 * @param arrnodeid arrival node ID
	 * @return route list, not always contain n routes
	 */
	public List<Route> getRoutes(Network network,String depnodeid,String arrnodeid);
	
	/**
	 * execute route search to get 1 route
	 * @param network network data for search
	 * @param depnodeid departure node ID
	 * @param arrnodeid arrival node ID
	 * @return route list, not always contain n routes
	 */
	public Route getRoute(Network network,String depnodeid,String arrnodeid);
	
	/**
	 * execute route search to get the specified number of routes
	 * @param network network data for search
	 * @param depx longitude of departure place
	 * @param depy latitude of departure place
	 * @param arrx longitude of arrival place
	 * @param arry latitude of arrival place
	 * @param n the number of routes to explore 
	 * @return route list, not always contain n routes
	 */
	public List<Route> getRoutes(Network network,double depx,double depy,double arrx,double arry,int n);

	/**
	 * execute route search to get the default number of routes
	 * @param network network data for search
	 * @param depx longitude of departure place
	 * @param depy latitude of departure place
	 * @param arrx longitude of arrival place
	 * @param arry latitude of arrival place
	 * @return route list, not always contain n routes
	 */
	public List<Route> getRoutes(Network network,double depx,double depy,double arrx,double arry);

	/**
	 * execute route search to get 1 route
	 * @param network network data for search
	 * @param depx longitude of departure place
	 * @param depy latitude of departure place
	 * @param arrx longitude of arrival place
	 * @param arry latitude of arrival place
	 * @return route list, not always contain n routes
	 */
	public Route getRoute(Network network,double depx,double depy,double arrx,double arry);
	
	/**
	 * explore the nearest node from the specified point
	 * @param network network data for search
	 * @param x longitude
	 * @param y latitude
	 * @return returns the nearest node if found, otherwise null, 
	 */
	public Node getNearestNode(Network network,double x,double y);
	
	/**
	 * explore the nearest node within mindist, from the specified point
	 * @param network network data for search
	 * @param x longitude
	 * @param y latitude
	 * @param mindist minimum distance from the input point
	 * @return returns the nearest node if found, otherwise null, 
	 */
	public Node getNearestNode(Network network,double x,double y,double mindist);
	
	/**
	 * explore the nearest link from the specified point
	 * @param network network data for search
	 * @param x longitude
	 * @param y latitude
	 * @return returns the nearest link if found, otherwise null, 
	 */
	public Link getNearestLink(Network network,double x,double y);
	
	/**
	 * explore the nearest link within mindist, from the specified point
	 * @param network network data for search
	 * @param x longitude
	 * @param y latitude
	 * @param mindist minimum distance from the input point
	 * @return returns the nearest link if found, otherwise null, 
	 */
	public Link getNearestLink(Network network,double x,double y,double mindist);
}
