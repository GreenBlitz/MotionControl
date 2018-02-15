package base.point.orientation;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

import base.point.IPoint2D;

/**
 * Mutable Orientation2D. Each method will affect {@code this} and than return
 * it.
 * <p>
 * for detailed explanation, see {@link IOrientation2D}
 * </p>
 * 
 * @see Orientation2D
 * @see IOrientation2D
 * @author karlo
 */
public class MOrientation2D extends Orientation2D {

	public MOrientation2D(double x, double y, double direction) {
		super(x, y, direction);
	}

	public MOrientation2D(IPoint2D point, double direction) {
		super(point, direction);
	}
	
	public MOrientation2D(IOrientation2D orientation) {
		super(orientation);
	}

	@Override
	public IOrientation2D moveBy(double x, double y, double direction, DirectionEffect effect) {
		double cos, sin;

		switch (effect) {
		case IGNORED:
			m_x += x;
			m_y += y;
			break;
		case CHANGED:
			m_direction = normalizeAngle(m_direction + direction);
		case RESERVED:
			cos = Math.cos(direction);
			sin = Math.sin(direction);

			m_x += x * cos - y * sin;
			m_y += x * sin + y * cos;
			break;

		default:
			throw new IllegalArgumentException("for the night is dark and full of terrors. Just like here.");
		}

		return this;
	}

	@Override
	public IOrientation2D rotate(double angle, boolean clockWise, DirectionEffect effect) {
		double sin = Math.sin(angle), cos = Math.cos(angle);
		double x = m_x, y = m_y;

		if (clockWise) {
			m_x = cos * x + sin * y;
			m_y = sin * x - cos * y;
		} else {
			m_x = cos * x - sin * y;
			m_y = sin * x + cos * y;
		}

		m_direction = effect.changed() ? normalizeAngle(m_direction + angle) : m_direction;

		return this;
	}

	@Override
	public IOrientation2D scale(double scale, DirectionEffect effect) {
		m_x *= scale;
		m_y *= scale;

		if (effect.changed())
			m_direction *= scale;

		return this;
	}

	@Override
	public IOrientation2D apply(Matrix transformation, DirectionEffect effect) {
		if (transformation.columns() != 2 && effect != DirectionEffect.IGNORED)
			throw new IllegalArgumentException(
					"transformation matrix expected to have 2 columns, " + transformation.columns() + " found");

		if (transformation.columns() != 3 && effect == DirectionEffect.IGNORED)
			throw new IllegalArgumentException(
					"transformation matrix expected to have 3 columns, " + transformation.columns() + " found");

		Vector vec = transformation.multiply(new BasicVector(new double[] { m_x, m_y, m_direction }));
		m_x = vec.get(0);
		m_y = vec.get(1);

		if (effect.changed())
			m_direction = vec.get(2);

		return this;
	}

	@Override
	public IOrientation2D setDirection(double angle) {
		m_direction = normalizeAngle(angle);
		return this;
	}

	@Override
	public IPoint2D moveBy(double x, double y) {
		IOrientation2D rotated = (IOrientation2D) Orientation2D.immutable(this).rotate(-getDirection(), DirectionEffect.IGNORED);
		rotated = (IOrientation2D) Orientation2D.immutable(rotated.getX() + x, rotated.getY() + y, rotated.getDirection())
				.rotate(getDirection(), DirectionEffect.IGNORED);
		m_x = rotated.getX();
		m_y = rotated.getY();
		m_direction = rotated.getDirection();
		return this;
	}

	@Override
	public IOrientation2D set(double x, double y, double direction) {
		m_x = x;
		m_y = y;
		m_direction = normalizeAngle(direction);
		return this;
	}

	@Override
	public String toString() {
		return "Mutable " + super.toString();
	}
}
