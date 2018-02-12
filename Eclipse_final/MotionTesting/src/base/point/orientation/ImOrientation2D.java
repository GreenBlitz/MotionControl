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

	public ImOrientation2D(IOrientation2D point) {
		super(point);
	}

	@Override
	public IOrientation2D moveBy(double x, double y, double direction, DirectionEffect effect) {
		double cos, sin, dir;

		switch (effect) {
		case IGNORED:
			return new ImOrientation2D(m_x + x, m_y + y, m_direction);
		case CHANGED:
			dir = m_direction + direction;
			break;
		case RESERVED:
			dir = m_direction;
			break;
		default:
			throw new IllegalArgumentException(
					"Oh, the places you'll go! wait, how did we got here? this souldn't be possible!");
		}

		cos = Math.cos(m_direction + direction);
		sin = Math.sin(m_direction + direction);

		return new ImOrientation2D(m_x + x * cos - y * sin, m_y + x * sin + y * cos, dir);

	}

	@Override
	public IOrientation2D rotate(double angle, DirectionEffect effect) {
		double sin, cos, x, y, dir;

		switch (effect) {
		case IGNORED:
			sin = Math.sin(angle);
			cos = Math.cos(angle);

			x = cos * m_x - sin * m_y;
			y = sin * m_x + cos * m_y;

			return new ImOrientation2D(x, y, m_direction);
		case RESERVED:
			dir = m_direction;

			sin = Math.sin(dir + angle);
			cos = Math.cos(dir + angle);
			break;
		case CHANGED:
			dir = (m_direction + angle) % TAU;

			sin = Math.sin(dir + angle);
			cos = Math.cos(dir + angle);
			break;
		default:
			throw new IllegalArgumentException(
					"It's dangerous to go alone! take this!" + " Oh wait, this is an exception. try again next time!");
		}

		x = cos * m_x - sin * m_y;
		y = sin * m_x + cos * m_y;

		return new ImOrientation2D(x, y, dir);

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

	@Override
	public String toString() {
		return "Immutable " + super.toString();
	}
}
