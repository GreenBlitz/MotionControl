package base.point;

/**
 * This is a Point2D basic implementation.
 *
 * @see IPoint2D
 * @author karlo
 */
public abstract class Point2D implements IPoint2D {	

	/**
	 * @param x
	 *            X axis cords
	 * @param y
	 *            Y axis cords
	 * @return new {@link MPoint2D mutable Point2D} object
	 */
	public static Point2D mutable(double x, double y) {
		return new MPoint2D(x, y);
	}

	/**
	 * @param x
	 *            X axis cords	
	 * @param y
	 *            Y axis cords
	 * @return new {@link ImPoint2D immutable Point2D} object
	 */
	public static Point2D immutable(double x, double y) {
		return new ImPoint2D(x, y);
	}

	protected double m_x;
	protected double m_y;

	public Point2D(double x, double y) {
		m_x = x;
		m_y = y;
	}

	/**
	 * Copies the other point.
	 * 
	 * @param other
	 */
	public Point2D(IPoint2D other) {
		m_x = other.getX();
		m_y = other.getY();
	}

	@Override
	public double getX() {
		return m_x;
	}

	@Override
	public double getY() {
		return m_y;
	}

	@Override
	public String toString() {
		return "Point2D [m_x=" + m_x + ", m_y=" + m_y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(m_x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m_y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point2D other = (Point2D) obj;
		if (Double.doubleToLongBits(m_x) != Double.doubleToLongBits(other.m_x))
			return false;
		if (Double.doubleToLongBits(m_y) != Double.doubleToLongBits(other.m_y))
			return false;
		return true;
	}
}
