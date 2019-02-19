package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

public class BezierSegment {

    private Point p0, p1, p2, p3; // points for location calculation (defines the curve)
    private Point p01, p12, p23; // points for velocity calculation
    private Point p012, p123; // points for acceleration calculation
    private double uStart, uEnd, uSize;

    public BezierSegment(Point p0, Point p1, Point p2, Point p3, double uStart, double uEnd) {
        this.uStart = uStart;
        this.uEnd = uEnd;
        uSize = uEnd - uStart;
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        p01 = Point.subtract(p1, p0).scale(3);
        p12 = Point.subtract(p2, p1).scale(6);
        p23 = Point.subtract(p3, p2).scale(3);
        p012 = Point.add(Point.subtract(p0, p1), Point.subtract(p2, p1)).scale(6);
        p123 = Point.add(Point.subtract(p1, p2), Point.subtract(p3, p2)).scale(6);
    }

    public BezierSegment(Point p1, Point p2, Point p3, Point p4, double uStart) {
        this(p1, p2, p3, p4, uStart, uStart + 1);
    }

    public BezierSegment(State start, State end, double uStart, double uEnd) {
        this(start,
                Point.add(start, start.getVelocity().scale((uEnd - uStart) / 3)),
                Point.subtract(end, end.getVelocity().scale((uEnd - uStart) / 3)),
                end,
                uStart,
                uEnd);
    }

    public BezierSegment(State start, State end, double uStart) {
        this(start, end, uStart, uStart + 1);
    }


    public Point getLocation(double t) {
        if (isTimeOutOfSegment(t))
            throw new IllegalArgumentException("Time not in this segment");
        return Point.bezierSample((t - uStart) / uSize, p0, p1, p2, p3);
    }

    public Point getVelocity(double t) {
        if (isTimeOutOfSegment(t)/*yes*/)
            throw new IllegalArgumentException("Time not in this segment");
        t = (t - uStart) / uSize;
        double tt = 1 - t;
        return Point.add(Point.add(p01.scale(tt * tt), p12.scale(t * tt)), p23.scale(t * t));
    }

    public Point getAcceleration(double t) {
        if (isTimeOutOfSegment(t)/*yes*/)
            throw new IllegalArgumentException("Time not in this segment");
        t = (t - uStart) / uSize;
        return Point.add(p012.scale((1 - t)), p123.scale(t));
    }

    public double getCurvature(double t) {
        Point derivative1 = getVelocity(t),
                derivative2 = getAcceleration(t);
        double derivative1X = derivative1.getX(),
                derivative1Y = derivative1.getY();

        double numerator = (derivative1X * derivative2.getY() - derivative1Y * derivative2.getX());
        double denominator = Math.pow(derivative1X, 2) + Math.pow(derivative1Y, 2);
        denominator = Math.pow(denominator, 1.5);
        return numerator / denominator;
    }

    public double getAngularVelocity(double t) {
        Point derivative1 = getVelocity(t),
                derivative2 = getAcceleration(t);
        double derivative1X = derivative1.getX(),
                derivative1Y = derivative1.getY();

        double numerator = (derivative1X * derivative2.getY() - derivative1Y * derivative2.getX());
        double denominator = Math.pow(derivative1X, 2) + Math.pow(derivative1Y, 2);
        return numerator / denominator;
    }

    public boolean isTimeOutOfSegment(double t) {
        return t < uStart || t > uEnd;
    }


    public double getTStart() {
        return uStart;
    }

    public double getTEnd() {
        return uEnd;
    }

    public Point getStartVelocity() {
        return p01;
    }

    public Point getStartLocation() {
        return p0;
    }

    @Override
    public String toString() {
        return "BezierSegment{" +
                "p0=" + p0 +
                ", p1=" + p1 +
                ", p2=" + p2 +
                ", p3=" + p3 +
                ", uStart=" + uStart +
                ", uEnd=" + uEnd +
                '}';
    }
}
