package APPC;

public class PathFactory {

	private Path m_path = new Path();

	public PathFactory() {
		m_path.add(new Orientation2D(0, 0, 0));
	}

	/**
	 * 
	 * @param origin
	 */
	public PathFactory(Orientation2D origin) {
		m_path.add(origin);
	}

	/**
	 * 
	 * @param path
	 */
	public PathFactory(Path path) {
		m_path = path;
		if (path.getTotalLength() == 0) {
			System.err.println("No origin supplied to path, setting default");
			m_path.add(new Orientation2D(0, 0, 0));
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
	public PathFactory connectLine(Orientation2D connectTo, double metersPerPoint) {
		Orientation2D origin = m_path.getLast();
		Orientation2D distance = origin.distanceVector(connectTo);
		if (distance.length() == 0)
			return this;
		double totalPoints = distance.length() / metersPerPoint;

		double xJump = distance.getX() / totalPoints, yJump = distance.getY() / totalPoints;

		while (m_path.getLast().distance(connectTo) > 0) {
			if (m_path.getLast().distance(connectTo) <= metersPerPoint) {
				// Code almost done, finish it off
				m_path.add(connectTo);
				break;
			}
			m_path.add(m_path.getLast().add(xJump, yJump));
		}
		return this;
	}

	/**
	 * Like {@link PathFactory#connectLine(Orientation2D, double)} but with different parameters
	 * @param x
	 * @param y
	 * @param metersPerPoint
	 * @return
	 */
	public PathFactory conncetLine(double x, double y, double metersPerPoint){
		return connectLine(new Orientation2D(x, y, 0), metersPerPoint);
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
		Orientation2D origin = m_path.getLast();
		for (double i = metersPerPoint; i < len + metersPerPoint; i += metersPerPoint) {
			m_path.add(new Orientation2D(0, i, 0).rotate(rotation).addButNotStupid(origin));
			// System.out.println(m_path.getLast());
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
			m_path.add(new Orientation2D(i, 0, 0));
		m_path.add(new Orientation2D(invert ? -length : length, 0, 0));
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
			m_path.add(new Orientation2D(0, i, 0));
		m_path.add(new Orientation2D(0, invert ? -length : length, 0));
		return this;
	}

}
