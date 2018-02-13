package base.point.orientation;

import org.la4j.Matrix;

import base.Tuple;
import base.point.IPoint2D;

/**
 * So, we meet again...
 * <p>
 * This is the most general definition of an Orientation2D object. the concept
 * is very simple: two coordinates with a direction.
 * <p>
 * <b>notes:</b><blockquote>1) the direction should be in
 * <a href = "https://en.wikipedia.org/wiki/Radian">radians</a></blockquote>
 * <blockquote>2) every angle and direction is counter-clock wise</blockquote>
 * <blockquote>3) positive x value is to the right of the point.</blockquote>
 * <blockquote>4) positive y value is forward of the point.</blockquote>
 * <p>
 * 
 * @author karlo
 */
public interface IOrientation2D extends IPoint2D {

	/**
	 * Indicates if a method should preserve the direction after being called or
	 * not
	 * 
	 * @author karlo
	 *
	 */
	public enum DirectionEffect {
		/**
		 * The direction should not be affected by the method
		 */
		RESERVED(false),

		/**
		 * The method changes the direction before doing anything else
		 */
		CHANGED(true),

		/**
		 * The method shouldn't affect nor use the direction- this option
		 * indicates that the behavior would be just as if it is a
		 * {@link IPoint2D} method
		 */
		IGNORED(false);

		private boolean m_change;

		private DirectionEffect(boolean change) {
			m_change = change;
		}

		public boolean changed() {
			return m_change;
		}
	}

	/**
	 * used to create a point with no active functions and no way to affect it's
	 * internals- great to create a global origin! <i>don't touch pls</i>
	 * 
	 * @author karlo
	 *
	 */
	static final class Orientation2DOrigin extends Point2DOrigin implements IOrientation2D {
		public static final IOrientation2D ORIGIN = new Orientation2DOrigin();

		private static final String errorMsg = "Can't move origin";

		protected Orientation2DOrigin() {
		}

		@Override
		public double getDirection() {
			return 0;
		}

		@Override
		public IOrientation2D moveBy(double x, double y, double direction, DirectionEffect effect) {
			throw new UnsupportedOperationException(errorMsg);
		}

		@Override
		public IOrientation2D rotate(double angle, DirectionEffect effect) {
			throw new UnsupportedOperationException(errorMsg);
		}

		@Override
		public IOrientation2D scale(double scale, DirectionEffect effect) {
			throw new UnsupportedOperationException(errorMsg);
		}

		@Override
		public IOrientation2D apply(Matrix transformation, DirectionEffect effect) {
			throw new UnsupportedOperationException(errorMsg);
		}

		@Override
		public IOrientation2D setDirection(double angle) {
			throw new UnsupportedOperationException(errorMsg);
		}
	}

	static final IOrientation2D GLOBAL_ORIGIN = Orientation2DOrigin.ORIGIN;
	static final double TAU = 2 * Math.PI;

	double getX();

	double getY();

	double getDirection();

	/**
	 * Move this point using given coordinates as coordinate system origin
	 * 
	 * @see IPoint2D#moveBy(double, double)
	 * @param x
	 *            X axis shift
	 * @param y
	 *            Y axis shift
	 * @param direction
	 *            direction shift
	 * @param effect
	 *            effect of calling this on direction
	 * @return this point moved as described
	 */
	IOrientation2D moveBy(double x, double y, double direction, DirectionEffect effect);

	/**
	 * Rotates this point around the global origin
	 * 
	 * @see IPoint2D#rotate(double)
	 * @param angle
	 *            angle of rotation
	 * @param effect
	 *            effect of calling this on direction
	 * @return this point rotated as described
	 */
	IOrientation2D rotate(double angle, DirectionEffect effect);

	/**
	 * Resizes this point as a vector
	 * <p>
	 * Direction won't be affected by this
	 * </p>
	 * 
	 * @see IPoint2D#scale(double)
	 * @param scale
	 * @return
	 */
	IOrientation2D scale(double scale, DirectionEffect effect);

	/**
	 * applies {@code transformation} on this point
	 * 
	 * @see IPoint2D#apply(Matrix)
	 * @param transfomration
	 *            transformation matrix
	 * @param effect
	 *            effect of calling this on direction
	 * 
	 * @return transformation * this
	 */
	IOrientation2D apply(Matrix transformation, DirectionEffect effect);

	/**
	 * Sets the direction of this Orientation
	 *
	 * @param angle
	 *            new direction
	 * @return a orientation object with new direction
	 */
	IOrientation2D setDirection(double angle);
	
	default IOrientation2D changePrespectiveTo(IOrientation2D origin) {
		return moveByReversed(origin, DirectionEffect.IGNORED).rotate(-origin.getDirection(), DirectionEffect.CHANGED);
	}

	/**
	 * Finds the relative coordinates of this Orientation with another one
	 * 
	 * @see IPoint2D#relativeCordsTo(IPoint2D, CordSystem)
	 * @param origin
	 *            origin of coordinate system
	 * @param sys
	 *            coordinate system type
	 * @return the relative coordinates of this orientation to given origin
	 */
	default Tuple<Double, Double> relativeCordsTo(IOrientation2D origin, IPoint2D.CordSystem sys, boolean direction) {
		switch (sys) {
		case CARTESIAN:
			return Tuple.of(getX() - origin.getX(), getY() - origin.getY());
		case POLAR:
			return Tuple.of(distance(origin),
					Math.atan2(getY() - origin.getY(), getX() - origin.getX()) + (direction ? getDirection() : 0));
		default:
			throw new IllegalArgumentException(
					"so here we are again, it's always such a pleasure... what did you even do to get to here?");
		}
	}

	/**
	 * Just like
	 * {@link IOrientation2D#moveBy(double, double, double, DirectionEffect)}
	 * only with an Orientation object as it's shifts
	 * 
	 * @see IOrientation2D#moveBy(double, double, double, DirectionEffect)
	 * @param other
	 *            collection of shifts
	 * @param effect
	 *            effect of this call on direction
	 * @return {@link IOrientation2D#moveBy(double, double, double, DirectionEffect)}
	 *         .
	 */
	default IOrientation2D moveBy(IOrientation2D other, DirectionEffect effect) {
		return moveBy(other.getX(), other.getY(), other.getDirection(), effect);
	}

	/**
	 * Rotates this Orientation around origin
	 * 
	 * @see IPoint2D#round(IPoint2D, double)
	 * @param origin
	 *            origin of rotation
	 * @param angle
	 *            angle of rotation
	 * @param effect
	 *            effect of this call on the direction
	 * @return this point rotated as described above
	 */
	default IOrientation2D rotateAround(IOrientation2D origin, double angle, DirectionEffect effect) {
		if (origin.equals(GLOBAL_ORIGIN))
			return rotate(-angle, effect);

		if (angle == 0)
			return this;

		switch (effect) {
		case CHANGED:
			return moveByReversed(origin, DirectionEffect.IGNORED).rotate(-angle, effect)
					.moveBy(origin, DirectionEffect.IGNORED).setDirection(-angle + getDirection());
		case RESERVED:
		case IGNORED:
			return moveByReversed(origin, DirectionEffect.IGNORED).rotate(-angle, effect).moveBy(origin,
					DirectionEffect.IGNORED);
		default:
			throw new IllegalArgumentException("'There's a starrrrrmaaaaaaaaaaaan, waiting in the sky!'. "
					+ "what a shame- we can't even run properly, and you are talking about flying???");
		}
	}

	/**
	 * @see IPoint2D#moveByReversed(double, double)
	 * @param x
	 *            X axis shift (reversed)
	 * @param y
	 *            Y axis shift (reversed)
	 * @param direction
	 *            direction shift (reversed)
	 * @param effect
	 *            effect of calling this on direction
	 * 
	 * @return this point, subtracted by given coordinates
	 */
	default IOrientation2D moveByReversed(double x, double y, double direction, DirectionEffect effect) {
		return moveBy(-x, -y, effect.changed() ? -direction : direction, effect);
	}

	/**
	 * @see IOrientation2D#moveBy(double, double, double, DirectionEffect)
	 * @param other
	 *            shifts as an orientation2D
	 * @param effect
	 *            effect of calling this on direction
	 * 
	 * @return this point, subtracted by given coordinates
	 */
	default IOrientation2D moveByReversed(IOrientation2D other, DirectionEffect effect) {
		return moveByReversed(other.getX(), other.getY(), other.getDirection(), effect);
	}

	/**
	 * Calculates the inverse of this orientation
	 * 
	 * @param effect
	 *            effect of calling this on direction
	 * 
	 * @return the inverse of this orientation object
	 */
	default IOrientation2D negate(DirectionEffect effect) {
		return moveByReversed(scale(2, DirectionEffect.IGNORED), effect);
	}

	/**
	 * Moves the point with direction = 0 and ignored
	 * 
	 * @see IOrientation2D#moveBy(double, double, double, DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D moveBy(double x, double y) {
		return moveBy(x, y, 0, DirectionEffect.IGNORED);
	}

	/**
	 * Rotates with direction ignored
	 * 
	 * @see IOrientation2D#rotate(double, DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D rotate(double angle) {
		return rotate(angle, DirectionEffect.IGNORED);
	}

	/**
	 * @see IOrientation2D#apply(Matrix, DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D _apply(Matrix transformation) {
		return apply(transformation, DirectionEffect.IGNORED);
	}

	/**
	 * @see IOrientation2D#apply(Matrix, DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D apply(Matrix transformation) {
		return apply(transformation, DirectionEffect.IGNORED);
	}

	/**
	 * @see IOrientation2D#relativeCordsTo(IOrientation2D,
	 *      base.point.IPoint2D.CordSystem)
	 */
	@Override
	default Tuple<Double, Double> relativeCordsTo(IPoint2D origin, CordSystem sys) {
		switch (sys) {
		case CARTESIAN:
			return Tuple.of(getX() - origin.getX(), getY() - origin.getY());
		case POLAR:
			return Tuple.of(distance(origin), Math.atan2(getY() - origin.getY(), getX() - origin.getX()));
		default:
			throw new IllegalArgumentException(
					"so here we are again, it's always such a pleasure... what did you even do to get to here?");
		}
	}

	/**
	 * @see IOrientation2D#rotateAround(IOrientation2D, double, DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D rotateAround(IPoint2D origin, double angle) {
		DirectionEffect effect = DirectionEffect.IGNORED;
		return moveByReversed(origin.getX(), origin.getY(), 0, effect).rotate(angle, effect).moveBy(origin.getX(),
				origin.getY(), 0, effect);
	}

	/**
	 * @see IOrientation2D#moveByReversed(double, double, double,
	 *      DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D moveByReversed(double x, double y) {
		return moveBy(x, y, 0, DirectionEffect.IGNORED);
	}

	/**
	 * @see IOrientation2D#moveBy(double, double, double, DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D moveByReversed(IPoint2D other) {
		return moveByReversed(other.getX(), other.getY(), 0, DirectionEffect.IGNORED);
	}

	/**
	 * @see IOrientation2D#negate(DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D negate() {
		return negate(DirectionEffect.IGNORED);
	}

	/**
	 * @see IOrientation2D#moveBy(double, double, double, DirectionEffect)
	 */
	@Deprecated
	@Override
	default IPoint2D moveBy(IPoint2D other) {
		return moveBy(other.getX(), other.getY(), 0, DirectionEffect.IGNORED);
	}
}
