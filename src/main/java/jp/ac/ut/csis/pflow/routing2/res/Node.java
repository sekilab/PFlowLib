package jp.ac.ut.csis.pflow.routing2.res;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.ac.ut.csis.pflow.geom.LonLat;

/**
 * Class for Network Node with position information
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Node extends LonLat {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Serial */	private static final long serialVersionUID = -3258816479568930463L;
	
	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** node name 		*/	String     _id;
	/** in-flow links	*/	List<Link> _inLinks;
	/** out-flow links	*/	List<Link> _outLinks;
	

	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * create instance
	 * @param id Node ID
	 * @param lon longitude(x)
	 * @param lat latitude(y)
	 */
	public Node(String id,double lon,double lat) {
		super(lon,lat);
		_id = id;
		
		_inLinks  = new ArrayList<Link>();
		_outLinks = new ArrayList<Link>();
	}
	
	/**
	 * create instance without position
	 * @param id Node ID
	 */
	public Node(String id) {
		this(id,Double.NaN,Double.NaN);
	}

	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get Node ID
	 * @return Node ID
	 */
	public String getNodeID() {
		return _id;
	}
	
	/**
	 * add the in-flow link to this node
	 * @param link adding in-flow link
	 */
	public void addInLink(Link link) { 
		_inLinks.add(link);
	}
	
	/**
	 * remove the in-flow link
	 * @param link removing link
	 * @return return true if successfully removed
	 */
	public boolean removeInLink(Link link) { 
		return _inLinks.remove(link);
	}
	
	/**
	 * returns in-flow links to this node
	 * @return list of in-flow links
	 */
	public List<Link> listInLinks() {
		return _inLinks;
	}
	
	/**
	 * add the out-flow link to this node
	 * @param link adding out-flow link
	 */
	public void addOutLink(Link link) { 
		_outLinks.add(link);
	}
	
	/**
	 * remove the out-flow link 
	 * @param link removing link
	 * @return return true if successfully removed
	 */
	public boolean removeOutLink(Link link) { 
		return _outLinks.remove(link);
	}
	
	/**
	 * returns out-flow links from this node
	 * @return list of out-flow links
	 */
	public List<Link> listOutLinks() {
		return _outLinks;
	}
	
	/**
	 * list all connecting links
	 * @return list of all links(no duplication)
	 */
	public List<Link> listAllLinks() { 
		Set<Link> district = new LinkedHashSet<Link>();
		district.addAll(_inLinks);
		district.addAll(_outLinks);		
		return new ArrayList<Link>(district);
	}
	
	/**
	 * check if the node is isolated
	 * @return return true if isolated, otherwise false
	 */
	public boolean isIsolated() {
		return _outLinks.isEmpty() && _inLinks.isEmpty();
	}
	
	/* @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Node && _id.equals( Node.class.cast(obj).getNodeID() );
	}
	
	/* @see jp.ac.ut.csis.pflow.geom.LonLat#toString() */
	@Override
	public String toString() {
		return getNodeID();
	}
	
	/* @see jp.ac.ut.csis.pflow.geom.LonLat#clone() */
	@Override
	public Node clone() {
		return new Node(_id,getLon(),getLat());
	}
}
