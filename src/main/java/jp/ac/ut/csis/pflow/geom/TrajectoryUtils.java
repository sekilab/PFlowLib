package jp.ac.ut.csis.pflow.geom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

/**
 * Utility class for trajectory handling
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public final class TrajectoryUtils {
	/* ==============================================================
	 * static methods
	 * ============================================================== */
	/**
	 * get trajectory length from point list
	 * @param points point list representing trajectory
	 * @return trajectory length (m)
	 */
	public static <T extends LonLat> double length(List<T> points) {
		// case no length /////////////////////////////////
		if( points == null || points.size() <= 1 ) { 
			return 0d; 
		}
		// case others ////////////////////////////////////
		int     size = points.size();
		double  d    = 0d;
		LonLat  p    = points.get(0);
		for(int i=1;i<size;i++) {
			LonLat q = points.get(i);
			d += DistanceUtils.distance(p,q);
			p = q;
		}
		return d;
	}
	
    /**
     * get interpolate ratio(0-1) determined with the nearest point from Point(P) on the line
     * @param points point list representing trajectory
     * @param p interpolate point
     * @return interpolation ratio(0-1)
     */
    public static double getLocatePointRatio(List<? extends LonLat> points,LonLat p) {
		int    len = points.size();
		LonLat p0  = points.get(0);
		double dis = Double.MAX_VALUE;
		double inc =0d;
		double sum =0d;
		
		for(int i=1;i<len;i++) {
			LonLat p1 = points.get(i);
			LonLat q  = DistanceUtils.nearestPoint(p0,p1,p);	// get nearest point between line
			double d  = p.distance(q);							// get distance from the nearest point
			if( d < dis ) {
				dis = d;
				inc = sum + DistanceUtils.distance(p0,q);
			}
			sum += DistanceUtils.distance(p0,p1);
			p0 = p1;
		}
		return inc/sum;	// ratio must be between 0 and 1
	}
    
    /**
     * get interpolated point from the beginning of line with the specified ratio
     * @param points point list representing trajectory
     * @param ratio interpolation ratio from the beginning point
     * @return interpolate point
     */
    public static <T extends LonLat> LonLat getLineInterpolatePoint(List<T> points,double ratio) {
    	// entire length and target length ////////////////
    	double length = length(points);
    	double divLen = length*ratio;
    	
    	int    size = points.size();
    	double dist = 0d;
    	T      p0   = points.get(0);
    	for(int i=1;i<size;i++) { 
    		T p1 = points.get(i);
    		double d = DistanceUtils.distance(p0,p1);
    		if( divLen <= (dist+d) ) { 
    			double rn = (dist+d-divLen)/d;
    			double rm = 1 - rn;
    			return new LonLat(p0.getLon()*rn+p1.getLon()*rm,p0.getLat()*rn+p1.getLat()*rm);
    		}
    		dist += d;
    		p0   =  p1;
    	}
    	return new LonLat(p0.getLon(),p0.getLat());
    }
    
    /**
     * get sub-line segmented between the specified ratio(0-1)
     * @param points point list representing trajectory
     * @param r0 start ratio
     * @param r1 end ratio
     * @return sub line(point list)
     */
    public static <T extends LonLat> List<LonLat> getLineSubstring(List<T> points,double r0,double r1) { 
    	// entire length //////////////////////////////////
    	double length  = length(points);
    	double divLen0 = length*r0;
    	double divLen1 = length*r1;
    	
    	int    size    = points.size();
    	double dist    = 0d;
    	T      p0      = points.get(0);
    	List<LonLat> subline = new ArrayList<LonLat>();
    	for(int i=1;i<size;i++) { 
    		T p1 = points.get(i);
    		double d = DistanceUtils.distance(p0,p1);
    		double tempD = dist+d;
    		if( tempD < divLen0 );
    		else {
    			if( subline.isEmpty() ) {
	    			double rn = (dist+d-divLen0)/d;
	    			double rm = 1 - rn;
	    			subline.add( new LonLat(p0.getLon()*rn+p1.getLon()*rm,p0.getLat()*rn+p1.getLat()*rm) );
    			}

    			if( divLen0 <= tempD && tempD < divLen1 ) { 
    				if( divLen0 != (dist+d) ) { subline.add(p1); }
    			}
	    		else {
	    			double rn = (dist+d-divLen1)/d;
	    			double rm = 1 - rn;
	    			subline.add( new LonLat(p0.getLon()*rn+p1.getLon()*rm,p0.getLat()*rn+p1.getLat()*rm) );
	    			break;
	    		}
    		}
	    	dist += d;
    		p0    = p1;
    	}
    	
    	return subline;
    }
    
    /**
     * split line with the specified ratio values
     * @param line point list representing trajectory
     * @param ratios ratio values. values must be sorted in advance
     * @return split lines
     */
    public static <T extends LonLat> List<List<LonLat>> splitTrajectory(List<T> line,double ratios[]) {
    	// organize ratio list ////////////////////////////
    	List<Double> ratioList = new ArrayList<Double>();
    	for(double ratio:ratios) { ratioList.add(ratio); }
    	if( ratioList.get(0) != 0d ) { ratioList.add(0,0d); } 
    	if( ratioList.get(ratioList.size()-1) != 1d ) { ratioList.add(1d); } 
    	
    	// extract each section ///////////////////////////
    	List<List<LonLat>> result = new ArrayList<List<LonLat>>();
    	int    len = ratioList.size(); 
    	double r0  = ratioList.get(0);
    	for(int i=1;i<len;i++) { 
    		double r1 = ratioList.get(i);
    		
    		List<LonLat> subline = getLineSubstring(line,r0,r1);
    		result.add(subline);
    		
    		r0 = r1;
    	}
    	return result;
    }
    
	/**
	 * organize point list into WKT STRING of LINESTRING 
	 * @param points point list representing trajectory
	 * @return LINESTRING string, otherwise null when the insufficient number of points 
	 */
	public static <T extends LonLat> String asWKT(List<T> points) {
		// error handle ///////////////////////////////////
		if ( points == null || points.size() <= 1 ) { return null; }
		// organize LINESTRING ////////////////////////////
		boolean      m   = false;
		StringBuffer buf = new StringBuffer();
		for(T p:points) {
			if( m |= p instanceof STPoint ) {
				STPoint q = STPoint.class.cast(p);
				buf.append(String.format(",%.06f %.06f %d",q.getLon(),q.getLat(),q.getTimeStamp().getTime()));
			}
			else {
				buf.append(String.format(",%.06f %.06f",p.getLon(),p.getLat()));
			}
		}
		return m ? "LINESTRING M(" + buf.substring(1) + ")" : "LINESTRING(" + buf.substring(1) + ")";
	}
	
	/**
	 * assign time-stamp to the all points in the indicated point list, by assuming that velocity is constant.  
	 * @param points point list representing trajectory
	 * @param ts start time
	 * @param te end time
	 * @return point list with passage time.
	 */
	public static <T extends LonLat> List<STPoint> assignTimeStamp(List<T> points,Date ts,Date te) {
		// total distance and total duration //////////////
		double dist     = length(points);
		long   duration = (te.getTime()-ts.getTime());
		
		// result points //////////////////////////////////
		List<STPoint> result = new ArrayList<STPoint>();
		
		// Origin position and time-stamp /////////////////
		Date   dt = Date.class.cast(ts.clone());
		LonLat p0 = points.get(0);
		result.add(new STPoint(dt,p0.getLon(),p0.getLat()));
		
		// assign time-stamp to each position /////////////
		for(int i=1;i<points.size();i++) {
			LonLat p1    = points.get(i);
			double len   = DistanceUtils.distance(p0,p1);
			double ratio = len/dist;
			// update time stamp
			dt   = DateUtils.addMilliseconds(dt,(int)(ratio*duration));
			result.add(new STPoint(dt,p1.getLon(),p1.getLat()));
			p0 = p1;
		}
		
		// adjust error ///////////////////////////////////
		result.get(result.size()-1).setTimeStamp(te);
		return result;	
	}
	
	/**
	 * interpolate the indicated trajectory, and reconstruct new list of spatiotemporal points with 1 minutes interval
	 * @param points point list representing trajectory
	 * @param ts start time
	 * @param te end time
	 * @return list of spatiotemporal points with 1 minutes interval
	 */
	public static <T extends LonLat> List<STPoint> interpolateUnitTime(List<T> points,Date ts,Date te) {
		return interpolateUnitTime(points,ts,te,60);
	}
	
	/**
	 * interpolate the indicated trajectory, and reconstruct new list of spatiotemporal points with the indicated time interval
	 * @param points point list representing trajectory
	 * @param ts start time
	 * @param te end time
	 * @param unitTimeInSecond time interval in second
	 * @return list of spatiotemporal points
	 */
	public static <T extends LonLat> List<STPoint> interpolateUnitTime(List<T> points,Date ts,Date te,int unitTimeInSecond) {
		// configuration ////////////////////////
		double dist    = length(points);	// total length of input trajectory
		int    N       = (int)((te.getTime()-ts.getTime())/(unitTimeInSecond*1000L));	// the number of points in result trajectory
		double unit_d  = dist / N;          // unit distance
		double unit_ts = (te.getTime()-ts.getTime()) / ((double)N);
		
		// start point //////////////////////////
		LonLat        p      = points.get(0);
		List<STPoint> result = new ArrayList<STPoint>();
		
		// head point ///////////////////////////
		result.add(new STPoint(Date.class.cast(ts.clone()),p.getLon(),p.getLat()));
		
		// re-sampling along with points ////////
		double len = 0;
		int    idx = 1;
		for(int i=1;i<N;i++) {
			while(true) {
				LonLat p0 = points.get(idx-1);
				LonLat p1 = points.get(idx);
				double d  = unit_d * i;
				double ln_len  = DistanceUtils.distance(p0.getLon(), p0.getLat(), p1.getLon(), p1.getLat());
				double tmp_len = len + ln_len;
				if( d <= tmp_len ) {
					result.add(new STPoint(	DateUtils.addMilliseconds(ts, (int)(unit_ts*i)),
											ln_len==0 ? p.getLon() : p0.getLon() + (p1.getLon()-p0.getLon())*(d-len)/ln_len,
											ln_len==0 ? p.getLat() : p0.getLat() + (p1.getLat()-p0.getLat())*(d-len)/ln_len));
					break;
				}
				len = tmp_len;
				idx += 1;
			}
		}
		// tail point ///////////////////////////
		LonLat pn = points.get(points.size()-1);
		result.add( new STPoint(Date.class.cast(te.clone()),pn.getLon(),pn.getLat()) );
		return result;
	}
	
	/**
	 * get spatiotemporal point on the trajectory at the indicated time-stamp
	 * @param stpoints point list representing spatiotemporal trajectory
	 * @param dt time-stamp
	 * @return spatiotemporal point at the time. return null if the indicated time-stamp is outside of trajectory
	 */
	public static <T extends STPoint> STPoint getSTPointAt(List<T> stpoints,Date dt) {
		int     L  = stpoints.size();
		STPoint ps = stpoints.get(0);
		Date    ts = ps.getTimeStamp();
		if( dt.before(ts)  ) { return null; }
		
		STPoint output = null;		
		if ( dt.equals(ts) ) { output = ps.clone(); }
		else {
			for(int i=1;i<L;i++) { 
				T    p = stpoints.get(i);
				Date t = p.getTimeStamp();
				if( dt.equals(t) ) { output = p.clone(); break; }
				else if( dt.before(t) ) { 
					long   m = dt.getTime() - ps.getTimeStamp().getTime();
					long   n = t.getTime()  - dt.getTime();
					double x = (ps.getLon()*n + p.getLon()*m) / (m+n);
					double y = (ps.getLat()*n + p.getLat()*m) / (m+n);
					output = new STPoint(Date.class.cast(dt.clone()),x,y);
					break;
				}
				ps = p;
			}
		}
		return output;
	}
}
