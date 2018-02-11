package base.point;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

/**
 * Immutable Point2D. Each method will return a new instance leaving
 * {@code this} unchanged.
 * 
 * @see Point2D
 * @see IPoint2D
 * @author karlo
 */
public class ImPoint2D extends Point2D {

	public ImPoint2D(double x, double y) {
		super(x, y);
	}

	public ImPoint2D(IPoint2D other) {
		super(other);
	}

	@Override
	public IPoint2D moveBy(double x, double y) {
		return new ImPoint2D(m_x + x, m_y + y);
	}

	@Override
	public IPoint2D rotate(double angle) {
		if (angle == 0)
			return new ImPoint2D(m_x, m_y);

		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new ImPoint2D(cos * m_x - sin * m_y, sin * m_x + cos * m_y);
	}

	@Override
	public IPoint2D scale(double scale) {
		return new ImPoint2D(scale * m_x, scale * m_y);
	}

	@Override
	public IPoint2D _apply(Matrix mat) {
		Vector vec = mat.multiply(new BasicVector(new double[] { m_x, m_y }));
		return new ImPoint2D(vec.get(0), vec.get(1));
	}
}
