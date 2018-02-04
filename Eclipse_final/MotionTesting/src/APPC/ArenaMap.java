package APPC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

@SuppressWarnings("all")
public class ArenaMap {

	private LinkedList[][] m_map;
	private LinkedList<IndexedOrientation2D> m_path = new LinkedList<IndexedOrientation2D>();
	private final double m_mapAccuracy;

	public ArenaMap(double acc, double arenaLen, double arenaWdth) {
		m_mapAccuracy = acc;
		m_map = new LinkedList[(int) (arenaLen / acc)][(int) (arenaWdth / acc)];
	}

	private int[] getLoc(Orientation2D point) {
		return new int[] { (int) (point.getX() * m_mapAccuracy), (int) (point.getY() * m_mapAccuracy) };
	}

	private void insert(IndexedOrientation2D point) {
		int[] loc = getLoc(point);
		if (m_map[loc[0]][loc[1]] == null)
			m_map[loc[0]][loc[1]] = new LinkedList<IndexedOrientation2D>();
		m_map[loc[0]][loc[1]].add(point);
		m_path.add(point);

	}

	public void construct(Iterable<Orientation2D> path) {
		clear();
		int ind = 0;
		for (Orientation2D point : path) {
			insert(new IndexedOrientation2D(point, ind));
			ind++;
		}
	}
	
	private IndexedOrientation2D findClosest(LinkedList<IndexedOrientation2D> list, Orientation2D loc){
		if(list.isEmpty()) return null;
		IndexedOrientation2D close = list.getFirst();
		for(IndexedOrientation2D point:list)
			if(point.distanceSquared(loc) < close.distanceSquared(loc))
				close = point;
		return close;
	}
	private LinkedList<IndexedOrientation2D> pointsInRange(Orientation2D loc, double minRadius, double maxRadius) {
		int radInSqrs = (int) (maxRadius / m_mapAccuracy) + 1;
		LinkedList<IndexedOrientation2D> inRange = new LinkedList<IndexedOrientation2D>();
		int[] mapLoc = getLoc(loc);
		for (int x = mapLoc[0] - radInSqrs; x < mapLoc[0] + radInSqrs; x++)
			for (int y = mapLoc[1] - radInSqrs; y < mapLoc[1] + radInSqrs; y++)
				if (x<0 || y<0)
					for (Object notPointYet : m_map[x][y]) {
						IndexedOrientation2D point = (IndexedOrientation2D) notPointYet;
						if (minRadius <= point.distance(loc) && point.distance(loc) <= maxRadius)
							inRange.add(point);
		}
		return inRange;
	}
	private IndexedOrientation2D closestPoint(Orientation2D loc, double radius){
		IndexedOrientation2D ret = findClosest(pointsInRange(loc, radius, 2*radius), loc);
		if(ret != null) return ret;
		return closestPoint(loc, 2*radius);
	}
	
	public IndexedOrientation2D pointInRange(Orientation2D loc, double radius) {
		IndexedOrientation2D close = new IndexedOrientation2D(new Orientation2D(0, 0, Double.NaN), -1);
		for(IndexedOrientation2D point:pointsInRange(loc, 0, (int) (radius / m_mapAccuracy) + 1)){
			if(point.distanceSquared(loc) < close.distanceSquared(loc)) close = point;
		}
		
		if(close.equals(new IndexedOrientation2D(new Orientation2D(0, 0, Double.NaN), -1))) return close;
		return closestPoint(loc, radius);
	}

	public Orientation2D getLast() {
		return m_path.getLast();
	}

	public void clear() {
		int[] loc;
		for (IndexedOrientation2D point : m_path) {
			loc = getLoc(point);
			m_map[loc[0]][loc[1]] = null;
		}
		m_path = new LinkedList<IndexedOrientation2D>();
	}
}
