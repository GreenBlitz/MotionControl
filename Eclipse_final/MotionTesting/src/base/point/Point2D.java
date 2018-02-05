package base.point;

import org.la4j.Matrix;

/**
 * General 2-dimensional point
 * 
 * @author karlo
 *
 */
public abstract class Point2D {

	/**
	 * Coordinate System
	 * <p>
	 * Members:<blockquote> {@link Point2D.CordSystem#CARTESIAN CARTESIAN}<br>
	 * {@link Point2D.CordSystem#POLAR POLAR} </blockquote>
	 * </p>
	 * 
	 * @author karlo
	 *
	 */
	public enum CordSystem {
		/**
		 * Cartesian coordinate system- x axis, y axis
		 */
		CARTESIAN,

		/**
		 * Polar coordinate system- radius, angle
		 */
		POLAR
	}

	/**
	 * used to create a point with no active functions and no way to affect it's
	 * internals- great to create a global origin! <i>don't touch pls</i>
	 * 
	 * @author karlo
	 *
	 */
	private static final class Point2DOrigin extends Point2D {
		private static String errorMsg = "can't move origin";

		public Point2DOrigin(double x, double y) {
			super(x, y);
		}

		@Override
		public Point2D moveBy(double x, double y) {
			throw new IllegalStateException(errorMsg);
		}

		@Override
		public Point2D moveBy(Point2D other) {
			throw new IllegalStateException(errorMsg);
		}

		@Override
		public Point2D rotate(double angle) {
			throw new IllegalStateException(errorMsg);
		}

		@Override
		public Point2D rotateAround(Point2D origin, double angle) {
			throw new IllegalStateException(errorMsg);
		}

		@Override
		public Point2D _apply(Matrix mat) {
			throw new IllegalStateException(errorMsg);
		}

		@Override
		public Point2D scale(double scale) {
			throw new IllegalStateException(errorMsg);
		}

		@Override
		protected Point2D relativeToPolar(Point2D origin) {
			throw new IllegalStateException(errorMsg);
		}
	}

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

	/**
	 * global origin of the Cartesian system - 0,0. This cannot be changed, and
	 * has it's own type- {@link Point2D.Point2DOrigin Point2DOrigin}
	 */
	public static final Point2D GLOBAL_ORIGIN = new Point2DOrigin(0, 0);

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
	public Point2D(Point2D other) {
		m_x = other.getX();
		m_y = other.getY();
	}

	/**
	 * 
	 * @param x
	 *            X axis shift
	 * @param y
	 *            Y axis shift
	 * @return move of this point by {@code x} and {@code y}
	 */
	public abstract Point2D moveBy(double x, double y);

	/**
	 * 
	 * @param shifts
	 *            as a {@link Point2D}, e.g: {@code other.getX()} corresponds to
	 *            shifts of {@code this.getX()}
	 * @return move of this point with the other's x and y values
	 */
	public abstract Point2D moveBy(Point2D other);

	/**
	 * Rotates this point around {@link Point2D.GLOBAL_ORIGIN (0,0)} with
	 * {@code angle}
	 * 
	 * @param angle
	 *            Rotation angle in
	 *            <b><a href="https://en.wikipedia.org/wiki/Radian">radians</a>
	 *            </b>
	 * @return this point, rotated by angle around {@link Point2D.GLOBAL_ORIGIN
	 *         (0,0)}
	 */
	public abstract Point2D rotate(double angle);

	/**
	 * Resizes this point by {@code scale}
	 * 
	 * @param scale
	 *            resize scale
	 * @return this point rescaled
	 */
	public abstract Point2D scale(double scale);

	/**
	 * applies {@code mat} on this point
	 * 
	 * @param mat
	 *            transformation matrix
	 * @return mat * this
	 */
	protected abstract Point2D _apply(Matrix mat);

	/**
	 * 
	 * @param origin
	 * @return the polar representation of this point relative to {@code origin}
	 */
	protected abstract Point2D relativeToPolar(Point2D origin);
	
	public double getX() {
		return m_x;
	}

	public double getY() {
		return m_y;
	}

	/**
	 * Rotates around {@code origin} with {@code angle}
	 * 
	 * @see Point2D#rotate(double)
	 * @param origin
	 *            rotation origin
	 * @param angle
	 *            rotation angle
	 * @return this point, rotated around another with {@code angle}
	 */
	public Point2D rotateAround(Point2D origin, double angle) {
		if (origin.equals(GLOBAL_ORIGIN))
			return rotate(angle);
		return moveByReversed(origin).rotate(angle).moveBy(origin);
	}

	/**
	 * Subtracting {@code x,y} from this point
	 * 
	 * @see Point2D#moveBy(double, double)
	 * @param x
	 *            X axis shift
	 * @param y
	 *            Y axis shift
	 * @return this point subtracted by {@code x,y}
	 */
	public Point2D moveByReversed(double x, double y) {
		return moveBy(-x, -y);
	}

	/**
	 * Subtracts other point from this
	 * 
	 * @see Point2D#moveBy(Point2D)
	 * @param other
	 * @return
	 */
	public Point2D moveByReversed(Point2D other) {
		return moveBy(-other.m_x, -other.m_y);
	}

	/**
	 * Returns the opposite of this point, e.g: negate(1, 2) = (-1, -2)
	 * 
	 * @return opposite of this point
	 */
	public Point2D negate() {
		return moveByReversed(2 * m_x, 2 * m_y);
	}

	/**
	 * 
	 * @param other
	 * @return distance <i>squared</i> between this to other
	 */
	public double distanceSquared(Point2D other) {
		return (other.m_x - m_x) * (other.m_x - m_x) + (other.m_y - m_y) * (other.m_y - m_y);
	}

	/**
	 * 
	 * @param other
	 * @return distance between this and other
	 */
	public double distance(Point2D other) {
		return Math.hypot(other.m_x - m_x, other.m_y - m_y);
	}

	/**
	 * Applies the transformation defined by {@code mat}
	 * 
	 * @param mat
	 * @return mat * this
	 * @throws IllegalArgumentException
	 *             if matrix doesn't have 2 columns
	 */
	public Point2D apply(Matrix mat) throws IllegalArgumentException {
		if (mat.columns() != 2)
			throw new IllegalArgumentException(
					"transformation matrix expected to have 2 columns, " + mat.columns() + " found");
		return _apply(mat);
	}

	public Point2D relativeCordsTo(Point2D origin, CordSystem sys) {
		if (sys == CordSystem.CARTESIAN)
			return relativeToCartesian(origin);
		else
			return relativeToPolar(origin);
	}
	
	protected Point2D relativeToCartesian(Point2D origin) {
		return moveByReversed(origin);
	}
}
