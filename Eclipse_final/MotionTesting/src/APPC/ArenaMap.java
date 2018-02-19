package APPC;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import base.point.IPoint2D;

/**
 * maps a path on the arena used to replace the class Path and improve
 * updateGoalPoint() performance hashing a point to estimation of coordinates
 * and uses chaining
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ArenaMap {

	public static class MapOutOfBoundsException extends IndexOutOfBoundsException {
		private static final long serialVersionUID = -8720706204736267096L;

		public MapOutOfBoundsException() {
			super();
		}

		public MapOutOfBoundsException(String message) {
			super(message);
		}

		/**
		 * Generates an error message which describes the available axis
		 * locations
		 * 
		 * @param axisValue
		 * @param axisLength
		 * @param axisOffset
		 * @param axisName
		 */
		public MapOutOfBoundsException(double axisValue, double axisLength, double axisOffset, String axisName) {
			super(String.format("invalid location %f on axis '%s': expected values between %f and %f", round(axisValue),
					axisName, round(-axisOffset), round(axisLength - axisOffset)));
		}

		private static final String round(double num) {
			DecimalFormat dec = new DecimalFormat("#0.000");
			return dec.format(num);
		}
	}

	/**
	 * The map
	 */
	private LinkedList<IndexedPoint2D>[][] m_map;

	/**
	 * A list of the points inserted, used to clear the map
	 */
	private LinkedList<IndexedPoint2D> m_path = new LinkedList<IndexedPoint2D>();

	private final double m_xAxisOffset;
	private final double m_yAxisOffset;

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
		m_map = new LinkedList[(int) Math.ceil(width / accuracy)][(int) Math.ceil(height / accuracy)];
		for (int x = 0; x < m_map.length; x++)
			for (int y = 0; y < m_map[x].length; y++)
				m_map[x][y] = new LinkedList();
		m_xAxisOffset = xAxisOffset;
		m_yAxisOffset = yAxisOffset;
	}

	/**
	 * Constructs an arena map with offsets 0
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
		this(accuracy, width, height, 0, 0);
	}

	/**
	 * the hash function
	 * 
	 * @param point
	 * @return calculated hash value of the point
	 */
	private int[] hash(IPoint2D point) {
		return new int[] { (int) ((point.getX() + m_xAxisOffset) / m_mapAccuracy),
				(int) ((point.getY() + m_yAxisOffset) / m_mapAccuracy) };
	}

	/**
	 * inserts a point to the map
	 * 
	 * @param point
	 *            point which will be inserted
	 */
	public void insert(IPoint2D point) {
		int[] loc = hash(point);
		List[] arr = null;
		IndexedPoint2D iPoint = new IndexedPoint2D(point, m_path.size());

		try {
			arr = m_map[loc[0]];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MapOutOfBoundsException(point.getX(), m_map.length, m_xAxisOffset, "x");
		}

		try {
			arr[loc[1]].add(iPoint);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MapOutOfBoundsException(point.getY(), m_map[0].length, m_yAxisOffset, "y");
		}
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
		int y1 = Math.min(mapLoc[1] + radInSqrs, m_map.length - 1);
		double dontCollectGC;
		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (IndexedPoint2D point : (List<IndexedPoint2D>) m_map[x][y]) {
					dontCollectGC = point.distanceSquared(loc);
					if (minRadiusSq <= dontCollectGC && dontCollectGC <= maxRadiusSq
							&& (ret == null || ret.getIndex() < point.index))
						ret = point;
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
		return ret;
	}

	/**
	 * @return last point in the path
	 */
	public IPoint2D getLast() {
		return m_path.getLast();
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
