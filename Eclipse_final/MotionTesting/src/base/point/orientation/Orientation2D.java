package base.point.orientation;

import org.la4j.Matrix;

import base.point.IPoint2D;
import base.point.Point2D;

public abstract class Orientation2D extends Point2D implements IOrientation2D {
	protected double m_direction;

	public Orientation2D(double x, double y, double direction) {
		super(x, y);
	}

	public Orientation2D(IPoint2D point, double direction) {
		super(point);
		m_direction = direction;
	}

	@Override
	public double getDirection() {
		return m_direction;
	}

	@Override
	public IPoint2D moveBy(double x, double y) {
		return (Orientation2D) moveBy(x, y, 0, DirectionEffect.IGNORED);
	}

	@Override
	public IPoint2D rotate(double angle) {
		return (Orientation2D) rotate(angle, DirectionEffect.IGNORED);
	}

	public IPoint2D scale(double scale) {
		return (Orientation2D) multiply(scale);
	}

	@Override
	public IPoint2D _apply(Matrix transformation) {
		return (Orientation2D) apply(transformation, DirectionEffect.IGNORED);
	}
}
