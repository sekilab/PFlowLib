package jp.ac.ut.csis.pflow.routing2.res;

import java.io.Serializable;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.DistanceUtils;
import jp.ac.ut.csis.pflow.geom.LonLat;


/**
 * Class for Network Link
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Link implements Serializable, Cloneable {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Serial */	private static final long serialVersionUID = -9058532949182281951L;
	
	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** link ID                */	private String       _linkid;
	/** origin/tail node       */	private Node         _tail;	
	/** destination/head node  */	private Node         _head;
	/** link cost              */	private double       _cost;
	/** reverse cost           */	private double       _revCost;
	/** one-way flag           */	private boolean      _oneway;
	/** link geometry(Line)    */	private List<LonLat> _geometry;
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create link instance (both directions, direct distance as cost)
	 * @param linkid Link ID
	 * @param tailNode origin/tail node
	 * @param headNode destination/head node
	 */
	public Link(String linkid,Node tailNode,Node headNode) {
		this(linkid,tailNode,headNode,DistanceUtils.distance(tailNode,headNode));
	}
	
	/**
	 * create link instance (both directions)
	 * @param linkid Link ID
	 * @param tailNode origin/tail node
	 * @param headNode destination/head node
	 * @param cost link cost
	 */
	public Link(String linkid,Node tailNode,Node headNode,double cost) {
		this(linkid,tailNode,headNode,cost,false);
	}
	
	/**
	 * create link instance
	 * @param linkid Link ID
	 * @param tailNode origin/tail node
	 * @param headNode destination/head node
	 * @param cost link cost
	 * @param oneway direction flag
	 */
	public Link(String linkid,Node tailNode,Node headNode,double cost,boolean oneway) {
		this(linkid,tailNode,headNode,cost,cost,oneway);
	}
	
	/**
	 * create link instance
	 * @param linkid Link ID
	 * @param tailNode origin/tail node
	 * @param headNode destination/head node
	 * @param cost link cost
	 * @param revCost reverse cost
	 * @param oneway direction flag
	 */
	public Link(String linkid,Node tailNode,Node headNode,double cost,double revCost,boolean oneway) {
		// set necessary parameters ///////////////////////
		_linkid = linkid;
		_tail   = tailNode;
		_head   = headNode;
		_cost   = cost;
		_revCost= revCost; // Double.NaN;
		_oneway = oneway;
		// check direction ////////////////////////////////
		_tail.addOutLink(this);
		_head.addInLink(this);
		if( !_oneway ) { 
//			_revCost = revCost;
			_tail.addInLink(this);
			_head.addOutLink(this);
		}
		// no geometry ////////////////////////////////////
		_geometry = null;
	}
	
	/**
	 * create link instance
	 * @param linkid Link ID
	 * @param tailNode origin/tail node
	 * @param headNode destination/head node
	 * @param cost link cost
	 * @param revCost reverse cost
	 * @param oneway direction flag
	 * @param geom point list(head node must be first point of list)
	 */
	public Link(String linkid,Node tailNode,Node headNode,double cost,double revCost,boolean oneway,List<LonLat> geom) {
		this(linkid,tailNode,headNode,cost,revCost,oneway);
		_geometry = geom;
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * set link geometry from point list
	 * @param geom point list
	 */
	public void setLineString(List<LonLat> geom) {
		_geometry = geom;
	}
	
	/**
	 * get link geometry as point list
	 * @return geometry(list) as point list
	 */
	public List<LonLat> getLineString() {
		return _geometry;
	}
	
	/**
	 * check if instance has geometry 
	 * @return result
	 */
	public boolean hasGeometry() { 
		return _geometry != null && !_geometry.isEmpty();
	}
	
	/**
	 * get Link ID
	 * @return link ID
	 */
	public String getLinkID() {
		return _linkid;
	}
	
	/**
	 * get origin/tail node
	 * @return origin/tail node
	 */
	public Node getTailNode() {
		return _tail;
	}
	
	/**
	 * get destination/head node
	 * @return destination/head node
	 */
	public Node getHeadNode() {
		return _head;
	}
		
	/**
	 * get link cost(e.g. distance, elapse time or ...)
	 * @return link cost value
	 */
	public double getCost() {
		return _cost;
	}
	
	/**
	 * set link cost(e.g. distance, elapse time, or ...)
	 * @param cost link cost value
	 */
	public void setCost(double cost) {
		_cost = cost;
	}
	
	/**
	 * get reverse link cost
	 * @return reverse link cost if both way, otherwise NaN 
	 */
	public double getReverseCost() { 
		return _revCost; // isOneWay() ? Double.NaN : _revCost;
	}
	
	/**
	 * set reverse link cost
	 * @param revCost
	 */
	public void setReverseCost(double revCost) { 
		_revCost = revCost;
	}
	
	/**
	 * check if the link is one-way or not
	 * @return return true when the link is one-way, otherwise false.
	 */
	public boolean isOneWay() {
		return _oneway;
	}
	
	/**
	 * set one-way flag
	 * @param oneway one-way flag
	 */
	public void setOneWay(boolean oneway) {
		_oneway = oneway;
	}
	
	/* @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return String.format("%s::(%s-(%f)-%s)::%b",_linkid,_tail,_cost,_head,_oneway);
	}

	/**
	 * create clone with the indicated nodes
	 * @param tail tail node
	 * @param head head node
	 * @return clone link
	 */
	public Link clone(Node tail,Node head) {
		return new Link(_linkid,tail,head,_cost,_revCost,_oneway,_geometry);	// should node and geometry be cloned as well?
	}
}
