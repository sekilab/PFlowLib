package jp.ac.ut.csis.pflow.routing2.matching;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.DistanceUtils;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.Network;
import jp.ac.ut.csis.pflow.routing2.res.Node;

/**
 * Class for Simple Map Matching
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class MapMatching implements IMatching {
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing2.matching.IMatching#runMatching(jp.ac.ut.csis.pflow.routing2.res.Network, java.util.List)  */
	@Override
	public <T extends LonLat> List<MatchingResult> runMatching(Network network,List<T> points) { 
		return runMatching(network,points,SEARCH_RANGE);
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.matching.IMatching#runMatching(jp.ac.ut.csis.pflow.routing2.res.Network, java.util.List, double) */
	@Override
	public <T extends LonLat> List<MatchingResult> runMatching(Network network,List<T> points,double range) {
		List<MatchingResult> result = new ArrayList<MatchingResult>(points.size());
		// conduct matching one by one
		for(T point:points) {
			result.add( runMatching(network,point,range) );
		}
		// return results
		return result;
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.matching.IMatching#runMatching(jp.ac.ut.csis.pflow.routing2.res.Network, jp.ac.ut.csis.pflow.geom.LonLat) */
	@Override
	public <T extends LonLat> MatchingResult runMatching(Network network,T point) {
		return runMatching(network,point,SEARCH_RANGE);
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.matching.IMatching#runMatching(jp.ac.ut.csis.pflow.routing2.res.Network, jp.ac.ut.csis.pflow.geom.LonLat, double) */
	@Override
	public <T extends LonLat> MatchingResult runMatching(Network network,T point,double range) {
		return runMatchingToLink(network,point,range);
	}
	
	/**
	 * conduct map matching to link
	 * @param network network 
	 * @param point source point 
	 * @param range search range in meter
	 * @return matching result
	 */
	public <T extends LonLat> MatchingResult runMatchingToLink(Network network,T point,double range) {
		// get candidate links ////////////////////////////
		List<Link> links = network.queryLink(point.getLon(),point.getLat(),range);
		
		// look for nearest link from the candidates //////
		double         distance     = Double.MAX_VALUE;
		Link           nearestLink  = null;
		LonLat         nearestPoint = null;
		Iterator<Link> itr          = links.iterator();
		while(itr.hasNext()) {
			// get candidate link 
			Link candidate = itr.next();
			
			// calculate distance from input point to road link
			LonLat p = DistanceUtils.nearestPoint(candidate.getLineString(),point);
			if( p == null ) { continue; }	// when the nearest point is not found
			
			double d = DistanceUtils.distance(p,point);
			// compare distance
			if( d < distance ) { 
				nearestLink  = candidate;
				nearestPoint = p;
				distance     = d;
			}
		}
		// create matching result and return.
		return new MatchingResult(point,nearestPoint,nearestLink,distance);
	}

	/**
	 * conduct map matching to node
	 * @param network network 
	 * @param point source point 
	 * @param range search range in meter
	 * @return matching result
	 */
	public <T extends LonLat> MatchingResult runMatchingToNode(Network network,T point,double range) {
		// get candidate links ////////////////////////////
		List<Node> nodes = network.queryNode(point.getLon(),point.getLat(),range);
		
		// look for nearest link from the candidates //////
		double         distance     = Double.MAX_VALUE;
		LonLat         nearestPoint = null;
		Iterator<Node> itr          = nodes.iterator();
		while(itr.hasNext()) {
			// get candidate node 
			Node candidate = itr.next();
			
			double d = DistanceUtils.distance(candidate,point);
			// compare distance
			if( d < distance ) { 
				nearestPoint = candidate;
				distance     = d;
			}
		}
		// create matching result and return.
		return new MatchingResult(point,nearestPoint,distance);
	}
}
