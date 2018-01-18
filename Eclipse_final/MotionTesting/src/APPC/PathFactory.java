package APPC;

import java.util.ArrayList;

public class PathFactory {
	
	private Path m_path = new Path();
	
	public PathFactory() { m_path.add(new Point2D(0, 0, 0)); }
	public PathFactory(Point2D origin) { m_path.add(origin); }
	public PathFactory(Path path) {
		m_path = path;
		if (path.getTotalLength() == 0){
			System.err.println("No origin supplied to path, setting default");
			m_path.add(new Point2D(0, 0, 0));
		}
	}
	
	// TODO this
	@Deprecated
	public PathFactory connectLine(Point2D connectTo, double metersPerPoint){
		return this;
	}
	
	public PathFactory genStraightLine(double len, double rotation, double metersPerPoint){
		Point2D origin = m_path.getLast();
		for(double i = 0; i <= len; i += metersPerPoint){
			m_path.add(new Point2D(0, i, 0).rotate(rotation).moveBy(origin));
			//System.out.println(m_path.getLast());
		}
		return this;
	}
	
	public Path construct(){ return m_path; }
	
	
	
}