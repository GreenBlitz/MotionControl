package gbmotion.base.point;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

/**
 * Mutable Point2D. each method will affect {@code this} and than return it.
 * 
 * @see Point2D
 * @see IPoint2D
 * @author karlo
 */
public class MPoint2D extends Point2D {

	public MPoint2D(double x, double y) {
		super(x, y);
	}

	public MPoint2D(Point2D other) {
		super(other);
	}

	public void setX(double x) {
		m_x = x;
	}

	public void setY(double y) {
		m_y = y;
	}

	@Override
	public IPoint2D moveBy(double x, double y) {
		m_x += x;
		m_y += y;
		return this;
	}

	@Override
	public IPoint2D rotate(double angle, boolean clockwise) {
		if (angle == 0)
			return this;

		double sin = Math.sin(angle);
		double cos = Math.cos(angle);

		double x = m_x;
		double y = m_y;

		if (!clockwise) {
			m_x = cos * x - sin * y;
			m_y = sin * x + cos * y;
		} else {
			m_x = cos * x + sin * y;
			m_y = sin * x - cos * y;
		}

		return this;
	}

	@Override
	public IPoint2D scale(double scale) {
		m_x *= scale;
		m_y *= scale;
		return this;
	}

	@Override
	public IPoint2D _apply(Matrix mat) {
		Vector vec = mat.multiply(new BasicVector(new double[] { m_x, m_y }));
		m_x = vec.get(0);
		m_y = vec.get(1);
		return this;
	}

	@Override
	public String toString() {
		return "Mutable " + super.toString();
	}
}
