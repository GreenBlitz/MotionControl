package gbmotion.path;

import gbmotion.base.point.IPoint2D;
import gbmotion.base.point.Point2D;

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
	public PathFactory connectLine(Point2D connectTo, double metersPerPoint) {
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
	 * Like {@link PathFactory#connectLine(IPoint2D, double)} but with
	 * different parameters
	 * 
	 * @param x
	 * @param y
	 * @param metersPerPoint
	 * @return
	 */
	public PathFactory conncetLine(double x, double y, double metersPerPoint) {
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
