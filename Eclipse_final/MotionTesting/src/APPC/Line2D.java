package APPC;

/**
 *
 * Representing a 2D line
 */
public class Line2D {
    private Point2D m_begin;
    private Point2D m_end;

    public Line2D(Point2D begin, Point2D end) {
        m_begin = begin;
        m_end = end;
    }

    /**
     * converts the point to the form Ax + By = C
     * @return the equation which describes the line as an array
     */
    public double[] toEquation() {
        double m = (m_begin.getY() - m_end.getY()) / (m_begin.getX() - m_end.getX());
        if (!Double.isFinite(m)) return new double[] {1, 0, m_begin.getX()};
        return new double[] {-m, 1, m_begin.getY() - m * m_begin.getX()};
    }

    public double distance(Point2D point) {
        double[] eq = toEquation();
        return Math.abs(eq[0] * point.getX() + eq[1] * point.getY() + eq[2]) / Math.hypot(eq[0], eq[1]);
    }
}
