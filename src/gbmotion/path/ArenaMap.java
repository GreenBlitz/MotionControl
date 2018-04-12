package gbmotion.path;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.usfirst.frc.team4590.robot.Robot;

import gbmotion.base.point.IPoint2D;

/**
 * maps a path on the arena used to replace the class Path and improve
 * updateGoalPoint() performance hashing a point to estimation of coordinates
 * and uses chaining
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ArenaMap {

	/**
	 * Indicates that an attempt was made to place a point outside of the given map
	 * @author karlo
	 */
	public static class OutOfMapException extends RuntimeException {
		private static final long serialVersionUID = -8720706204736267096L;

		public OutOfMapException() {
			super();
		}

		public OutOfMapException(String message) {
			super(message);
		}

		/**
		 * Generates an error message which describes the available axis
		 * locations
		 * 
		 * @param axisValue
		 *            value on given axis
		 * @param axisLength
		 *            mapped length of given axis
		 * @param axisOffset
		 *            map offset at given axis
		 * @param axisName
		 *            given axis' name
		 * @param mapAccuracy
		 *            given map accuracy
		 */
		public OutOfMapException(double axisValue, double axisLength, double axisOffset, String axisName,
				double mapAccuracy) {
			super(String.format("invalid location %s on axis '%s': expected values between %s and %s", round(axisValue),
					axisName, round(-axisOffset * mapAccuracy), round((axisLength - axisOffset) * mapAccuracy)));
		}

		private static final String round(double num) {
			DecimalFormat dec = new DecimalFormat("#0.000");
			return dec.format(num);
		}
	}

	private static final double METRE_PER_FOOT = .3048;
	
	public static final double DEFAULT_MAP_ACCURACY = 0.1;
	public static final double DEFAULT_HEIGHT = 54 * METRE_PER_FOOT;
	public static final double DEFAULT_WIDTH = 27 * METRE_PER_FOOT;
	
	/**
	 * X axis offset (with relation to {@code DEFAULT_WIDTH})
	 */
	public static final double DEFAULT_X_OFFSET = DEFAULT_WIDTH / 2;
	
	/**
	 * Y axis offset (with relation to {@code DEFAULT_HEIGHT})
	 */
	public static final double DEFAULT_Y_OFFSET = DEFAULT_HEIGHT / 2;
	

	/**
	 * The map
	 */
	private LinkedList<IndexedPoint2D>[][] m_map;

	/**
	 * A list of the points inserted, used to clear the map
	 */
	private LinkedList<IndexedPoint2D> m_path = new LinkedList<IndexedPoint2D>();

	/**
	 * X axis offset (in map blocks)
	 */
	private final int m_xAxisOffset;
	
	/**
	 * Y axis offset (in map blocks)
	 */
	private final int m_yAxisOffset;

	/**
	 * Map accuracy (ratio between an axis' length and amount of blocks on it, identical for both x and y)
	 */
	private final double m_mapAccuracy;

	/**
	 * @param accuracy
	 *            map accuracy
	 * @param width
	 *            arena width (x axis total length)
	 * @param height
	 *            arena height (y axis total length)
	 * @param xAxisOffset
	 *            initial position x axis
	 * @param yAxisOffset
	 *            initial position y axis
	 */
	public ArenaMap(double accuracy, double width, double height, double xAxisOffset, double yAxisOffset) {
		m_mapAccuracy = accuracy;
		m_map = new LinkedList[(int) (width / accuracy)][(int) (height / accuracy)];
		for (int x = 0; x < m_map.length; x++)
			for (int y = 0; y < m_map[x].length; y++)
				m_map[x][y] = new LinkedList();
		m_xAxisOffset = (int) (xAxisOffset / accuracy);
		m_yAxisOffset = (int) (yAxisOffset / accuracy);
	}

	/**
	 * Constructs an arena map with offsets at the middle of the arena
	 * 
	 * @see ArenaMap#ArenaMap(double, double, double, double, double)
	 * @param accuracy
	 *            map accuracy
	 * @param width
	 *            arena width
	 * @param height
	 *            arena height
	 */
	public ArenaMap(double accuracy, double width, double height) {
		this(accuracy, width, height, width / 2, height / 2);
	}

	/**
	 * Constructs an arena map using the default parameters noted in this class
	 */
	public ArenaMap() {
		this(DEFAULT_MAP_ACCURACY, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET);
	}

	/**
	 * the hash function
	 * 
	 * @param point
	 * @return calculated hash value of the point
	 */
	private int[] hash(IPoint2D point) {
		return new int[] { (int) (point.getX() / m_mapAccuracy) + m_xAxisOffset,
				(int) (point.getY() / m_mapAccuracy) + m_yAxisOffset };
	}

	/**
	 * inserts a point to the map
	 * 
	 * @param point
	 *            point which will be inserted
	 */
	public void insert(IPoint2D point) {
		int[] loc = hash(point);
		IndexedPoint2D iPoint = new IndexedPoint2D(point, m_path.size());

		if (loc[0] >= m_map.length)
			throw new OutOfMapException(point.getX(), m_map.length, m_xAxisOffset, "x", m_mapAccuracy);

		if (loc[1] >= m_map[0].length)
			throw new OutOfMapException(point.getY(), m_map[0].length, m_yAxisOffset, "y", m_mapAccuracy);

		m_map[loc[0]][loc[1]].add(iPoint);
		m_path.add(iPoint);
	}

	/**
	 * takes a path (usually of type PathFactory) and draws it on the map
	 * 
	 * @param path
	 *            the path which will be inserted to the map
	 */
	public void construct(Iterable<IPoint2D> path) {
		clear();
		for (IPoint2D point : path) {
			insert(point);
		}
	}

	/**
	 * finds the closest point to a given point ({@code loc}) from a list of
	 * points ({@code list})
	 * 
	 * @param list
	 * @param loc
	 * @return
	 */
	private IPoint2D findClosest(LinkedList<? extends IPoint2D> list, IPoint2D loc) {
		if (list.isEmpty())
			return null;
		IPoint2D close = list.getFirst();
		for (IPoint2D point : list)
			if (point.distanceSquared(loc) < close.distanceSquared(loc))
				close = point;
		return close;
	}

	/**
	 * generates a list of all the points in a given range of distances (
	 * {@code minRadius}, {@code maxRadius}) from a given point ({@code loc})
	 * 
	 * @param loc
	 * @param minRadius
	 * @param maxRadius
	 * @return
	 */
	private LinkedList<IndexedPoint2D> pointsInRange(IPoint2D loc, double minRadius, double maxRadius) {
		double minRadiusSq = minRadius * minRadius, maxRadiusSq = maxRadius * maxRadius;
		int radInSqrs = (int) (maxRadius / m_mapAccuracy) + 1;
		LinkedList<IndexedPoint2D> inRange = new LinkedList<IndexedPoint2D>();
		int[] mapLoc = hash(loc);
		int x0 = Math.max(mapLoc[0] - radInSqrs, 0);
		int x1 = Math.min(mapLoc[0] + radInSqrs, m_map.length);
		int y0 = Math.max(mapLoc[1] - radInSqrs, 0);
		int y1 = Math.min(mapLoc[1] + radInSqrs, m_map.length);
		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (IndexedPoint2D point : (List<IndexedPoint2D>) m_map[x][y]) {
					if (minRadiusSq <= point.distanceSquared(loc) && point.distanceSquared(loc) <= maxRadiusSq)
						inRange.add(point);
				}
		return inRange;
	}

	public IndexedPoint2D lastPointInRange(IPoint2D loc, double minRadius, double maxRadius) {
		double minRadiusSq = minRadius * minRadius, maxRadiusSq = maxRadius * maxRadius;
		int radInSqrs = (int) (maxRadius / m_mapAccuracy) + 1;
		IndexedPoint2D ret = null;
		int[] mapLoc = hash(loc);
		int x0 = Math.max(mapLoc[0] - radInSqrs, 0);
		int x1 = Math.min(mapLoc[0] + radInSqrs, m_map.length - 1);
		int y0 = Math.max(mapLoc[1] - radInSqrs, 0);
		int y1 = Math.min(mapLoc[1] + radInSqrs, m_map[0].length - 1);
		double dontCollectGC;
		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++){
				Robot.managedPrinter.println(getClass(), "reached square x: " + x + ", y: " + y);
				for (IndexedPoint2D point : (List<IndexedPoint2D>) m_map[x][y]) {
					dontCollectGC = point.distanceSquared(loc);
					Robot.managedPrinter.println(getClass(), "reached");
					if (minRadiusSq <= dontCollectGC && dontCollectGC <= maxRadiusSq
							&& (ret == null || ret.getIndex() < point.index))
						ret = point;
				}
			}
		Robot.managedPrinter.println(getClass(), ret);
		return ret;
	}
	public IndexedPoint2D lastPointInRangeBF(IPoint2D loc, double minRadius, double maxRadius) {
	
		double minRadiusSq = minRadius * minRadius, maxRadiusSq = maxRadius * maxRadius;
		int radInSqrs = (int) (maxRadius / m_mapAccuracy) + 1;
		IndexedPoint2D ret = null;
		int[] mapLoc = hash(loc);
		int x0 = Math.max(mapLoc[0] - radInSqrs, 0);
		int x1 = Math.min(mapLoc[0] + radInSqrs, m_map.length - 1);
		int y0 = Math.max(mapLoc[1] - radInSqrs, 0);
		int y1 = Math.min(mapLoc[1] + radInSqrs, m_map[0].length - 1);
		double dontCollectGC;
		for (int x = 0; x < m_map.length; x++)
			for (int y = 0; y < m_map[0].length; y++){
				for (IndexedPoint2D point : m_map[x][y]) {
					dontCollectGC = point.distanceSquared(loc);
					if (minRadiusSq <= dontCollectGC && dontCollectGC <= maxRadiusSq
							&& (ret == null || (ret.getIndex() < point.index)))
						ret = point;
				}
			}
		Robot.managedPrinter.println(getClass(), ret);
		if (ret == null) {
			ret = (IndexedPoint2D) closestPoint(loc, maxRadius);
		}
		return ret;
	}

	/**
	 * finds the closest point to a given point ({@code loc}) uses
	 * {@code radius} for recursive search
	 * 
	 * @param loc
	 * @param radius
	 * @return
	 */
	private IPoint2D closestPoint(IPoint2D loc) {
		return closestPoint(loc, 1);
	}

	private IPoint2D closestPoint(IPoint2D loc, double radius) {
		if (radius > 2 * m_mapAccuracy * (m_map.length + m_map[0].length))
			return null;
		IPoint2D ret = findClosest(pointsInRange(loc, 0, 2 * radius), loc);
		if (ret != null)
			return ret;
		return closestPoint(loc, 2 * radius);
	}

	/**
	 * used in updateGoalPoint() returns the last point in the path (by index)
	 * within a given distance (radius) of a given point (loc) otherwise returns
	 * the closest point to (loc)
	 * 
	 * @param loc
	 * @param radius
	 * @return
	 */

	public IPoint2D lastPointInRange(IPoint2D loc, double radius) {
		IPoint2D ret = lastPointInRange(loc, 0, radius);

		if (ret == null) {
			ret = closestPoint(loc, radius);
		}
		Robot.managedPrinter.printf(getClass(), "location: %s, point in range: %s, radius: %f\r\n", loc, ret, radius);
		return ret;
	}

	/**
	 * @return last point in the path
	 */
	public IPoint2D getLast() {
		try {
			return m_path.getLast();
		} catch (NoSuchElementException e) {
			throw new IllegalStateException("empty arena map");
		}
		
	}

	/**
	 * resets the map to empty
	 */
	public void clear() {
		int[] loc;
		for (IndexedPoint2D point : m_path) {
			loc = hash(point);
			if (!m_map[loc[0]][loc[1]].isEmpty())
				m_map[loc[0]][loc[1]] = new LinkedList();
		}
		m_path = new LinkedList<IndexedPoint2D>();
	}
}
