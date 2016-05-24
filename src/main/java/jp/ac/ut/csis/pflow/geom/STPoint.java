package jp.ac.ut.csis.pflow.geom;

import java.util.Date;

import org.apache.commons.lang.math.LongRange;

/**
 * Class for Spatiotemporal point. 
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class STPoint extends LonLat implements Comparable<STPoint> {
    /* ==============================================================
	 * static fields
	 * ============================================================== */
	/** serial */	private static final long serialVersionUID = -6096951274573950900L;

	
    /* ==============================================================
	 * static methods
	 * ============================================================== */
	/**
	 * get time duration in millisecond
	 * @param ts start time
	 * @param te end time
	 * @return time duration (msec)
	 */
	public static long getDuration(Date ts, Date te) {
		return ts == null || te == null ? -1 : te.getTime() - ts.getTime();
	}
	
	
    /* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** start time	*/	private Date _dtstart;
	/** end time	*/	private Date _dtend;
	
	
    /* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * initialization with the indicated time-span and position
	 * @param dtstart start time
	 * @param dtend end time
	 * @param lon longitude
	 * @param lat latitude
	 */
	public STPoint(Date dtstart, Date dtend, double lon, double lat) {
		super(lon, lat);
		setTimeSpan(dtstart, dtend);
	}
	
	/**
	 * initialization with the indicated time and position
	 * @param ts time stamp
	 * @param lon longitude
	 * @param lat latitude
	 */
	public STPoint(Date ts, double lon, double lat) {
		super(lon, lat);
		setTimeStamp(ts);
	}
	
	/**
	 * initialization
	 */
	public STPoint() {
		super();
	}
	
	
    /* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get time stamp
	 * @return time stamp, return null if this represents time span
	 */
	public Date getTimeStamp() {
		return isTimeStamp() ? _dtstart : null;
	}
	
	/**
	 * get start time 
	 * @return start time
	 */
	public Date getDtStart() {
		return _dtstart;
	}
	
	/**
	 * get end time
	 * @return end time
	 */
	public Date getDtEnd() {
		return _dtend;
	}
	
	/**
	 * set time stamp
	 * @param time time 
	 */
	public void setTimeStamp(Date time) {
		setTimeSpan(time, time);
	}
	
	/**
	 * set time span
	 * @param dtstart start time 
	 * @param dtend end time
	 */
	public void setTimeSpan(Date dtstart, Date dtend) {
		_dtstart = dtstart;
		_dtend   = dtend;
	}
	
	/**
	 * set indicated values to this instance
	 * @param time time-stamp
	 * @param lon longitude
	 * @param lat latitude
	 */
	public void setValues(Date time,double lon,double lat) { 
		setTimeStamp(time);
		setLocation(lon,lat);
	}
	
	/**
	 * set indicated values to this instance
	 * @param dtstart start time
	 * @param dtend end time
	 * @param lon longitude
	 * @param lat latitude
	 */
	public void setValues(Date dtstart,Date dtend,double lon,double lat) { 
		setTimeSpan(dtstart,dtend);
		setLocation(lon,lat);
	}
	
	/**
	 * check if this instance indicates time stamp
	 * @return result
	 */
	public boolean isTimeStamp() {
		return _dtstart != null && _dtend != null && _dtstart.equals(_dtend);
	}
	
	/**
	 * check if this instance indicates time slice
	 * @return result
	 */
	public boolean isTimeSpan() {
		return _dtstart != null && _dtend != null && !_dtstart.equals(_dtend);
	}
	
	/**
	 * get time duration of this instance
	 * @return time duration(msec)
	 */
	public long getDuration() {
		return getDuration(_dtstart, _dtend);
	}

	/**
	 * check if this instance intersects with the indicated time stamp
	 * @param ts time stamp
	 * @return result
	 */
	public boolean intersects(Date ts) {
		return _dtstart  != null && _dtend != null && ts != null &&
				new LongRange(_dtstart.getTime(), _dtend.getTime()).containsLong(ts.getTime());
	}

	/**
	 * check if this instance intersects with the indicated time span
	 * @param ts start time
	 * @param te end time
	 * @return result
	 */
	public boolean intersects(Date ts, Date te) {
		return	_dtstart != null && _dtend != null && ts != null && te != null &&
				new LongRange(_dtstart.getTime(), _dtend.getTime()).overlapsRange(new LongRange(ts.getTime(), te.getTime()));
	}
	
	/* @see java.lang.Comparable#compareTo(java.lang.Object) */
	@Override
	public int compareTo(STPoint p) {
		Date t0 = isTimeStamp()   ? getTimeStamp()   : getDtStart();
		Date t1 = p.isTimeStamp() ? p.getTimeStamp() : p.getDtStart();
		
		return t0.compareTo(t1);
	}

	/* @see java.lang.Object#toString() */
    @Override
    public String toString() {
    	// case time-span
    	if( isTimeSpan() ) {
    		return String.format("%s - %s (%f,%f)", _dtstart, _dtend, getLon(), getLat());
    	}
    	// case time-stamp
    	else {
    		return String.format("%s (%f,%f)", _dtstart, getLon(), getLat());
    	}
    }
    
    /* @see java.lang.Object#clone() */
    @Override
    public STPoint clone() {
    	return new STPoint(	Date.class.cast(_dtstart.clone()), 
    						Date.class.cast(_dtend.clone()), 
    						getLon(), 
    						getLat() );
    }
}
