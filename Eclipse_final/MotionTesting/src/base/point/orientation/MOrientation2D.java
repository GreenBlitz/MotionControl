package base.point.orientation;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

import base.point.IPoint2D;

public class MOrientation2D extends Orientation2D {

	public MOrientation2D(double x, double y, double direction) {
		super(x, y, direction);
	}

	public MOrientation2D(IPoint2D point, double direction) {
		super(point, direction);
	}

	@Override
	public IOrientation2D moveBy(double x, double y, double direction, DirectionEffect effect) {
		return null;
	}

	@Override
	public IOrientation2D rotate(double angle, DirectionEffect effect) {
		double sin, cos;
		double x = m_x, y = m_y;
		
		switch(effect) {
		case IGNORED: case RESERVED:
			sin = Math.sin(angle);
			cos = Math.cos(angle);
			
			m_x = cos * x - sin * y;
			m_y = sin * x + cos * y;
			
			return this;
		case PRE_CHANGED: case POST_CHANGED:
			m_direction = normalize(m_direction + angle);
			return rotate(angle, DirectionEffect.IGNORED);
		}
		
		return this;
	}

	@Override
	public IOrientation2D multiply(double scale) {
		m_x *= scale;
		m_y *= scale;
		return this;
	}

	@Override
	public IOrientation2D apply(Matrix transformation, DirectionEffect effect) {
		Vector vec = transformation.multiply(new BasicVector(new double[] { m_x, m_y, m_direction }));
		m_x = vec.get(0);
		m_y = vec.get(1);
		m_direction = effect == DirectionEffect.IGNORED ? 
	}

	@Override
	public IOrientation2D setDirection(double angle) {
		m_direction = angle;
		return this;
	}

}
