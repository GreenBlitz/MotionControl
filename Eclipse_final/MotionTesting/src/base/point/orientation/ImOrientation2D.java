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
			dir = normalizeAngle(m_direction + direction);
			break;
		case RESERVED:
			dir = m_direction;
			break;
		default:
			throw new IllegalArgumentException(
					"Oh, the places you'll go! Wait, how did we got here? This shouldn't be possible!");
		}

		cos = Math.cos(direction);
		sin = Math.sin(direction);

		return new ImOrientation2D(m_x + x * cos + y * sin, m_y + x * sin - y * cos, normalizeAngle(dir));

	}

	@Override
	public IOrientation2D rotate(double angle, boolean clockwise, DirectionEffect effect) {
		double sin = Math.sin(angle), cos = Math.cos(angle);
		double x, y, dir;

		if (!clockwise) {
			x = cos * m_x + sin * m_y;
			y = sin * m_x - cos * m_y;
		} else {
			x = cos * m_x - sin * m_y;
			y = sin * m_x + cos * m_y;
		}

		dir = effect.changed() ? normalizeAngle(m_direction + angle) : m_direction;

		return new ImOrientation2D(x, y, dir);
	}

	@Override
	public IOrientation2D scale(double scale, DirectionEffect effect) {
		return new ImOrientation2D(m_x * scale, m_y * scale,
				normalizeAngle((effect.changed() ? m_direction * scale : m_direction)));
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

		double direction = normalizeAngle(effect.changed() ? vec.get(2) : m_direction);

		return new ImOrientation2D(x, y, direction);
	}

	@Override
	public IOrientation2D setDirection(double angle) {
		return new ImOrientation2D(m_x, m_y, normalizeAngle(angle));
	}

	@Override
	public IPoint2D moveBy(double x, double y) {
		IOrientation2D rotated = Orientation2D.immutable(this).rotate(-getDirection(), DirectionEffect.IGNORED);
		return Orientation2D.immutable(rotated.getX() + x, rotated.getY() + y, rotated.getDirection())
				.rotate(getDirection(), DirectionEffect.IGNORED);
	}

	@Override
	public IOrientation2D copy(double x, double y, double direction) {
		return new ImOrientation2D(x, y, normalizeAngle(direction));
	}

	@Override
	public String toString() {
		return "Immutable " + super.toString();
	}
}
