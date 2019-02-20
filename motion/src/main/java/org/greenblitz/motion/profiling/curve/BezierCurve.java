package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

public class BezierCurve implements ICurve {

    BezierSegment segment;
    double uStart, uSize;
    double curvature = Double.NaN;

    static final double DEFAULT_MIN_VELOCITY = 0.1;

    private BezierCurve(BezierSegment segment, double uStart, double uEnd) {
        this.segment = segment;
        this.uStart = uStart;
        this.uSize = uEnd - uStart;
    }

    private BezierCurve(State start, State end, double uStart, double uEnd) {
        this(new BezierSegment(start, end, 0, 1), uStart, uEnd);
    }

    public BezierCurve(State start, State end, double minVelocity, double uStart, double uEnd) {
        this(Math.abs(start.getLinearVelocity()) >= minVelocity ? start :
                new State(start.getX(), start.getY(), start.getAngle(), minVelocity, start.getAngularVelocity(), start.getLinearAccel(), start.getAngularAccel()),
                Math.abs(end.getLinearVelocity()) >= minVelocity ? end :
                        new State(end.getX(), end.getY(), end.getAngle(), minVelocity, end.getAngularVelocity(), end.getLinearAccel(), end.getAngularAccel()),
                uStart, uEnd);
    }

    public BezierCurve(State start, State end, double minVelocity){
        this(start, end, minVelocity, 0, 1);
    }

    public BezierCurve(State start, State end) {
        this(start, end, DEFAULT_MIN_VELOCITY);
    }

    private double convertU(double u) {
        return (u * uSize) + uStart;
    }


    @Override
    public Point getLocation(double u) {
        return segment.getLocation(convertU(u));
    }

    public Point getVelocity(double u) {
        return segment.getVelocity(convertU(u));
    }

    @Override
    public double getLinearVelocity(double u) {
        return getVelocity(u).norm();
    }

    @Override
    public double getAngularVelocity(double u) {
        return segment.getAngularVelocity(convertU(u));
    }

    @Override
    public double getLength(double u) {
        double length = Point.subtract(getLocation(u), getLocation(0)).norm();
        double curvature = getCurvature();
        if (Point.isFuzzyEqual(curvature, 0, 1E-3))
            return length;
        return 2 / curvature * Math.asin(length * curvature / 2);
    }

    @Override
    public double getAngle(double u) {
        Point vel = segment.getVelocity(u);
        return Math.atan2(vel.getY(), vel.getX());
    }

    @Override
    public double getCurvature() {
        if (Double.isNaN(curvature)/*is NaN*/)
            curvature = segment.getCurvature(convertU(0.5));
        return curvature;
    }

    @Override
    public double getCurvature(double u) {
        return segment.getCurvature(u);
    }

    @Override
    public ICurve getSubCurve(double uStart, double uEnd) {
        return new BezierCurve(
                segment,
                (uStart * this.uSize) + this.uStart,
                (uEnd * this.uSize) + this.uStart);
    }

    @Override
    public String toString() {
        return "BezierCurve{" +
                "start=" + new State(getLocation(0), getVelocity(0)) +
                ", end=" + new State(getLocation(1), getVelocity(1)) +
                ", uStart=" + uStart +
                ", uEnd=" + (uStart + uSize) +
                '}';
    }
}
