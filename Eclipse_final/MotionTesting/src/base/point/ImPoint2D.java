package base.point;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

/**
 * Immutable 2-dimensional point. 
 * The x and y values of this class won't change from any method defined here.
 * @author karlo
 *
 */
public class ImPoint2D extends Point2D {

	public ImPoint2D(double x, double y) {
		super(x, y);
	}
	
	public ImPoint2D(Point2D other) {
		super(other);
	}

	public Point2D moveBy(double x, double y) {
		return new ImPoint2D(m_x + x, m_y + y);
	}
	
	public Point2D moveBy(Point2D p) {
		return moveBy(p.getX(), p.getY());
	}

	@Override
	public Point2D rotate(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new ImPoint2D(cos * m_x - sin * m_y, sin * m_x + cos * m_y);
	}

	@Override
	public Point2D scale(double scale) {
		return new ImPoint2D(scale * m_x, scale * m_y);
	}

	@Override
	public Point2D _apply(Matrix mat) {
		Vector vec = mat.multiply(new BasicVector(new double[]{m_x, m_y}));
		return new ImPoint2D(vec.get(0), vec.get(1));
	}

	@Override
	protected Point2D relativeToPolar(Point2D origin) {
		return new ImPoint2D(distance(origin), Math.atan2(m_y - origin.m_y, m_x - origin.m_x));
	}
}
