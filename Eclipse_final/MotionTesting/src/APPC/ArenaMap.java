package APPC;

import java.util.LinkedList;
import java.util.List;

/**
 * maps a path on the arena used to replace the class Path and improve
 * updateGoalPoint() performance hashing a point to estimation of coordinates
 * and uses chaining
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ArenaMap {
	// the map
	private LinkedList<IndexedOrientation2D>[][] m_map;
	// a list of the points inserted, used to clear the map
	private LinkedList<IndexedOrientation2D> m_path = new LinkedList<IndexedOrientation2D>();
	//
	private final double m_mapAccuracy;

	public ArenaMap(double acc, double arenaXLength, double arenaYLength) {
		m_mapAccuracy = acc;
		m_map = new LinkedList[(int) (arenaXLength / acc)][(int) (arenaYLength / acc)];
		for (int x = 0; x < m_map.length; x++)
			for (int y = 0; y < m_map[x].length; y++)
				m_map[x][y] = new LinkedList();
	}

	// the hash function
	private int[] getLoc(Orientation2D point) {
		return new int[] { (int) (point.getX() / m_mapAccuracy), (int) (point.getY() / m_mapAccuracy) };
	}

	/**
	 * inserts a point to the map
	 * 
	 * @param point
	 */
	public void insert(Orientation2D point) {
		int[] loc = getLoc(point);
		IndexedOrientation2D IPoint = new IndexedOrientation2D(point, m_path.size());
		m_map[loc[0]][loc[1]].add(IPoint);
		m_path.add(IPoint);

	}

	/**
	 * takes a path (usually of type PathFactory) and draws it on the map
	 * 
	 * @param path
	 */
	public void construct(Iterable<Orientation2D> path) {
		clear();
		for (Orientation2D point : path) {
			insert(point);
		}
	}

	/**
	 * finds the closest point to a given point (loc) from a list of points
	 * (list)
	 * 
	 * @param list
	 * @param loc
	 * @return
	 */
	private Orientation2D findClosest(LinkedList<? extends Orientation2D> list, Orientation2D loc) {
		if (list.isEmpty())
			return null;
		Orientation2D close = list.getFirst();
		for (Orientation2D point : list)
			if (point.distanceSquared(loc) < close.distanceSquared(loc))
				close = point;
		return close;
	}

	/**
	 * generates a list of all the points in a given range of distances
	 * (minRadius, maxRadius) from a given point (loc)
	 * 
	 * @param loc
	 * @param minRadius
	 * @param maxRadius
	 * @return
	 */
	private LinkedList<IndexedOrientation2D> pointsInRange(Orientation2D loc, double minRadius, double maxRadius) {
		double minRadiusSq = minRadius * minRadius, maxRadiusSq = maxRadius * maxRadius;
		int radInSqrs = (int) (maxRadius / m_mapAccuracy) + 1;
		LinkedList<IndexedOrientation2D> inRange = new LinkedList<IndexedOrientation2D>();
		int[] mapLoc = getLoc(loc);
		int x0 = Math.max(mapLoc[0] - radInSqrs, 0);
		int x1 = Math.min(mapLoc[0] + radInSqrs, m_map.length);
		int y0 = Math.max(mapLoc[1] - radInSqrs, 0);
		int y1 = Math.min(mapLoc[1] + radInSqrs, m_map.length);
		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (IndexedOrientation2D point : (List<IndexedOrientation2D>) m_map[x][y]) {
					if (minRadiusSq <= point.distanceSquared(loc) && point.distanceSquared(loc) <= maxRadiusSq)
						inRange.add(point);
				}
		return inRange;
	}

	public IndexedOrientation2D lastPointInRange(Orientation2D loc, double minRadius, double maxRadius) {
		double minRadiusSq = minRadius * minRadius, maxRadiusSq = maxRadius * maxRadius;
		int radInSqrs = (int) (maxRadius / m_mapAccuracy) + 1;
		IndexedOrientation2D ret = null;
		int[] mapLoc = getLoc(loc);
		int x0 = Math.max(mapLoc[0] - radInSqrs, 0);
		int x1 = Math.min(mapLoc[0] + radInSqrs, m_map.length);
		int y0 = Math.max(mapLoc[1] - radInSqrs, 0);
		int y1 = Math.min(mapLoc[1] + radInSqrs, m_map.length);
		double dontCollectGC;
		int bestIndex = -1;
		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (IndexedOrientation2D point : (List<IndexedOrientation2D>) m_map[x][y]) {
					dontCollectGC = point.distanceSquared(loc);
					if (minRadiusSq <= dontCollectGC && dontCollectGC <= maxRadiusSq
							&& (ret == null || bestIndex < point.index))
						ret = point;
				}
		return ret;
	}

	/**
	 * finds the closest point to a given point (loc) uses the param radius for
	 * recursive search
	 * 
	 * @param loc
	 * @param radius
	 * @return
	 */
	private Orientation2D closestPoint(Orientation2D loc) {
		return closestPoint(loc, 1);
	}

	private Orientation2D closestPoint(Orientation2D loc, double radius) {
		if (radius > 2 * m_mapAccuracy * (m_map.length + m_map[0].length))
			return null;
		Orientation2D ret = findClosest(pointsInRange(loc, 0, 2 * radius), loc);
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

	public Orientation2D lastPointInRange(Orientation2D loc, double radius) {

		Orientation2D ret = lastPointInRange(loc, 0, radius);

		if (ret == null) {
			ret = closestPoint(loc, radius);
		}
		return ret;
	}

	// returns the last point in the path
	public Orientation2D getLast() {
		return m_path.getLast();
	}

	/**
	 * resets the map to empty
	 */
	public void clear() {
		int[] loc;
		for (IndexedOrientation2D point : m_path) {
			loc = getLoc(point);
			if (!m_map[loc[0]][loc[1]].isEmpty())
				m_map[loc[0]][loc[1]] = new LinkedList();
		}
		m_path = new LinkedList<IndexedOrientation2D>();
	}

}
