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

	@Override
	public IOrientation2D moveBy(double x, double y, double direction, DirectionEffect effect) {
		switch (effect) {
		case IGNORED:
			m_x += x;
			m_y += y;
			break;
		case RESERVED:
			m_x += Math.sin(direction + m_direction) * m_x;
			m_y += Math.cos(direction + m_direction) * m_y;
			break;
		case CHANGED:
			m_direction += direction;
			m_x += Math.sin(m_direction) * m_x;
			m_y += Math.cos(m_direction) * m_y;
			break;
		default:
			throw new IllegalArgumentException("for the night is dark and full of terrors. Just like here.");
		}

		return this;
	}

	@Override
	public IOrientation2D rotate(double angle, DirectionEffect effect) {
		double sin, cos;
		double x = m_x, y = m_y;

		switch (effect) {
		case IGNORED:
			sin = Math.sin(angle);
			cos = Math.cos(angle);

			m_x = cos * x - sin * y;
			m_y = sin * x + cos * y;

			return this;
		case RESERVED:
			sin = Math.sin(angle + m_direction);
			cos = Math.cos(angle + m_direction);

			m_x = cos * x - sin * y;
			m_y = sin * x + cos * y;

			return this;
		case CHANGED:
			m_direction = (m_direction + angle) % TAU;
			return rotate(angle, DirectionEffect.RESERVED);

		default:
			throw new IllegalArgumentException(
					"'madness?'" + "'THIS IS SPARTA!' he said as he throw the code of the runtime environment");
		}
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
		m_direction = angle;
		return this;
	}
}
