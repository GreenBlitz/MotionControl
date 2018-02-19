package gbmotion.base.point;

import org.la4j.Matrix;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import gbmotion.base.point.orientation.IOrientation2D;
import gbmotion.util.Tuple;

/**
 * So, here's the point... (HAHAHAHA GREAT PUN LOLLL XDDDDD)
 * <p>
 * Come on, this is dead simple.
 * <p />
 * <b>THIS IS A 2-DIMENSIONAL POINT. IT HAS 2 COORDINATES- NAMELY X AND Y. GOT
 * IT? GOOD. KEEP WORKING!</b>
 * </p>
 * 
 * @author karlo
 */
public interface IPoint2D {
	/**
	 * Coordinate System
	 * <p>
	 * Members:<blockquote> {@link IPoint2D.CordSystem#CARTESIAN CARTESIAN}<br>
	 * {@link IPoint2D.CordSystem#POLAR POLAR} </blockquote>
	 * </p>
	 * 
	 * @author karlo
	 *
	 */
	enum CordSystem {
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
	static class Point2DOrigin implements IPoint2D {
		public static final IPoint2D ORIGIN = new Point2DOrigin();

		private static final String errorMsg = "can't move origin";

		protected Point2DOrigin() {
		}

		@Override
		public double getX() {
			return 0;
		}

		@Override
		public double getY() {
			return 0;
		}

		@Override
		public IPoint2D moveBy(double x, double y) {
			throw new UnsupportedOperationException(errorMsg);
		}

		@Override
		public IPoint2D rotate(double angle, boolean clockwise) {
			throw new UnsupportedOperationException(errorMsg);
		}

		@Override
		public IPoint2D scale(double scale) {
			throw new UnsupportedOperationException(errorMsg);
		}

		@Override
		public IPoint2D _apply(Matrix transformation) {
			throw new UnsupportedOperationException(errorMsg);
		}
	}

	/**
	 * global origin of the Cartesian system - 0,0. This cannot be changed, and
	 * has it's own type- {@link IPoint2D.Point2DOrigin Point2DOrigin}
	 */
	static final IPoint2D GLOBAL_ORIGIN = Point2DOrigin.ORIGIN;

	double getX();

	double getY();

	/**
	 * 
	 * @param x
	 *            X axis shift
	 * @param y
	 *            Y axis shift
	 * @return move of this point by {@code x} and {@code y}
	 */
	IPoint2D moveBy(double x, double y);

	/**
	 * Rotates this point around {@link IPoint2D.GLOBAL_ORIGIN (0,0)} with
	 * {@code angle}
	 * 
	 * @param angle
	 *            Rotation angle in
	 *            <b><a href="https://en.wikipedia.org/wiki/Radian">radians</a>
	 *            </b>
	 * @return this point, rotated by angle around {@link IPoint2D.GLOBAL_ORIGIN
	 *         (0,0)}
	 */
	IPoint2D rotate(double angle, boolean clockwise);

	/**
	 * Resizes this point by {@code scale}
	 * 
	 * @param scale
	 *            resize scale
	 * @return this point rescaled
	 */
	IPoint2D scale(double scale);

	/**
	 * applies {@code transformation} on this point
	 * 
	 * @param transformation
	 *            transformation matrix
	 * @return transformation * this
	 */
	IPoint2D _apply(Matrix transformation);

	/**
	 * 
	 * @param origin
	 *            coordinate system origin
	 * @param sys
	 *            type of coordinate system
	 * @return
	 */
	default Tuple<Double, Double> relativeCordsTo(IPoint2D origin, CordSystem sys) {
		switch(sys) {
		case CARTESIAN:
			return Tuple.of(getX() - origin.getX(), getY() - origin.getY());
		case POLAR:
			return Tuple.of(distance(origin), Math.atan2(getY() - origin.getY(), getX() - origin.getX()));
		default:
			throw new IllegalArgumentException(
					"Toto, I've a feeling we're not in Kansas anymore.");
		}
	}

	/**
	 * Rotates around {@code origin} with {@code angle}
	 * 
	 * @see IPoint2D#rotate(double)
	 * @param origin
	 *            rotation origin
	 * @param angle
	 *            rotation angle
	 * @return this point, rotated around another with {@code angle}
	 */
	default IPoint2D rotateAround(IPoint2D origin, double angle) {
		if (origin.equals(GLOBAL_ORIGIN))
			return rotate(-angle);
		
		if (angle == 0)
			return this;
			
		return moveByReversed(origin).rotate(-angle).moveBy(origin);
	}
	
	default IPoint2D rotate(double angle) {
		return rotate(angle, false);
	}

	/**
	 * Subtracting {@code x,y} from this point
	 * 
	 * @see IPoint2D#moveBy(double, double)
	 * @param x
	 *            X axis shift
	 * @param y
	 *            Y axis shift
	 * @return this point subtracted by {@code x,y}
	 */
	default IPoint2D moveByReversed(double x, double y) {
		return moveBy(-x, -y);
	}

	/**
	 * Subtracts other point from this
	 * 
	 * @see IPoint2D#moveBy(IPoint2D)
	 * @param other
	 * @return
	 */
	default IPoint2D moveByReversed(IPoint2D other) {
		return moveBy(-other.getX(), -other.getY());
	}

	/**
	 * Returns the opposite of this point, e.g: negate(1, 2) = (-1, -2)
	 * 
	 * @return opposite of this point
	 */
	default IPoint2D negate() {
		return moveByReversed(2 * getX(), 2 * getY());
	}

	/**
	 * Applies the transformation defined by {@code mat}
	 * 
	 * @param mat
	 * @return mat * this
	 * @throws IllegalArgumentException
	 *             if matrix doesn't have 2 columns
	 */
	default IPoint2D apply(Matrix transformation) {
		if (transformation.columns() != 2)
			throw new IllegalArgumentException(
					"transformation matrix expected to have 2 columns, " + transformation.columns() + " found");
		return _apply(transformation);
	}

	/**
	 * 
	 * @param shifts
	 *            as a {@link IPoint2D}, e.g: {@code other.getX()} corresponds
	 *            to shifts of {@code this.getX()}
	 * @return move of this point with the other's x and y values
	 */
	default IPoint2D moveBy(IPoint2D other) {
		return moveBy(other.getX(), other.getY());
	}

	/**
	 * 
	 * @param other
	 * @return distance between this and other
	 */
	default double distance(IPoint2D other) {
		return Math.hypot(getX() - other.getX(), getY() - other.getY());
	}

	/**
	 * 
	 * @param other
	 * @return distance <i>squared</i> between this to other
	 */
	default double distanceSquared(IPoint2D other) {
		return (getX() - other.getX()) * (getX() - other.getX()) + (getY() - other.getY()) * (getY() - other.getY());
	}
	
	default IPoint2D changePrespectiveTo(IOrientation2D origin, boolean clockwise) {
		return moveByReversed(origin).rotate(-origin.getDirection(), clockwise);
	}
	
	default IPoint2D changePrespectiveTo(IOrientation2D origin) {
		return moveByReversed(origin).rotate(-origin.getDirection(), false);
	}
	
	default double length() {
		return Math.sqrt(getX() * getX() + getY() *getY());
	}
	
	default double lengthSquared() {
		return getX() * getX() + getY() * getY();
	}
	
	default void toDashboard(String s) {
		SmartDashboard.putNumber(s + " x coordinate", getX());
		SmartDashboard.putNumber(s + " y coordinate", getY());
	}
}
