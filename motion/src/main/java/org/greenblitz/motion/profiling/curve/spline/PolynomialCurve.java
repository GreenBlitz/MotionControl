package org.greenblitz.motion.profiling.curve.spline;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.profiling.curve.AbstractCurve;

/**
 * @author peleg
 */
public class PolynomialCurve extends AbstractCurve {

    /**
     * The degree of the polynomial for the x coord. For a list:
     * [a, b, c, d]
     * we get a polynomial:
     * d*x^3 + c*x^2 + b*x + a
     */
    protected double[] x;
    /**
     * Same as `double[] x`, but for the y coord
     */
    protected double[] y;
    /**
     * Used to scale the time factor so it would be possible to create [0, t] curves and subcurves for them.
     */
    protected double tScaling;
    /**
     * Highest degree with non-zero coef. Must be 0 or bigger.
     * e.g. a polynomial of degree 0 is a constant.
     *      of degree 2 is a parabola.
     */
    private int rank;

    /**
     *
     * @param rank Highest degree with non-zero coef. Must be 0 or bigger.
     * @param xArr The array of x coefs
     * @param yArr The array of y coefs
     * @param uStart The start of the range
     * @param uEnd The end of the range
     * @param tScaling The scaling of the range to get a new range
     */
    public PolynomialCurve(int rank, double[] xArr, double[] yArr, double uStart, double uEnd, double tScaling) {
        x = new double[rank + 1];
        y = new double[rank + 1];
        this.tScaling = tScaling;
        this.rank = rank;

        this.uStart = uStart * tScaling;
        this.uEnd = uEnd * tScaling;

        for (int i = 0; i < this.rank + 1; i++) {
            x[i] = xArr[i];
            y[i] = yArr[i];
        }
    }

    /**
     * @see PolynomialCurve#PolynomialCurve(int, double[], double[], double, double, double)
     * @param rank
     * @param xArr
     * @param yArr
     */
    public PolynomialCurve(int rank, double[] xArr, double[] yArr) {
        this(rank, xArr, yArr, 0, 1, 1);
    }

    @Override
    protected Point getLocationInternal(double u) {
        double xVal = 0;
        double yVal = 0;
        for (int i = rank; i >= 0; i--) {
            xVal = u * xVal + x[i];
            yVal = u * yVal + y[i];
        }
        return new Point(xVal, yVal);
    }

    /**
     *
     * @param u the "time" param
     * @return the vector of partial derivatives at that point
     */
    public Vector2D getDerivativeInter(double u) {
        double xVal = 0;
        double yVal = 0;
        for (int i = rank; i > 0; i--) {
            xVal = u * xVal + i * x[i];
            yVal = u * yVal + i * y[i];
        }
        return new Vector2D(xVal, yVal);
    }

    /**
     *
     * @param u the "time" param
     * @return the vector of second partial derivative
     */
    public Vector2D getDoubleDerivativeInter(double u) {
        double xVal = 0;
        double yVal = 0;
        for (int i = rank; i > 1; i--) {
            xVal = u * xVal + (i - 1) * i * x[i];
            yVal = u * yVal + (i - 1) * i * y[i];
        }
        return new Vector2D(xVal, yVal);
    }

    /**
     * Assumes constant curvature. to calculate actual length you will actually die.
     *
     * @param u
     * @return
     */
    @Override
    protected double getLengthInternal(double u) {
        double length = Point.dist(getLocationInternal(u),
                getLocationInternal(uStart));
        double curvature = getCurvature();
        if (Point.isFuzzyEqual(curvature, 0, 1E-3))
            return length;
        if (Math.abs(length * curvature / 2) > 1)
            throw new RuntimeException("len * curve / 2 is too big");
        return 2 / curvature * Math.asin(length * curvature / 2);
    }

    @Override
    protected double getAngleInternal(double u) {
        Vector2D d = getDerivativeInter(u);
        return Math.atan2(d.getY(), d.getX());
    }

    /**
     * See https://www.wikipedia.org/wiki/Curvature
     * @see AbstractCurve#getCurvatureInternal(double)
     *
     * @param u
     * @return
     */
    @Override
    protected double getCurvatureInternal(double u) {
        Vector2D derv = getDerivativeInter(u);
        Vector2D doubleDerv = getDoubleDerivativeInter(u);
        double normCubed = Math.pow(derv.norm(), 3);
        if (normCubed < 1E-3) {
            return 0;
        }

        return (derv.getX() * doubleDerv.getY() - derv.getY() * doubleDerv.getX()) /
                normCubed;
    }

    @Override
    public ICurve getSubCurve(double uStart, double uEnd) {
        return new PolynomialCurve(rank, x, y,
                clamp(uStart) / tScaling, clamp(uEnd) / tScaling,
                tScaling);
    }

    public int getRank() {
        return rank;
    }

}
