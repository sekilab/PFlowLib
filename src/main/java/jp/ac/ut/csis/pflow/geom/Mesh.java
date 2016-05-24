package jp.ac.ut.csis.pflow.geom;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.fraction.Fraction;

/**
 * Class for area mesh in Japan
 * 
 * <dl>
 * <dt>references</dt>
 * <dd>MIC statistics : <a href="http://www.stat.go.jp/data/mesh/m_tuite.htm">http://www.stat.go.jp/data/mesh/m_tuite.htm/</a></dd>
 * </dl>
 * 
 * @author People Flow Project, CSIS, UTokyo.
 */
public class Mesh implements Serializable {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** Serial */	private static final long serialVersionUID = -1130751934507725776L;

	/** height of level 1 mesh */	public static final Fraction LAT_HEIGHT_MESH1 = new Fraction(2,3);
	/** width of level 1 mesh  */	public static final Fraction LNG_WIDTH_MESH1  = new Fraction(1,1);
	/** height of level 2 mesh */	public static final Fraction LAT_HEIGHT_MESH2 = LAT_HEIGHT_MESH1.divide(8);
	/** width of level 2 mesh  */	public static final Fraction LNG_WIDTH_MESH2  = LNG_WIDTH_MESH1.divide(8);
	/** height of level 3 mesh */	public static final Fraction LAT_HEIGHT_MESH3 = LAT_HEIGHT_MESH2.divide(10);
	/** width of level 3 mesh  */	public static final Fraction LNG_WIDTH_MESH3  = LNG_WIDTH_MESH2.divide(10);
	/** height of level 4 mesh */	public static final Fraction LAT_HEIGHT_MESH4 = LAT_HEIGHT_MESH3.divide(2);
	/** width of level 4 mesh  */	public static final Fraction LNG_WIDTH_MESH4  = LNG_WIDTH_MESH3.divide(2);
	/** height of level 5 mesh */	public static final Fraction LAT_HEIGHT_MESH5 = LAT_HEIGHT_MESH4.divide(2);
	/** width of level 5 mesh  */	public static final Fraction LNG_WIDTH_MESH5  = LNG_WIDTH_MESH4.divide(2);
	/** height of level 6 mesh */	public static final Fraction LAT_HEIGHT_MESH6 = LAT_HEIGHT_MESH5.divide(2);
	/** width of level 6 mesh  */	public static final Fraction LNG_WIDTH_MESH6  = LNG_WIDTH_MESH5.divide(2);
		
	
	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** mesh rectangle        */	private Rectangle2D.Double _rect;
	/** representative point  */	private LonLat             _point;
	/** mesh code             */	private String             _code;
	/** mesh level            */	private int                _level;
	
	
	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * initialization with default configuration
	 */
	public Mesh() {
		super();
		// set value
		_level	= 1;
		_code	= "";
		_rect	= new Rectangle2D.Double();	
		_point	= new LonLat();
	}
	
	/**
	 * create mesh instance with mesh code
	 * @param code mesh code
	 */
	public Mesh(String code) {
		this();
		// set mesh code
		setCode(code);
	}
	
	/**
	 * create mesh instance with the indicated parameters
	 * @param level mesh level
	 * @param lon longitude(decimal)
	 * @param lat latitude(decimal)
	 */
	public Mesh(int level, double lon, double lat) {
		this();
		// set level 
		_level = level;
		// set mesh code
		setPoint(lon, lat);
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get mesh level
	 * @return mesh level
	 */
	public int getLevel() {
		return _level;
	}
	
	/**
	 * set mesh code level(default is 1)
	 * @param level mesh level(1-6)
	 */
	public void setLevel(final int level) {
		_level = level;
		// empty instance cannot update mesh information
		if( !getCode().isEmpty() ) { updateMeshInfo(); }
	}
	
	/**
	 * get mesh code
	 * @return mesh code
	 */
	public String getCode() {
		return _code;
	}
	
	/**
	 * set mesh code
	 * @param code mesh code
	 */
	public void setCode(String code) {
		_code = code;
		// check if mesh is available
		LonLat pt = parseMeshCode(code);
		// error
		if( pt == null ) { return; }
		
		setPoint(pt.getLon(), pt.getLat());
		// update point info
		pt.setLocation(getCenter());
	}
	
	/**
	 * set representative point
	 * @param x longitude(decimal)
	 * @param y latitude(decimal)
	 */
	public void setPoint(double x, double y) {
		_point.setLocation(x, y);
		// update mesh code
		updateMeshInfo();
	}
	
	/**
	 * get representative point information
	 * @return representative point
	 */
	public LonLat getPoint() {
		return _point;
	}
	
	/**
	 * set mesh rectangle
	 * @param x lower left longitude of mesh
	 * @param y lower left latitude of mesh
	 * @param w width(longitude direction)
	 * @param h height(latitude direction)
	 */
	public void setRect(double x, double y, double w, double h) {
		_rect.setRect(x, y, w, h);
	}
	
	/**
	 * get mesh as rectangle2D instance
	 * @return mesh rectangle
	 */
	public Rectangle2D.Double getRect() {
		return _rect;
	}
	
	/**
	 * get centroid position of this mesh
	 * @return centroid position
	 */
	public LonLat getCenter() {
		return new LonLat(_rect.getCenterX(), _rect.getCenterY());
	}
	
	/**
	 * get mesh height(latitude)
	 * @return mesh height in degree
	 */
	public final Fraction getHeightInDegree() {
		switch( getLevel() ) {
			case 1 : default :	return LAT_HEIGHT_MESH1;
			case 2 : 			return LAT_HEIGHT_MESH2;
			case 3 : 			return LAT_HEIGHT_MESH3;
			case 4 : 			return LAT_HEIGHT_MESH4;
			case 5 : 			return LAT_HEIGHT_MESH5;
			case 6 : 			return LAT_HEIGHT_MESH6;
		}	
	}
	
	/**
	 * get mesh width(longitude)
	 * @return mesh width in degree
	 */
	public Fraction getWidthInDegree() {
		switch( getLevel() ) {
			case 1 : default :	return LNG_WIDTH_MESH1;
			case 2 : 			return LNG_WIDTH_MESH2;
			case 3 : 			return LNG_WIDTH_MESH3;
			case 4 : 			return LNG_WIDTH_MESH4;
			case 5 : 			return LNG_WIDTH_MESH5;
			case 6 : 			return LNG_WIDTH_MESH6;
		}	
	}

	/**
	 * get mesh of 1 higher level mesh. 
	 * @return 1 higher level mesh. returns null if this mesh is level 1.
	 */
	public Mesh getUpperMesh() {
		String code = getCode();
		switch(getLevel()) {
			case 1 : default :	return null;
			case 2 :			return new Mesh(code.substring(0, 4));	// create level 1
			case 3 :			return new Mesh(code.substring(0, 6));	// create level 2
			case 4 : 			return new Mesh(code.substring(0, 8));	// create level 3
			case 5 : 			return new Mesh(code.substring(0, 9));	// create level 4
			case 6 :			return new Mesh(code.substring(0,10));	// create level 5
		}
	}
	
	/**
	 * list meshes of 1 lower level meshes
	 * @return 1 lower hierarchy meshes. returns null if this mesh is level 6. 
	 */
	public List<Mesh> listLowerMeshes() {
		String code = getCode();
		List<Mesh> list = new ArrayList<Mesh>();
		switch(getLevel()) {
			case 1 :	// list of 2 
				for(int i=0;i<8;i++) {
					for(int j=0;j<8;j++) {
						list.add(new Mesh(String.format("%s%d%d",code,i,j)));
					}
				}
				break;
			case 2 : 	// list of level 3
				for(int i=0;i<10;i++) {
					for(int j=0;j<10;j++) {
						list.add(new Mesh(String.format("%s%d%d",code,i,j))); 
					}
				}
				break;
			case 3 : case 4 : case 5 :	// list of 4,5,6 mesh
				for(int i=1;i<=4;i++) {
					list.add(new Mesh(String.format("%s%d",code,i))); 
				}
				break;
			case 6 : default :	// error
				list = null;
				break;
		}
		return list;
	}
	
	/**
	 * get list of 8 neighbors
	 * @return list of 8 neighbors
	 */
	public List<Mesh> list8Neighbors() {
		return listNeighbors(8);
	}
	
	/**
	 * get list of 4 neighbors
	 * @return list of 4 neighbors
	 */
	public List<Mesh> list4Neighbors() {
		return listNeighbors(4);
	}
	
	/**
	 * get list of 4/8 neighbors. 
	 * @param n size of neighbors
	 * @return mesh list of 4/8 neighbors
	 */
	private List<Mesh> listNeighbors(int n) {
		List<Mesh> list = new ArrayList<Mesh>();
		
		LonLat cp = getCenter();
		// set width(x) and height(y)
		Fraction dif_x = getWidthInDegree();
		Fraction dif_y = getHeightInDegree();

		// 4 neighbors
		int level = getLevel();
		list.add( new Mesh(level,cp.getLon(),                    cp.getLat()+dif_y.doubleValue()) ); // north
		list.add( new Mesh(level,cp.getLon()+dif_x.doubleValue(),cp.getLat())                     ); // east
		list.add( new Mesh(level,cp.getLon(),                    cp.getLat()-dif_y.doubleValue()) ); // south
		list.add( new Mesh(level,cp.getLon()-dif_x.doubleValue(),cp.getLat())                     ); // west
		// 8 neighbors
		if( n == 8 ) { 
			list.add( new Mesh(level,cp.getLon()+dif_x.doubleValue(),cp.getLat()+dif_y.doubleValue()) );// north east
			list.add( new Mesh(level,cp.getLon()+dif_x.doubleValue(),cp.getLat()-dif_y.doubleValue()) );// south east
			list.add( new Mesh(level,cp.getLon()-dif_x.doubleValue(),cp.getLat()-dif_y.doubleValue()) );// south west
			list.add( new Mesh(level,cp.getLon()-dif_x.doubleValue(),cp.getLat()+dif_y.doubleValue()) );// north west
		}
		return list;
	}
	
	/**
	 * check if the indicated mesh is 8 neighbor of this mesh. 
	 * @param mesh comparing mesh instance
	 * @return return true if mesh is in 8 neighbors, otherwise false.
	 */
	public boolean is8NeighborOf(Mesh mesh) {
		for(Mesh m : list8Neighbors()) {
			if( mesh.getCode().equals(m.getCode()) ) { return true; }
		}
		return false;
	}
	
	/**
	 * check if the indicated mesh is 4 neighbor of this mesh. 
	 * @param mesh comparing mesh instance
	 * @return return true if mesh is in 4 neighbors, otherwise false.
	 */
	public boolean is4NeighborOf(Mesh mesh) {
		for(Mesh m : list4Neighbors()) {
			if( mesh.getCode().equals(m.getCode()) ) { return true; }
		}
		return false;
	}

	/**
	 * get decimal coordinate in this mesh from the normalized coordinate
	 * @param x normalized x coordinate in this mesh
	 * @param y normalized y coordinate in this mesh
	 * @return corresponding position in LonLat instance
	 */
	public LonLat getPositionOf(int x, int y) {
		// get mesh rectangle
		Rectangle2D.Double rect = getRect();
		// calculate position within this mesh
		double lon = rect.getMinX() + (rect.getWidth()  * x / 10000d);
		double lat = rect.getMinY() + (rect.getHeight() * y / 10000d);
		// create LatLon instance
		return new LonLat(lon, lat);
	}
	
	/**
	 * update mesh code information
	 */
	private void updateMeshInfo() {
		// error handle /////////////////////////
		if( getLevel() < 0 || getLevel() > 6 ) { return; }
		
		double x = 0;
		double y = 0;
		// mesh level 1 : 4 characters ==============================
		int lat_1 = (int)(getPoint().getLat() * 1.5) % 100;	// mesh1 lat
		int lng_1 = (int)(getPoint().getLon() - 100);		// mesh1 lng
		// level 1 ==============================
		if( getLevel() >= 1 ) {
			// set rectangle area
			y += LAT_HEIGHT_MESH1.multiply(lat_1).doubleValue();
			x += lng_1 + 100;
			setRect(x, y, LNG_WIDTH_MESH1.doubleValue(), LAT_HEIGHT_MESH1.doubleValue());
			// Mesh code format: 
			_code = String.format("%02d%02d", lat_1, lng_1);
		}
		
		// level 2 ==============================
		if( getLevel() < 2 ) return;
		// mesh level 2 : append 2 additional characters 
		int lat_2 = (int)((getPoint().getLat() - 
							LAT_HEIGHT_MESH1.multiply(lat_1).doubleValue()) / LAT_HEIGHT_MESH2.doubleValue());
		int lng_2 = (int)((getPoint().getLon() - (lng_1 + 100)) / LNG_WIDTH_MESH2.doubleValue());
		// set rectangle area
		y += LAT_HEIGHT_MESH2.multiply(lat_2).doubleValue();
		x += LNG_WIDTH_MESH2.multiply(lng_2).doubleValue();
		setRect(x, y, LNG_WIDTH_MESH2.doubleValue(), LAT_HEIGHT_MESH2.doubleValue());
		// Mesh code format:  %02d%02d-%d%d
		_code = String.format("%02d%02d%d%d", lat_1, lng_1, lat_2, lng_2);
	
		
		// level 3 ==============================
		if( getLevel() < 3 ) return;
		// mesh level 3 : append 2 additional characters
		int lat_3 = (int)(	(	getPoint().getLat() - 
								LAT_HEIGHT_MESH1.multiply(lat_1).doubleValue() - 
								LAT_HEIGHT_MESH2.multiply(lat_2).doubleValue()	) 
								/ 	LAT_HEIGHT_MESH3.doubleValue()			);
		int lng_3 = (int)(	(	getPoint().getLon() - (lng_1 + 100) - 
								LNG_WIDTH_MESH2.multiply(lng_2).doubleValue()	) 
								/ 	LNG_WIDTH_MESH3.doubleValue() 			);
		// set rectangle area
		y += LAT_HEIGHT_MESH3.multiply(lat_3).doubleValue();
		x += LNG_WIDTH_MESH3.multiply(lng_3).doubleValue();
		setRect(x, y, LNG_WIDTH_MESH3.doubleValue(), LAT_HEIGHT_MESH3.doubleValue());
		// Mesh code format:  %02d%02d-%d%d-%d%d
		_code = String.format("%02d%02d%d%d%d%d", lat_1, lng_1, lat_2, lng_2, lat_3, lng_3) ;
		
	
		// level 4 ==============================
		if( getLevel() < 4 ) return;
		// mesh level 4 : append 2 additional characters
		int lat_4 = (int)(	(	getPoint().getLat() - 
								LAT_HEIGHT_MESH1.multiply(lat_1).doubleValue() - 
								LAT_HEIGHT_MESH2.multiply(lat_2).doubleValue() -
								LAT_HEIGHT_MESH3.multiply(lat_3).doubleValue()  	) 
								/ LAT_HEIGHT_MESH4.doubleValue()				);
		int lng_4 = (int)(	(	getPoint().getLon() - 
								(lng_1 + 100) - 
								LNG_WIDTH_MESH2.multiply(lng_2).doubleValue() - 
								LNG_WIDTH_MESH3.multiply(lng_3).doubleValue()	) 
								/ LNG_WIDTH_MESH4.doubleValue() 			);
		// set rectangle area
		y += LAT_HEIGHT_MESH4.multiply(lat_4).doubleValue();
		x += LNG_WIDTH_MESH4.multiply(lng_4).doubleValue();
		setRect(x, y, LNG_WIDTH_MESH4.doubleValue(), LAT_HEIGHT_MESH4.doubleValue());
		// Mesh code format: %02d%02d-%d%d-%d%d-%d
		_code = String.format("%02d%02d%d%d%d%d%d", lat_1, lng_1, lat_2, lng_2, lat_3, lng_3, 
													composeCode(lat_4, lng_4));
		
		// level 5 ==============================
		if( getLevel() < 5 ) return;
		// mesh level 5 : append 1 additional characters
		int lat_5 = (int)(	(	getPoint().getLat() - 
								LAT_HEIGHT_MESH1.multiply(lat_1).doubleValue() - 
								LAT_HEIGHT_MESH2.multiply(lat_2).doubleValue() -
								LAT_HEIGHT_MESH3.multiply(lat_3).doubleValue() -
								LAT_HEIGHT_MESH4.multiply(lat_4).doubleValue()  	) 
								/ LAT_HEIGHT_MESH5.doubleValue()				);
		int lng_5 = (int)(	(	getPoint().getLon() - 
								(lng_1 + 100) - 
								LNG_WIDTH_MESH2.multiply(lng_2).doubleValue() - 
								LNG_WIDTH_MESH3.multiply(lng_3).doubleValue() - 
								LNG_WIDTH_MESH4.multiply(lng_4).doubleValue()	) 
								/ LNG_WIDTH_MESH5.doubleValue() 			);
		// set rectangle area
		y += LAT_HEIGHT_MESH5.multiply(lat_5).doubleValue();
		x += LNG_WIDTH_MESH5.multiply(lng_5).doubleValue();
		setRect(x, y, LNG_WIDTH_MESH5.doubleValue(), LAT_HEIGHT_MESH5.doubleValue());
		// Mesh code format:  %02d%02d-%d%d-%d%d-%d-%d
		_code = String.format("%02d%02d%d%d%d%d%d%d", 	lat_1, lng_1, lat_2, lng_2, lat_3, lng_3, 
														composeCode(lat_4, lng_4),
														composeCode(lat_5, lng_5) );
		
		// level 6 ==============================
		if( getLevel() < 6 ) return;
		// mesh level 6 : append 1 additional characters
		int lat_6 = (int)(	(	getPoint().getLat() - 
								LAT_HEIGHT_MESH1.multiply(lat_1).doubleValue() - 
								LAT_HEIGHT_MESH2.multiply(lat_2).doubleValue() -
								LAT_HEIGHT_MESH3.multiply(lat_3).doubleValue() -
								LAT_HEIGHT_MESH4.multiply(lat_4).doubleValue() -
								LAT_HEIGHT_MESH5.multiply(lat_5).doubleValue()  	) 
								/ LAT_HEIGHT_MESH6.doubleValue()				);
		int lng_6 = (int)(	(	getPoint().getLon() - 
								(lng_1 + 100) - 
								LNG_WIDTH_MESH2.multiply(lng_2).doubleValue() - 
								LNG_WIDTH_MESH3.multiply(lng_3).doubleValue() - 
								LNG_WIDTH_MESH4.multiply(lng_4).doubleValue() - 
								LNG_WIDTH_MESH5.multiply(lng_5).doubleValue()	) 
								/ LNG_WIDTH_MESH6.doubleValue() 			);
		// set rectangle area
		y += LAT_HEIGHT_MESH6.multiply(lat_6).doubleValue();
		x += LNG_WIDTH_MESH6.multiply(lng_6).doubleValue();
		setRect(x, y, LNG_WIDTH_MESH6.doubleValue(), LAT_HEIGHT_MESH6.doubleValue());
		// Mesh code format:  %02d%02d-%d%d-%d%d-%d-%d-%d
		_code = String.format("%02d%02d%d%d%d%d%d%d%d",	lat_1, lng_1, lat_2, lng_2, lat_3, lng_3,
														composeCode(lat_4, lng_4), 
														composeCode(lat_5, lng_5), 
														composeCode(lat_6, lng_6) );
	}
	
	/**
	 * get code index under level 4 mesh
	 * @param lat index of latitude
	 * @param lon index of longitude
	 * @return mesh code index
	 */
	private int composeCode (int latIndex, int lonIndex) {
		if( latIndex == 0 && lonIndex == 0 ) { return 1; }
		else if( latIndex == 0 && lonIndex == 1 ) { return 2; }
		else if( latIndex == 1 && lonIndex == 0 ) { return 3; }
		else if( latIndex == 1 && lonIndex == 1 ) { return 4; }
		else { return 0; }
	}
	
	/**
	 * extract a point within the rectangle of indicated mesh code 
	 * @param meshcode mesh code
	 * @return a point within the rectangle of mesh code
	 */
	private LonLat parseMeshCode(String meshcode) {
		// remove hyphen in the mesh code /////////////////
		String str = meshcode.replaceAll("-", "");
		
		int strlen = str.length();
		if( (strlen == 0) || strlen > 11 ) { return null; }
		// small value
		double x = .0000000001;
		double y = .0000000001;
		// level 1
		if( strlen >= 4 ) {
			y += LAT_HEIGHT_MESH1.multiply(new Integer(str.substring(0,2)).intValue()).doubleValue();
			x += 100 + new Integer(str.substring(2,4)).intValue();
			_level = 1;
		}
		// level 2
		if( strlen >= 6 ) {
			y += LAT_HEIGHT_MESH2.multiply(new Integer(str.substring(4,5)).intValue()).doubleValue();
			x += LNG_WIDTH_MESH2.multiply(new Integer(str.substring(5,6)).intValue()).doubleValue();
			_level = 2;
		}
		// level 3
		if( strlen >= 8 ) {
			y += LAT_HEIGHT_MESH3.multiply(new Integer(str.substring(6,7)).intValue()).doubleValue();
			x += LNG_WIDTH_MESH3.multiply(new Integer(str.substring(7,8)).intValue()).doubleValue();
			_level = 3;
		}
		// level 4
		if( strlen >= 9 ) {
			int n = new Integer(str.substring(8,9)).intValue();
			y += LAT_HEIGHT_MESH4.multiply( (n<=2 ? 0 : 1) ).doubleValue();
			x += LNG_WIDTH_MESH4.multiply( (n%2==1 ? 0 : 1) ).doubleValue();
			_level = 4;
		}
		// level 5
		if( strlen >= 10 ) {
			int n = new Integer(str.substring(9,10)).intValue();
			y += LAT_HEIGHT_MESH5.multiply( (n<=2 ? 0 : 1) ).doubleValue();
			x += LNG_WIDTH_MESH5.multiply( (n%2==1 ? 0 : 1) ).doubleValue();
			_level = 5;
		}
		// level 6
		if( strlen >= 11 ) {
			int n = new Integer(str.substring(10,11)).intValue();
			y += LAT_HEIGHT_MESH6.multiply( (n<=2 ? 0 : 1) ).doubleValue();
			x += LNG_WIDTH_MESH6.multiply( (n%2==1 ? 0 : 1) ).doubleValue();
			_level = 6;
		}
		return new LonLat(x,y);
	}
	
	/* @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		// compare mesh code if mesh class instance
		if( obj instanceof Mesh ) {
			String code1 = getCode().replaceAll("-", "");
			String code2 = Mesh.class.cast(obj).getCode().replaceAll("-", "");
			return code1.equals(code2);
		}
		return false;
	}
	
	/* @see java.lang.Object#toString() */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append(getClass().getName()).append("[");
		buf.append("code="	).append(getCode()	).append(",");
		buf.append("rect="	).append(getRect()	).append(",");
		buf.append("point="	).append(getPoint()	);
		buf.append("]");
		
		return buf.toString();
	}

	/* @see java.lang.Object#clone() */
	@Override
	public Mesh clone() {
		LonLat pt = getPoint();
		// create new instance
		return new Mesh(getLevel(), pt.getLon(), pt.getLat());
	}
}
