package jp.ac.ut.csis.pflow.routing2.logic;

import jp.ac.ut.csis.pflow.routing2.res.Link;

/**
 * Class for link cost operator
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class LinkCost {
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get link cost
	 * @param link link cost
	 * @return link cost
	 */
	public double getCost(Link link) { 
		return link.getCost();
	}
	
	/**
	 * get Reverse cost
	 * @param link reverse cost
	 * @return reverse cost
	 */
	public double getReverseCost(Link link) { 
		return link.getReverseCost();
	}
}


