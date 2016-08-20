package jp.ac.ut.csis.pflow.routing2.logic;

import jp.ac.ut.csis.pflow.routing2.res.Link;
import jp.ac.ut.csis.pflow.routing2.res.OsmLink;

/**
 * Class for link cost operator of OSM links
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class OsmLinkCost extends LinkCost {
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing2.logic.LinkCost#getCost(jp.ac.ut.csis.pflow.routing2.res.Link) */
	@Override
	public double getCost(Link link) { 
		// case OsmLink, get cost as time duration
		if ( link instanceof OsmLink ) {
			OsmLink osmlink = OsmLink.class.cast(link);
			double  vel_m_s = osmlink.getSpeed() * 1000d/3600d;
			return link.getCost() / vel_m_s;
		}
		// otherwise get original cost
		else { 
			return link.getCost();
		}
	}
	
	/* @see jp.ac.ut.csis.pflow.routing2.logic.LinkCost#getReverseCost(jp.ac.ut.csis.pflow.routing2.res.Link) */
	@Override
	public double getReverseCost(Link link) { 
		// case OsmLink, get reverse cost as time duration
		if ( link instanceof OsmLink ) {
			OsmLink osmlink = OsmLink.class.cast(link);
			double  vel_m_s = osmlink.getSpeed() * 1000d/3600d;
			return link.getReverseCost() / vel_m_s;
		}
		// otherwise, get original reverse cost
		else { 
			return link.getReverseCost();
		}
	}
}
