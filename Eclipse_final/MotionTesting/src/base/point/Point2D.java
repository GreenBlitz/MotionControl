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
}
