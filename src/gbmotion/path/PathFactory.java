package gbmotion.path;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import gbmotion.base.point.IPoint2D;
import gbmotion.base.point.ImPoint2D;
import gbmotion.base.point.Point2D;
import gbmotion.util.MathUtil;

public class PathFactory {

	private Path m_path = new Path();

	public PathFactory() {
		m_path.add(Point2D.immutable(0, 0));
	}

	/**
	 * @param origin
	 *            path origin
	 */
	public PathFactory(Point2D origin) {
		m_path.add(origin);
	}

	/**
	 * @param path1
	 */
	public PathFactory(Path path) {
		m_path = path;
		if (path.getTotalLength() == 0) {
			System.err.println("No origin supplied to path, setting default");
			m_path.add(Point2D.immutable(0, 0));
		}
	}

	/**
	 * Continue the path in a straight line form the last path point to the
	 * given point
	 * 
	 * @param connectTo
	 *            the given point
	 * @param metersPerPoint
	 *            the distance in meters between each point
	 * @return the factory
	 */
	public PathFactory connectLine(IPoint2D connectTo, double metersPerPoint) {
		IPoint2D origin = m_path.getLast();
		double length = origin.distance(connectTo);
		if (length == 0)
			return this;
		double totalPoints = length / metersPerPoint;

		double xJump = (connectTo.getX() - origin.getX()) / totalPoints,
				yJump = (connectTo.getY() - origin.getY()) / totalPoints;

		while (m_path.getLast().distance(connectTo) > 0) {
			if (m_path.getLast().distance(connectTo) <= metersPerPoint) {
				m_path.add(connectTo);
				break;
			}
			m_path.add(m_path.getLast().moveBy(xJump, yJump));
		}
		return this;
	}
	
	/**
	 * adds a round section to the path using bazier curve
	 * @param numOfPoints
	 * 		the number of points in the added curve.
	 * @param poles
	 * 		the poles you want to make into a curve
	 * @return
	 */
	private PathFactory bazierCurve(double numOfPoints, ImPoint2D... poles){
		numOfPoints = 1/numOfPoints;
		ImPoint2D[] polesAgain = new ImPoint2D[poles.length+1];
		ImPoint2D origin = Point2D.immutable(m_path.getLast());
		for(double phase=numOfPoints; phase<1; phase+=numOfPoints){
			polesAgain[0] = origin;
			for(int i=0; i<poles.length; i++){
				polesAgain[i+1] = poles[i];
			}
			m_path.add(MathUtil.bazierPhase(phase, polesAgain));
		}
		m_path.add(Point2D.immutable(poles[poles.length-1]));
		return this;
	}
	
	/**
	 * 
	 * @param metersPerPoint
	 * @param roundingSize
	 * 		the size of the rounded part in each corner
	 * @param points
	 * the points to be connected
	 * @return
	 */
	public PathFactory smartConnect(double metersPerPoint, double roundingSize, IPoint2D... points){
		ImPoint2D[] pointsAgain = new ImPoint2D[points.length];
		for(int i=0; i<points.length;) pointsAgain[i] = Point2D.immutable(points[i++]);
		for(int i=0; i<points.length-1;){
			connectLine(MathUtil.nextTo(pointsAgain[i], m_path.getLast(), roundingSize), metersPerPoint);
			bazierCurve(metersPerPoint/(2*roundingSize), pointsAgain[i], MathUtil.nextTo(pointsAgain[i], pointsAgain[++i], roundingSize));
		}
		connectLine(points[points.length-1], metersPerPoint);
		return this;
	}

	/**
	 * Like {@link PathFactory#connectLine(IPoint2D, double)} but with
	 * different parameters
	 * 
	 * @param x
	 * @param y
	 * @param metersPerPoint
	 * @return
	 */
	public PathFactory connectLine(double x, double y, double metersPerPoint) {
		return connectLine(Point2D.immutable(x, y), metersPerPoint);
	}

	/**
	 * Generate a straight line from the last path point to a point a certain
	 * distance and angle form it
	 * 
	 * @param len
	 *            the distance
	 * @param rotation
	 *            the angle in radians to rotate the path from the positive y
	 *            axis.
	 * @param metersPerPoint
	 *            the distance in meters between each point
	 * @return the factory
	 */
	public PathFactory genStraightLine(double len, double rotation, double metersPerPoint) {
		IPoint2D origin = m_path.getLast();
		for (double i = metersPerPoint; i < len + metersPerPoint; i += metersPerPoint) {
			m_path.add(( Point2D.immutable(0, i).rotate(rotation))
					.moveBy(origin));
		}
		return this;
	}

	public ArenaMap construct(ArenaMap map) {
		NetworkTable motionTable = NetworkTable.getTable("motion");
		double[] arr = new double[m_path.getTotalLength() * 2];
		int i = arr.length;
		for (IPoint2D pt : m_path){
			arr[--i] = pt.getX();
			arr[--i] = pt.getY();
		}
		for (int j = 0; j < arr.length; j+=255){
			double[] cur = new double[255];
			System.arraycopy(arr, j, cur, 0, Math.min(255, arr.length - j));
			motionTable.putNumberArray("path" + j / 255, cur);
		}
		motionTable.putNumber("pathLength", arr.length / 2);
		map.construct(m_path);
		return map;
	}

	/**
	 * 
	 * @param length
	 * @param invert
	 * @param metersPerPoint
	 * @return
	 */
	public PathFactory genSidewayPath(double length, boolean invert, double metersPerPoint) {
		if (invert)
			metersPerPoint = -metersPerPoint;
		for (double i = metersPerPoint; Math.abs(i) < length; i += metersPerPoint)
			m_path.add(Point2D.immutable(i, 0));
		m_path.add(Point2D.immutable(invert ? -length : length, 0));
		return this;
	}

	/**
	 * 
	 * @param length
	 * @param invert
	 * @param metersPerPoint
	 * @return
	 */
	public PathFactory genForwardPath(double length, boolean invert, double metersPerPoint) {
		if (invert)
			metersPerPoint = -metersPerPoint;
		for (double i = metersPerPoint; Math.abs(i) < length; i += metersPerPoint)
			m_path.add(Point2D.immutable(0, i));
		m_path.add(Point2D.immutable(0, invert ? -length : length));
		return this;
	}
}
