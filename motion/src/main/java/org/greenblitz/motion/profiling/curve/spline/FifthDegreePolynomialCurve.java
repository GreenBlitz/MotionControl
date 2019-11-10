package org.greenblitz.motion.profiling.curve.spline;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.AbstractCurve;
import org.greenblitz.motion.profiling.curve.ICurve;

public class FifthDegreePolynomialCurve extends AbstractCurve {

    /**
     * The degree of the polynomial. For a list:
     * [a, b, c, d]
     * we get a polynomial:
     * d*x^3 + c*x^2 + b*x + a
     */
    protected double[] x;
    protected double[] y;
    double tScaling;

    public FifthDegreePolynomialCurve(double[] xArr, double[] yArr, double uStart, double uEnd, double tScaling){
        x = new double[6];
        y = new double[6];
        this.tScaling = tScaling;

        this.uStart = uStart*tScaling;
        this.uEnd = uEnd*tScaling;

        for (int i = 0; i < 6; i++){
            x[i] = xArr[i];
            y[i] = yArr[i];
        }
    }

    public FifthDegreePolynomialCurve(double[] xArr, double[] yArr){
        this(xArr, yArr, 0, 1, 1);
    }

    @Override
    protected Point getLocationInternal(double u) {
        return new Point(x[0] + u*(x[1] + u*(x[2] + u*(x[3]))),
                y[0] + u*(y[1] + u*(y[2] + u*y[3])));
    }

    protected Vector2D getDerivativeInter(double u){
        return new Vector2D(x[1] + u*(2*x[2] + u*(3*x[3])),
                y[1] + u*(2*y[2] + u*(3*y[3])));
    }

    protected Vector2D getDoubleDerivativeInter(double u){
        return new Vector2D(2*x[2] + 6*u*x[3],
                2*y[2] + 6*u*y[3]);
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
        return new ThirdDegreePolynomialCurve(x, y,
                clamp(uStart)/tScaling, clamp(uEnd)/tScaling,
                tScaling);
    }

}
