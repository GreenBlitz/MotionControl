package base.point.orientation;

import java.text.DecimalFormat;

import org.la4j.Matrix;

import base.point.IPoint2D;
import base.point.Point2D;

/**
 * This is a Orientation2D basic implementation.
 * 
 * @see IOrientation2D
 * @author karlo
 */
public abstract class Orientation2D extends Point2D implements IOrientation2D {
	protected double m_direction;

	public Orientation2D(double x, double y, double direction) {
		super(x, y);
		m_direction = direction;
	}

	public Orientation2D(IPoint2D point, double direction) {
		super(point);
		m_direction = direction;
	}

	public Orientation2D(IOrientation2D point) {
		this(point.getX(), point.getY(), point.getDirection());
	}

	/**
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 * @param direction
	 *            direction value
	 * @return mutable Orientation2D object
	 * @see MOrientation2D
	 */
	public static final Orientation2D mutable(double x, double y, double direction) {
		return new MOrientation2D(x, y, direction);
	}

	/**
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 * @param direction
	 *            direction value
	 * @return immutable Orientation2D object
	 * @see ImOrientation2D
	 */
	public static final Orientation2D immutable(double x, double y, double direction) {
		return new ImOrientation2D(x, y, direction);
	}

	@Override
	public double getDirection() {
		return m_direction;
	}

	@Override
	public IPoint2D moveBy(double x, double y) {
		return moveBy(x, y, 0, DirectionEffect.IGNORED);
	}

	@Override
	public IPoint2D rotate(double angle) {
		return rotate(angle, DirectionEffect.IGNORED);
	}

	@Override
	public IPoint2D scale(double scale) {
		return scale(scale, DirectionEffect.IGNORED);
	}

	@Override
	public IPoint2D _apply(Matrix transformation) {
		return apply(transformation, DirectionEffect.IGNORED);
	}
	
	@Override
	public String toString() {
		DecimalFormat dec = new DecimalFormat("#000.0000");
		return "Orientation2D [x= " + dec.format(m_x) + ", y= " + dec.format(m_y) + ", direction = " + dec.format(Math.toDegrees(m_direction)) + "]";
	}
}
