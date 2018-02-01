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

	private int[] getLoc(IndexedOrientation2D point) {
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

	public IndexedOrientation2D pointInRange(Orientation2D loc, double radius) {
		int radInSqrs = (int) (radius / m_mapAccuracy) + 1;
		IndexedOrientation2D close = new IndexedOrientation2D(new Orientation2D(0, 0, Double.NaN), -1);
		for (int x = -radInSqrs; x < radInSqrs; x++)
			for (int y = -radInSqrs; y < radInSqrs; y++)
				for (Object notPointYet : m_map[x][y]) {
					IndexedOrientation2D point = (IndexedOrientation2D) notPointYet;
					if (point.distance(loc) <= radius && point.index > close.index)
						close = point;
		}
		
		if(close.equals(new IndexedOrientation2D(new Orientation2D(0, 0, Double.NaN), -1))) return close;
		for(IndexedOrientation2D point:m_path)
			if(close.getIndex() == -1 || point.distanceSquared(loc)< close.distanceSquared(loc))
				close = point;
		return close;
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
