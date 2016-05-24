package jp.ac.ut.csis.pflow.routing2.matching;

import java.util.List;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.res.Network;

/**
 * Interface for map matching
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public interface IMatching {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** default search range	*/	
	public static final double SEARCH_RANGE = 3000d;	// 3 km
	
	
	/* ==============================================================
	 * abstract methods
	 * ============================================================== */
	/**
	 * conduct map matching with the indicated point list to the network
	 * @param network network data
	 * @param points point list
	 * @return result of matching result (point list on links)
	 */
	public <T extends LonLat> List<MatchingResult> runMatching(Network network,List<T> points);
	
	/**
	 * conduct map matching with the indicated point list to the network
	 * @param network network data
	 * @param points point list
	 * @param range search range from the input point
	 * @return result of matching result (point list on links)
	 */
	public <T extends LonLat> List<MatchingResult> runMatching(Network network,List<T> points,double range);
	
	/**
	 * conduct map matching with the indicated point to the network
	 * @param network network 
	 * @param point point
	 * @return matching result
	 */
	public <T extends LonLat> MatchingResult runMatching(Network network,T point);
	
	/**
	 * conduct map matching with the indicated point to the network
	 * @param network network 
	 * @param point point
	 * @param range search range from the input point
	 * @return matching result
	 */
	public <T extends LonLat> MatchingResult runMatching(Network network,T point,double range);	
}
