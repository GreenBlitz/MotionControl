package org.greenblitz.motion.profiling.curve.arcmap;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.profiling.curve.AbstractCurve;
import org.greenblitz.motion.profiling.curve.ICurve;

public class ArcCurve implements ICurve {

    protected Point startPoint;
    protected Point endPoint;
    protected double curvature;

    public ArcCurve(Point startPoint, Point endPoint, double curvature){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.curvature = curvature;
    }

    @Override
    public Point getLocation(double u) {
        return null;
    }

    @Override
    public double getLinearVelocity(double u) {
        return 0;
    }

    @Override
    public double getAngularVelocity(double u) {
        return 0;
    }

    @Override
    public double getLength(double u) {
        return 0;
    }

    @Override
    public double getAngle(double u) {
        return 0;
    }

    @Override
    public double getCurvature() {
        return curvature;
    }

    @Override
    public double getCurvature(double u) {
        return curvature;
    }

    @Override
    public ICurve getSubCurve(double uStart, double uEnd) {
        return new ArcCurve(getLocation(uStart), getLocation(uEnd), curvature);
    }
}