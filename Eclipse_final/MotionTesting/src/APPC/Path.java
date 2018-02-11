package APPC;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import base.point.orientation.IOrientation2D;

/**
 *
 * Represents a path of points
 */
public class Path implements Iterable<IOrientation2D> {
	protected List<IOrientation2D> m_path;

	/**
	 * 
	 * @param path
	 */
	public Path(List<IOrientation2D> path) {
		m_path = path;
	}

	public Path() {
		this(new ArrayList<>());
	}

	/**
	 * 
	 * @param origin
	 */
	public Path(IOrientation2D origin) {
		this();
		this.add(origin);
	}

	/**
	 * Attains the Location2D at the given tick
	 * 
	 * @param tick
	 *            The index of the point
	 * @return The location at the given tick
	 * @throws EndOfPathException
	 *             if the path doesn't have enough locations in it
	 */
	public IOrientation2D get(int tick) throws EndOfPathException {
		if (tick >= m_path.size())
			throw new EndOfPathException(tick);
		return m_path.get(tick);
	}

	/**
	 * 
	 * @return return an iterator over the path
	 */
	public PathIterator get() {
		return new PathIterator();
	}

	/**
	 * 
	 * @return the last point on the path
	 */
	public IOrientation2D getLast() {
		return m_path.get(m_path.size() - 1);
	}

	@Override
	public PathIterator iterator() {
		return new PathIterator();
	}

	/**
	 * Adds a location at the end of the path
	 * 
	 * @param l
	 *            The location to be added
	 */
	public void add(IOrientation2D l) {
		m_path.add(l);
	}

	/**
	 * 
	 * @param point2D
	 *            searching point
	 * @return Closest point on the path to the searching point
	 */
	public IOrientation2D closestPointTo(IOrientation2D point2D) {
		return m_path.stream().min(Comparator.comparingDouble(value -> value.distance(point2D))).get();
	}

	/**
	 * 
	 * @return number of point on the path
	 */
	public int getTotalLength() {
		return m_path.size();
	}

	@Override
	public String toString() {
		return "Path [m_path=" + m_path + "]";
	}

	public class PathIterator implements Iterator<IOrientation2D>, Iterable<IOrientation2D> {
		private int currentIndex = 0;

		public PathIterator(int currentIndex) {
			this.currentIndex = currentIndex;
		}

		public PathIterator() {
		}

		public int getCurrentIndex() {
			return currentIndex;
		}

		public void setCurrentIndex(int index) {
			currentIndex = index;
		}

		public void changeCurrentIndex(int diff) {
			currentIndex += diff;
		}

		public int getLength() {
			return Path.this.m_path.size();
		}

		public IOrientation2D getLast() {
			return Path.this.getLast();
		}

		public boolean hasNext() {
			return currentIndex < m_path.size();
		}

		/**
		 * returns the next point without increasing index, calling
		 * <code>peek()</code> twice will return the same thing
		 * 
		 * @return the next point
		 */
		public IOrientation2D peek() {
			return m_path.get(currentIndex);
		}

		public IOrientation2D next() {
			return m_path.get(currentIndex++);
		}

		public PathIterator iterator() {
			return new PathIterator(currentIndex);
		}

		public PathIterator resetIterator() {
			currentIndex = 0;
			return this;
		}

		/**
		 * Attains the next point after the last call to recieve
		 * 
		 * @return The next location in the path
		 * @throws EndOfPathException
		 *             If the path has ended
		 */
		public synchronized IOrientation2D get() throws EndOfPathException {
			return Path.this.get(currentIndex++);
		}

		/**
		 * 
		 * @return get the percentage of the path that was completed
		 */
		public double getCompletion() {
			return getRemainingLength() / getTotalLength();
		}

		/**
		 * 
		 * @return The amount of points passed
		 */
		public int getRemainingLength() {
			return m_path.size() - currentIndex;
		}

	}
}
