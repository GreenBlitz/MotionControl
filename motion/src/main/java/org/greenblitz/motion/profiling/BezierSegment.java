package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

public class BezierSegment {

    private Point p0, p1, p2, p3;
    private Point p01, p12, p23;
    private Point p012, p123;
    private double tStart, tEnd, tSize;
    private static final IndexOutOfBoundsException timeException =
            new IndexOutOfBoundsException("Time not in this segment");

    public BezierSegment(Point p0, Point p1, Point p2, Point p3, double tStart, double tEnd) {
        this.tStart = tStart;
        this.tEnd = tEnd;
        tSize = tEnd - tStart;
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

    public Point getVelocity(double t) {
        if (isTimeOutOfSegment(t)/*yes*/)
            throw timeException;
        t = (t - tStart) / tSize;
        double tt = 1 - t;
        return Point.add(Point.add(p01.scale(tt * tt), p12.scale(t * tt)), p23.scale(t * t));
    }

    public Point getAcceleration(double t) {
        t = (t-tStart)/tSize;
        return Point.add(p012.scale((1-t)), p123.scale(t));
    }

    public double getCurvature (double t) {
        Point derivative1 = getVelocity(t),
                derivative2 = getAcceleration(t);
        double derivative1X = derivative1.getX(),
                derivative1Y = derivative1.getY();

        double numerator = (derivative1X * derivative2.getY() - derivative1Y * derivative2.getX());
        double denominator = Math.pow(derivative1X, 2) + Math.pow(derivative1Y, 2);
        denominator =  Math.pow(denominator, 1.5);
        return numerator / denominator;
    }

    public double getAngularVelocity (double t) {
        Point derivative1 = getVelocity(t),
                derivative2 = getAcceleration(t);
        double derivative1X = derivative1.getX(),
                derivative1Y = derivative1.getY();

        double numerator = (derivative1X * derivative2.getY() - derivative1Y * derivative2.getX());
        double denominator = Math.pow(derivative1X, 2) + Math.pow(derivative1Y, 2);
        return numerator / denominator;
    }

    public BezierSegment(Point p1, Point p2, Point p3, Point p4, double tStart) {
        this(p1, p2, p3, p4, tStart, tStart+1);
    }
    public BezierSegment(State start, State end, double tStart, double tEnd) {
        this(start,
                Point.add(start, start.velocity.scale((tEnd-tStart) / 3)),
                Point.subtract(end, end.velocity.scale((tEnd-tStart) / 3)),
                end,
                tStart,
                tEnd);
    }
    public BezierSegment(State start, State end, double tStart) {
        this(start, end, tStart, tStart+1);
    }

    public boolean isTimeOutOfSegment(double t) {
        return t < tStart || t > tEnd;
    }


    public Point getLocation(double t) {
        if (isTimeOutOfSegment(t))
            throw timeException;
        return Point.bezierSample((t - tStart) / tSize, p0, p1, p2, p3);
    }

    public double getTStart() {
        return tStart;
    }

    public double getTEnd() {
        return tEnd;
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
                ", tStart=" + tStart +
                ", tEnd=" + tEnd +
                '}';
    }
}
