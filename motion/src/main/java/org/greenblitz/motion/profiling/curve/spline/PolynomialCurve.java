package org.greenblitz.motion.profiling.curve.spline;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.profiling.curve.AbstractCurve;

public class PolynomialCurve extends AbstractCurve {

    /**
     * The degree of the polynomial. For a list:
     * [a, b, c, d]
     * we get a polynomial:
     * d*x^3 + c*x^2 + b*x + a
     */
    protected double[] x;
    protected double[] y;
    double tScaling;
    private int rank;

    public PolynomialCurve(int rank,double[] xArr, double[] yArr, double uStart, double uEnd, double tScaling){
        x = new double[rank + 1];
        y = new double[rank + 1];
        this.tScaling = tScaling;
        this.rank = rank;

        this.uStart = uStart*tScaling;
        this.uEnd = uEnd*tScaling;

        for (int i = 0; i < this.rank + 1; i++){
            x[i] = xArr[i];
            y[i] = yArr[i];
        }
    }

    public PolynomialCurve(int rank,double[] xArr, double[] yArr){
        this(rank,xArr, yArr, 0, 1, 1);
    }

    @Override
    protected Point getLocationInternal(double u) {
        double xVal = 0;
        double yVal = 0;
        for(int i = rank; i >= 0; i --){
            xVal = u*(xVal + x[i]);
            yVal = u*(yVal + y[i]);
        }
        return new Point(xVal,yVal);
    }

    protected Vector2D getDerivativeInter(double u){
        double xVal = 0;
        double yVal = 0;
        for(int i = rank; i > 0; i --){
            xVal = u*(xVal + i*x[i]);
            yVal = u*(yVal + i*y[i]);
        }
        return new Vector2D(xVal,yVal);
    }

    protected Vector2D getDoubleDerivativeInter(double u){
        double xVal = 0;
        double yVal = 0;
        for(int i = rank; i > 1; i --){
            xVal = u*(xVal + (i-1)*i*x[i]);
            yVal = u*(yVal + (i-1)*i*y[i]);
        }
        return new Vector2D(xVal,yVal);
    }

    @Override
    protected double getLinearVelocityInternal(double u) {
        return getDerivativeInter(u).norm();
    }

    @Override
    protected double getAngularVelocityInternal(double u) {
        return getCurvatureInternal(u)*getLinearVelocityInternal(u);
    }

    /**
     * Assumes constant curvature. to calculate actual length you will actually die.
     * @param u
     * @return
     */
    @Override
    protected double getLengthInternal(double u) {
        double length = Point.subtract(getLocationInternal(u),
                getLocationInternal(uStart)).norm();
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
     * @param u
     * @return
     */
    @Override
    protected double getCurvatureInternal(double u) {
        Vector2D derv = getDerivativeInter(u);
        Vector2D doubleDerv = getDoubleDerivativeInter(u);
        double normCubed = Math.pow(derv.norm(), 3);
        if (normCubed < 1E-3){
            return 0;
        }

        return (derv.getX()*doubleDerv.getY() - derv.getY()*doubleDerv.getX()) /
                normCubed;
    }

    @Override
    public ICurve getSubCurve(double uStart, double uEnd) {
        return new PolynomialCurve(rank,x, y,
                clamp(uStart)/tScaling, clamp(uEnd)/tScaling,
                tScaling);
    }

}
