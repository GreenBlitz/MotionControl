package org.greenblitz.motion.base;

/**
 * Represents a simple 2D point
 *
 * @author Alexey
 */
public class Point {

    public static final Point ORIGIN = new Point(0, 0);

    /**
     * the x coordinate: right to left
     * positive direction left
     */
    protected double x;
    /**
     * the y coordinate: forwards & backwards
     * positive direction forwards
     */
    protected double y;

    /**
     * @param x
     * @param y
     */
    public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public static Point cis(double ang, double len){
        return new Point(len*Math.sin(ang), len*Math.cos(ang));
    }

    /**
     * Returns a new point in the same location
     */
    public Point clone() {
        return new Point(x, y);
    }

    /**
     * @return A double array of the x and y values in that order
     */
    public double[] get() {
        return new double[]{x, y};
    }

    /**
     * Set new coordinates to the point
     *
     * @param x
     * @param y
     */
    public void set(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * Move the point by [x, y]
     *
     * @param x
     * @param y
     */
    public Point translate(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Point add(Point first, Point other){
        return first.clone().translate(other);
    }

    /**
     *
     * @return The negative of this point
     */
    public Point negate(){
        return new Point(-getX(), -getY());
    }

    /**
     * Rotate the point COUNTER-CLOCKWISE around (0, 0)
     *
     * @param radians
     */
    public Point rotate(double radians) {
        double cos = Math.cos(radians),
                sin = Math.sin(radians);
        Point temp = this.clone();
        setX(temp.y * sin + temp.x * cos);
        setY(temp.y * cos - temp.x * sin);
        return this;
    }

    /**
     * Move by the x and y of the point
     *
     * @param p
     */
    public Point translate(Point p) {
        return translate(p.getX(), p.getY());
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public static Point subtract(Point subtractee, Point subtractor) {
        return Point.add(subtractee, subtractor.clone().negate());
    }

    public static double dotProduct(Point a, Point b) {
        return a.x * b.x + a.y * b.y;
    }

    public static double normSquared(Point point) {
        return dotProduct(point, point);
    }

    public static double distSqared(Point a, Point b) {
        return normSquared(subtract(a, b));
    }

    public static double dist(Point a, Point b) {
        return Math.hypot(subtract(a, b).x, subtract(a, b).y);
    }

    public static boolean isFuzzyEqual(double first, double second, double epsilon) {
        return Math.abs(first - second) < epsilon;
    }

    public static boolean isFuzzyEqual(double first, double second) {
        return isFuzzyEqual(first, second, 1E-6);
    }

    public static boolean fuzzyEquals(Point fir, Point sec, double epsilon) {
        return isFuzzyEqual(fir.getX(), sec.getX(), epsilon) && isFuzzyEqual(fir.getY(), sec.getY(), epsilon);
    }

    public static Point weightedAvg(Point a, Point b, double bWeight) {
        return new Point((1 - bWeight) * a.x + bWeight * b.x, (1 - bWeight) * a.y + bWeight * b.y);
    }

    public static Point avg(Point a, Point b) {
        return weightedAvg(a, b, 0.5);
    }

    /**
     * calculates a point on a Bezier curve.
     * @param locInCurve the location of the point along the curve. 0 iff start, 1 iff end
     * @param corners the corners of the curve
     * @return the desired point
     */
    public static Point bezierSample(double locInCurve, Point... corners){
        return bezierSample(corners, corners.length, locInCurve);
    }

    private static Point bezierSample(Point[] corners, int cornersUsedLength, double locInCurve) {
        if (cornersUsedLength == 1) return corners[0];
        for (int ind = 0; ind < cornersUsedLength-1; ind++)
            corners[ind] = weightedAvg(corners[ind], corners[ind + 1], locInCurve);
        return bezierSample(corners, cornersUsedLength - 1, locInCurve);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return (Point.isFuzzyEqual(this.getX(), point.getX()))
                && isFuzzyEqual(this.getY(), point.getY());
    }

    public Point frcToMathCoords(){
        return new Point(-x,y);
    }

    public Point mathToFrcCoords(){
        return frcToMathCoords();
    }

    /**
     *
     * @return first element is the length, second is the angle
     */
    public double[] toPolarCoords(){
        return new double[] {dist(Point.ORIGIN, this), Math.atan2(getY(), getX())};
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
