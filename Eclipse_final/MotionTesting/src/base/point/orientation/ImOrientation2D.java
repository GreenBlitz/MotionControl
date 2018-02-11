package base.point.orientation;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

import base.point.IPoint2D;

/**
 * Immutable Orientation2D. Each method will return a new instance leaving
 * {@code this} unchanged.
 * <p>
 * for detailed explanation on the Orientation2D concept, see
 * {@link IOrientation2D}
 * </p>
 * 
 * @see Orientation2D
 * @see IOrientation2D
 * @author karlo
 */
public class ImOrientation2D extends Orientation2D {

	public ImOrientation2D(double x, double y, double direction) {
		super(x, y, direction);
	}

	public ImOrientation2D(IPoint2D point, double direction) {
		super(point, direction);
	}

	@Override
	public IOrientation2D moveBy(double x, double y, double direction, DirectionEffect effect) {
		switch (effect) {
		case IGNORED:
			return new ImOrientation2D(m_x + x, m_y + y, m_direction);
		case RESERVED:
			return new ImOrientation2D(m_x + Math.sin(direction + m_direction) * m_x,
					m_y + Math.cos(direction + m_direction) * m_y, m_direction);
		case CHANGED:
			return new ImOrientation2D(m_x + Math.sin(m_direction) * m_x, m_y + Math.cos(m_direction) * m_y,
					m_direction + direction);
		default:
			throw new IllegalArgumentException(
					"Oh, the places you'll go!" + "wait, how did we got here? this souldn't be possible!");
		}
	}

	@Override
	public IOrientation2D rotate(double angle, DirectionEffect effect) {
		double sin, cos, x, y;

		switch (effect) {
		case IGNORED:
			sin = Math.sin(angle);
			cos = Math.cos(angle);

			x = cos * m_x - sin * m_y;
			y = sin * m_x + cos * m_y;

			return new ImOrientation2D(x, y, m_direction);
		case RESERVED:
			sin = Math.sin(angle + m_direction);
			cos = Math.cos(angle + m_direction);

			x = cos * m_x - sin * m_y;
			y = sin * m_x + cos * m_y;

			return new ImOrientation2D(x, y, m_direction);
		case CHANGED:
			sin = Math.sin(angle + m_direction);
			cos = Math.cos(angle + m_direction);

			x = cos * m_x - sin * m_y;
			y = sin * m_x + cos * m_y;

			return new ImOrientation2D(x, y, m_direction + angle);
		default:
			throw new IllegalArgumentException(
					"It's dangerous to go alone! take this!" + "Oh wait, this is an exception. try again next time!");
		}

	}

	@Override
	public IOrientation2D scale(double scale, DirectionEffect effect) {
		return new ImOrientation2D(m_x * scale, m_y * scale, effect.changed() ? m_direction * scale : m_direction);
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
		double x = vec.get(0);
		double y = vec.get(1);

		double direction = effect.changed() ? vec.get(2) : m_direction;

		return new ImOrientation2D(x, y, direction);
	}

	@Override
	public IOrientation2D setDirection(double angle) {
		return new ImOrientation2D(m_x, m_y, angle);
	}
}
