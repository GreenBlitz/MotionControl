package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

public class BezierCurve implements ICurve {

    BezierSegment segment;
    double uStart, uSize;
    double curvature = Double.NaN;

    private BezierCurve(BezierSegment segment, double uStart, double uEnd) {
        this.segment = segment;
        this.uStart = uStart;
        this.uSize = uEnd - uStart;
    }

    public BezierCurve(State start, State end, double uStart, double uEnd) {
        this(new BezierSegment(start, end, 0, 1), uStart, uEnd);
    }

    public BezierCurve(State start, State end) {
        this(start, end, 0, 1);
    }

    private double convertU(double u) {
        return (u - uSize) / uSize;
    }


    @Override
    public Point getLocation(double u) {
        return segment.getLocation(convertU(u));
    }

    @Override
    public double getLinearVelocity(double u) {
        return segment.getVelocity(convertU(u)).norm();
    }

    @Override
    public double getAngularVelocity(double u) {
        return segment.getAngularVelocity(convertU(u));
    }

    @Override
    public double getLength(double u) {
        double length = Point.subtract(getLocation(1), getLocation(0)).norm();
        double curvature = getCurvature(0.5);
        if (Point.isFuzzyEqual(curvature, 0, 1E-4))
            return length;
        return 2 / curvature * Math.asin(length * curvature / 2);
    }

    @Override
    public double getAngle(double u) {
        Point vel = segment.getVelocity(u);
        return Math.atan2(vel.getY(), vel.getX());
    }

    @Override
    public double getCurvature(double u) {
        if(curvature!= curvature/*is NaN*/)
            curvature = segment.getCurvature(convertU(u));
        return curvature;
    }

    @Override
    public ICurve getSubCurve(double uStart, double uEnd) {
        return new BezierCurve(
                segment,
                (uStart / this.uSize) + this.uStart,
                (uEnd / this.uSize) + this.uStart);
    }
}
