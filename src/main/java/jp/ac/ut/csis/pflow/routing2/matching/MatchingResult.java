package jp.ac.ut.csis.pflow.routing2.matching;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.routing2.res.Link;

/**
 * Class for representing map matching result
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class MatchingResult {
	/* ==============================================================
	 * instance fields
	 * ============================================================== */	
	/** input point for map matching   */	private LonLat       _inputPoint;
	/** output point from map matching */	private LonLat       _nearestPoint;
	/** matching link id if exists     */	private Link         _nearestLink;
	/** distance between in and out	   */	private double       _dist;
	/** attribute List                 */ 	private List<String> _attrs;
	

	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * initialization
	 * @param in input point
	 * @param out the nearest point from input point
	 * @param dist distance(m)
	 */
	protected MatchingResult(LonLat in,LonLat out,double dist) {
		this(in,out,null,dist,null);
	}

	/**
	 * initialization
	 * @param in input point
	 * @param out the nearest point from input point
	 * @param link the nearest link from input point
	 * @param dist distance(m)
	 */
	protected MatchingResult(LonLat in,LonLat out,Link link,double dist) {
		this(in,out,link,dist,null);
	}
	
	/**
	 * initialization
	 * @param in input point
	 * @param out the nearest point from input point
	 * @param dist distance (m)
	 * @param attrs attribute list
	 */
	protected MatchingResult(LonLat in,LonLat out,double dist,List<String> attrs) {
		this(in,out,null,dist,attrs);
	}
	
	/**
	 * initialization
	 * @param in input point
	 * @param out the nearest point from input point
	 * @param link the nearest link from input point
	 * @param dist distance (m)
	 * @param attrs attribute list
	 */
	protected MatchingResult(LonLat in,LonLat out,Link link,double dist,List<String> attrs) {
		_dist         = dist;
		_inputPoint   = in;
		_nearestPoint = out;
		_nearestLink  = link;
		_attrs        = attrs;
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	
	/**
	 * check result is valid
	 * @return result
	 */
	public boolean isValid() { 
		return _nearestPoint != null;
	}
	
	/**
	 * get input point 
	 * @return input point
	 */
	public LonLat getInputPoint() {
		return _inputPoint;
	}
	
	/**
	 * get the nearest point on link from the input. 
	 * @return output point from map matching. returns null when failed to matching
	 */
	public LonLat getNearestPoint() {
		return _nearestPoint;
	}
	
	/**
	 * get the nearest link
	 * @return nearest link. return null if failed
	 */
	public Link getNearestLink() {
		return _nearestLink;
	}
	
	/**
	 * get distance between input and output points
	 * @return distance(m)
	 */
	public double getDistance() {
		return _dist;
	}
	
	/**
	 * get additional attributes
	 * @return attributes
	 */
	public List<String> getAttributes() {
		return _attrs;
	}
	
	/**
	 * format output string with tab delimiter
	 * @return output string
	 */
	public String toResultString() {
		return toResultString("\t");
	}
	
	/**
	 * format output string
	 * @param delim delimiter
	 * @return output string
	 */
	public String toResultString(String delim) {
		DecimalFormat df  = new DecimalFormat("###.######");
		List<String>  val = Arrays.asList(
								df.format(_inputPoint.getLon()),
								df.format(_inputPoint.getLat()),
								_nearestPoint!=null ? df.format(_nearestPoint.getLon()) : "",
								_nearestPoint!=null ? df.format(_nearestPoint.getLat()) : "",
								_nearestPoint!=null ? String.valueOf(_dist)             : "",
								_nearestLink!=null  ? _nearestLink.getLinkID()          : ""
							);
		
		// matching result + original attributes
		return StringUtils.join(val,delim) +
			   (_attrs != null && !_attrs.isEmpty() ? delim + StringUtils.join(_attrs,delim) : "");
	}
	
}
