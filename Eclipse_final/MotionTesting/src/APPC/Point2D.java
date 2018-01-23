package APPC;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.function.Function;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

/**
 *
 * Represents a cartesian point
 */
public class Point2D {
    public static final Point2D GLOBAL_ORIGIN = new Point2D(0, 0, 0);

    private double m_x;
    private double m_y;
    private double m_direction;

    /**
     * creates a 2D point with x,y coordinates and a direction
     * <p>the direction can only be changed using setDirection, and will be preserved in every function in this class</p>
     * @param x X axis value
     * @param y Y axis value
     * @param direction the direction of the point
     */
    public Point2D(double x, double y, double direction) {
        m_x = x;
        m_y = y;
        m_direction = normalize(direction);
    }

    /**
     * copy constructor
     * @param point2D
     */
    public Point2D(Point2D point2D){
        m_x = point2D.m_x;
        m_y = point2D.m_y;
        m_direction = point2D.m_direction;
    }


    public double getX() { return m_x; }
    public double getY() { return m_y; }

    /**
     * returns a new point which is a shift of this one by x and y
     * @param x shift along the X axis
     * @param y shift along the Y axis
     * @return this point shifted by x and y
     */
    private Point2D toNatrualChords(){
    	return this.rotate(-m_direction);
    }
    private Point2D toRegularChords(){
    	return this.rotate(m_direction);
    }
    
    public Point2D add(double x, double y, double dir) {
        //return (new Point2D(m_x + x, m_y + y, m_direction)).rotateRelativeTo(this, m_direction);
        Point2D meh = this.toNatrualChords();
        meh = new Point2D(meh.getX() + x, meh.getY() + y, meh.getDirection()).toRegularChords();
        return new Point2D(meh.getX(), meh.getY(), meh.getDirection() + dir);
    }
    
    public Point2D add(double x, double y) {
    	return add(x, y, 0);
    }
    
    public Point2D add(Point2D point){
    	return add(point.getX(),point.getY(), point.getDirection());
    }
    public Point2D sub(Point2D point) {
    	return add(point.neg());
    }
    public Point2D neg() {
    	return new Point2D(-m_x, -m_y, -m_direction);
    }
    
    public Point2D changePrespectiveTo(Point2D point){
    	return new Point2D(m_x-point.getX(),m_y-point.getY(),0).rotate(-point.getDirection());
    }

    /**
     * finds the coordinates of the other point relatively to this
     * @param other the point which will relate to this
     * @return other point in relation to this
     */
    public Point2D moveRelativeTo(Point2D other) {
        Point2D tmp = other.rotateRelativeTo(this, m_direction);
        return new Point2D(tmp.getX() - m_x, tmp.getY() - m_y, m_direction);
    }

    /**
     * apply the given matrix to this point (matrix multiplication)
     * @param transform the matrix which will be applied to the point. <b>Has to be 2X2!</b>
     * @return transform * point (matrix multiplication)
     * @throws IllegalArgumentException if transform isn't 2X2
     */
    public Point2D apply(Matrix transform) throws IllegalArgumentException {
        if (transform.rows() != 2 || transform.columns() != 2)
            throw new IllegalArgumentException(
                    String.format(
                            "transform matrix size 2X2 expected, found %dX%d instead",
                            transform.rows(),
                            transform.columns()));

        Vector ret = transform.multiply(new BasicVector(new double[] {m_x, m_y}));

        return new Point2D(ret.get(0), ret.get(1), m_direction);
    }

    /**
     * creates a new point which is a resize of this one
     * @param s scaled version of this point
     * @return a new point whose coordinates are this * s
     */
    public Point2D scale(double s) {
        return apply(genScaleMatrix(s));
    }

    /**
     * creates a new point which is a rotation of this one using (0,0) as origin
     * @param angle the angle of rotation
     * @return this point rotated by angle
     */
    public Point2D rotate(double angle) {
        return apply(genRotationMatrix(angle));
    }

    /**
     * creates a new point which is a rotation of this one using origin
     * @param origin the center of rotation
     * @param angle the angle of rotation
     * @return this point rotated with a different origin
     */
    //TODO- moveBy behaviour changed fix accordingly
    public Point2D rotateRelativeTo(Point2D origin, double angle) {
    	Point2D relative = new Point2D(
    			m_x - origin.getX(),
    			m_y - origin.getY(),
    			m_direction).rotate(angle);
    	
    	return new Point2D(relative.getX() + origin.getX(), relative.getY() + origin.getY(), m_direction);
    	//return new Point2D(relative.getX() +m_x, relative.getY() + m_y, m_direction);
    	//there where pluses
    }
    
    public Point2D rotateRelativeToChange(Point2D origin, double angle) {
    	Point2D relative = new Point2D(
    			m_x - origin.getX(),
    			m_y - origin.getY(),
    			m_direction).rotate(angle);
    	
    	return new Point2D(relative.getX() + origin.getX(), relative.getY() + origin.getY(), m_direction - angle);
    	//return new Point2D(relative.getX() +m_x, relative.getY() + m_y, m_direction);
    	//there where pluses
    }

    /**
     * create a 2X2 matrix by 4 elements
     * @param a11 first line, first column
     * @param a12 first line, second column
     * @param a21 second line, first column
     * @param a22 second line, second column
     * @return Matrix containing these 4 elements
     */
    public static org.la4j.Matrix genMatrix(double a11, double a12, double a21, double a22) {
        return new Basic2DMatrix(new double[][] {{a11, a12}, {a21, a22}});
    }

    /**
     * creates a matrix which, when applied on a point, rescales it
     * @param s the scale factor
     * @return A scale matrix
     */
    public static org.la4j.Matrix genScaleMatrix(double s) {
        return genMatrix(s, 0, 0, s);
    }

    /**
     * creates a matrix which, when applied on a point, rotates it
     * @param angle the rotation angle
     * @return A rotation matrix
     */
    public static org.la4j.Matrix genRotationMatrix(double angle) {
        return genMatrix(Math.cos(angle), -Math.sin(angle), Math.sin(angle), Math.cos(angle));
    }

    /**
     * creates a linear transformation from the matrix of the four elements
     * @param a11 first line, first column
     * @param a12 first line, second column
     * @param a21 second line, first column
     * @param a22 second line, second column
     * @return The linear transformation defined by these four elements
     */
    public Function<Point2D, Point2D> genTransformation(double a11, double a12, double a21, double a22) {
        return point -> point.apply(genMatrix(a11, a12, a21, a22));
    }

    /**
     * returns the polar representation of this point using origin as the source
     * @param other the center of rotation and distance
     * @return the polar representation of this point relatively to origin
     */
    public double[] toPolarRelative(Point2D other) {
        return new double[] {
                Math.hypot(m_x - other.getX(), m_y - other.getY()),
                Math.atan2(m_y - other.getY(), m_x - other.getX()) - m_direction };
    }

    public double distanceSquared(Point2D other) {
        return add(new Point2D(-other.m_x, -other.m_y, 0)).lengthSquared();
    }
    
    public double lengthSquared(){
	return m_x * m_x + m_y * m_y;
    }

    public double distance(Point2D other) {
        return Math.hypot(m_x-other.m_x, m_y-other.m_y);
    }

    
    /**
     *
     * @return the polar representation of this point relatively to the center (0,0)
     */
    public double[] toPolar() {
        return toPolarRelative(GLOBAL_ORIGIN);
    }

    public void setDirection(double dir) {
        m_direction = dir;
    }

    public double getDirection() { return m_direction; }

    /**
     * calculates the global coordinates of a relative <b>polar</b> point to this
     * @param radius the radius of the polar representation
     * @param angle the angle of the polar representation
     * @return
     */
    public Point2D genRelativeRotated(double radius, double angle) {
        return new Point2D(m_x + radius, m_y, m_direction).rotate(angle + m_direction);
    }

    /**
     * calculates the global coordinates of a relative <b>cartesian</b> point to this
     * @param x X axis shift
     * @param y Y axis shift
     * @return the new point, in relative coordinates to this point
     */
    public Point2D genRelative(double x, double y) {
        return new Point2D(m_x + x, m_y + y, m_direction).rotate(m_direction);
    }

    /**
     * Makes an angle in -2pi <= angle <= 2pi while keeping it's useful value the same
     * @param angle the angle which will be normalized
     * @return The angle normalized to be contained by -2pi and 2pi
     */
    private static double normalize(double angle) {
        if (angle >= 2*Math.PI) return normalize(angle - 2*Math.PI);
        if (angle <= -2*Math.PI) return normalize(angle + 2*Math.PI);
        return angle;
    }
    
    public double length() {
    	return Math.hypot(m_x, m_y);
    }

	@Override
	public String toString() {
		int p = 4;
		return "Point2D [m_x=" + goodRound(m_x, p) + ", m_y=" + goodRound(m_y, p) + ", m_direction=" + goodRound(m_direction, p) + "]";
	}
	
	// TODO goodRound doesn't use precision
	private static String goodRound(double num, int precision){
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(num);
	}
    
    
}
