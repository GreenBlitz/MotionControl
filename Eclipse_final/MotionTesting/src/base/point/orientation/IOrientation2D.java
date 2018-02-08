package base.point.orientation;

import org.la4j.Matrix;

import base.Tuple;
import base.point.ILocation;
import base.point.IPoint2D;
import base.point.IPoint2D.CordSystem;

public interface IOrientation2D extends ILocation {

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
		RESERVED,

		/**
		 * The method should change the direction before doing anything else
		 */
		PRE_CHANGED,

		/**
		 * The method should change the direction after doing anything else
		 */
		POST_CHANGED,

		/**
		 * The method shouldn't affect nor use the direction- this option
		 * indicates that the behavior would be just as if it is a
		 * {@link IOrientation2D} method
		 */
		IGNORED
	}

	/**
	 * used to create a point with no active functions and no way to affect it's
	 * internals- great to create a global origin! <i>don't touch pls</i>
	 * 
	 * @author karlo
	 *
	 */
	static final class Orientation2DOrigin implements IOrientation2D {
		public static final IOrientation2D ORIGIN = new Orientation2DOrigin();

		private static final String errorMsg = "can't move origin";

		private Orientation2DOrigin() {
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
		public IOrientation2D multiply(double scale) {
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
	IOrientation2D multiply(double scale);

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

	// -------------------- implement all ---------------------

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
	default Tuple<Double, Double> relativeCordsTo(IOrientation2D origin, IPoint2D.CordSystem sys) {
		return sys == CordSystem.CARTESIAN ? Tuple.of(getX() - origin.getX(), getY() - origin.getY())
				: Tuple.of(distance(origin), Math.atan2(getY() - origin.getY(), getX() - origin.getX()));
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
	 * @see IPoint2D#rotateAround(IPoint2D, double)
	 * @param origin
	 *            origin of rotation
	 * @param angle
	 *            angle of rotation
	 * @param effect
	 *            effect of this call on the direction
	 * @return this point rotated as described above
	 */
	default IOrientation2D rotateAround(IOrientation2D origin, double angle, DirectionEffect effect) {
		DirectionEffect ignore = DirectionEffect.IGNORED;

		if (origin.equals(GLOBAL_ORIGIN) || angle == 0)
			return rotate(angle, effect);

		if (effect == DirectionEffect.PRE_CHANGED) {
			setDirection(angle + getDirection());
			return moveByReversed(origin, ignore).rotate(angle, ignore).moveBy(origin, ignore);
		}

		if (effect == DirectionEffect.PRE_CHANGED) {
			IOrientation2D ret = moveByReversed(origin, ignore).rotate(angle, ignore).moveBy(origin, ignore);
			setDirection(angle + getDirection());
			return ret;
		}

		return moveByReversed(origin, effect).rotate(angle, effect).moveBy(origin, effect);
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
		return moveBy(-x, -y, -direction, effect);
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
		return moveByReversed(multiply(2), effect);
	}
	
	default double normalize(double angle) {
		while (angle > 2 * Math.PI) angle -= Math.PI;
		while (angle < 0) angle += Math.PI;
		
		return angle;
	}
}
