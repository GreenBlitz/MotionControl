package org.greenblitz.motion.base;

/**
 * Represents a simple 2D point
 *
 * @author Alexey
 */
public class Point {

    public enum CoordinateSystems {
        /**
         * Regular mathematics coordinate system.
         * positive x is right
         * positive y is forwards
         * angle 0 is facing positive x
         * angle rotation is counter clockwise.
         */
        MATH(0),
        /**
         * Locations used by localizer and follower commands such as motion.
         * positive x is left.
         * positive y is forwards.
         * angle 0 is facing positive y.
         * angle rotation is counter clockwise.
         */
        LOCALIZER(1),
        /**
         * The coordinates of WPILib's weaver. Similar to picture/matrix coordinates.
         * positive x is down.
         * positive y is right.
         * angle 0 is facing positive y.
         * angle rotation is counter clockwise.
         */
        WEAVER(2);

        int index;

        CoordinateSystems(int ind) {
            index = ind;
        }

        public int getIndex() {
            return index;
        }
    }

    public static final Point ORIGIN = new Point(0, 0);

    /**
     * the x coordinate: right to left
     */
    protected double x;
    /**
     * the y coordinate: forwards and backwards
     */
    protected double y;

    public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public static Point cis(double ang, double len) {
        return new Point(len * Math.sin(ang), len * Math.cos(ang));
    }


    public Point clone() {
        return new Point(x, y);
    }


    public double[] get() {
        return new double[]{x, y};
    }


    public void set(double x, double y) {
        setX(x);
        setY(y);
    }


    public Point translate(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Point add(Point first, Point other) {
        return first.clone().translate(other);
    }

    public Point negate() {
        return new Point(-x, -y);
    }

    public Point scale(double scale){
        return new Point(scale*x, scale*y);
    }

    public Point rotate(double radians) {
        double cos = Math.cos(radians),
                sin = Math.sin(radians);
        Point temp = this.clone();
        setX(temp.y * sin + temp.x * cos);
        setY(temp.y * cos - temp.x * sin);
        return this;
    }

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

    public static double norm(Point point) {
        return Math.hypot(point.x, point.y);
    }

    public double norm(){return norm(this);}

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

    public Point weightedAvg(Point b, double bWeight) {
        return new Point((1 - bWeight) * x + bWeight * b.x, (1 - bWeight) * y + bWeight * b.y);
    }

    public Point avg(Point b) {
        return weightedAvg(b, 0.5);
    }

    public static Point bezierSample(double locInCurve, Point... corners) {
        if(locInCurve == 0) return corners[0];
        int degree = corners.length - 1;
        double factor = Math.pow(locInCurve, degree);
        double factorFactor = 1 / locInCurve - 1;
        double binomial = 1;
        double sumX = 0, sumY = 0;

        for (int ind = 0; ind < corners.length; ind++) {
            sumX += binomial * factor * corners[degree-ind].getX();
            sumY += binomial * factor * corners[degree-ind].getY();
            if (ind == degree)
                break;
            binomial *= (degree - ind);
            binomial /= ind + 1;
            factor *= factorFactor;
        }
        return new Point(sumX, sumY);
    }

    /**
     * calculates a point on a Bezier curve.
     *
     * @param locInCurve the location of the point along the curve. 0 iff start, 1 iff end
     * @param corners    the corners of the curve
     * @return the desired point
     */
    public static Point recursiveBezierSample(double locInCurve, Point... corners) {
        return recursiveBezierSample(corners, corners.length, locInCurve);
    }

    private static Point recursiveBezierSample(Point[] corners, int cornersUsedLength, double locInCurve) {
        if (cornersUsedLength == 1) return corners[0];
        for (int ind = 0; ind < cornersUsedLength - 1; ind++)
            corners[ind] = corners[ind].weightedAvg(corners[ind + 1], locInCurve);
        return recursiveBezierSample(corners, cornersUsedLength - 1, locInCurve);
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

    public Point localizerToMathCoords() {
        return new Point(-x, y);
    }

    public Point mathToWeaverCoords() {
        return new Point(-y, x);
    }

    public Point weaverToLocalizerCoords() {
        return new Point(-y, x);
    }


    public Point changeCoords(CoordinateSystems src, CoordinateSystems dest) {
        if (src == CoordinateSystems.LOCALIZER && dest == CoordinateSystems.MATH) {
            return localizerToMathCoords();
        } else if (src == CoordinateSystems.MATH && dest == CoordinateSystems.WEAVER) {
            return mathToWeaverCoords();
        } else if (src == CoordinateSystems.WEAVER && dest == CoordinateSystems.LOCALIZER) {
            return weaverToLocalizerCoords();
        }
        switch (src) {
            case MATH:
                return mathToWeaverCoords().changeCoords(CoordinateSystems.WEAVER, dest);
            case LOCALIZER:
                return localizerToMathCoords().changeCoords(CoordinateSystems.MATH, dest);
            case WEAVER:
                return weaverToLocalizerCoords().changeCoords(CoordinateSystems.LOCALIZER, dest);
        }
        throw new IllegalArgumentException("Roses are red, Violets are blue, I don't know this enum, what should I do???");
    }

    @Deprecated
    public Point mathToFrcCoords() {
        return localizerToMathCoords();
    }

    /**
     * @return first element is the length, second is the angle
     */
    public double[] toPolarCoords() {
        return new double[]{dist(Point.ORIGIN, this), Math.atan2(getY(), getX())};
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
