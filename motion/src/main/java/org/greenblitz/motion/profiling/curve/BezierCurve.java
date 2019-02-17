package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

public class BezierCurve implements ICurve {

    BezierSegment segment;
    double uStart, uSize;

    private BezierCurve(BezierSegment segment, double uStart, double uEnd) {
        this.segment = segment;
        this.uStart = uStart;
        this.uSize = uEnd - uStart;
    }

    public BezierCurve(State start, State end, double uStart, double uEnd) {
        this(new BezierSegment(start, end, 0, 1), uStart, uEnd);
    }

    public BezierCurve(State start, State end){
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
        return 0;
    }

    @Override
    public double getAngle(double u) {
        return 0;
    }

    @Override
    public double getCurvature(double u) {
        return segment.getCurvature(convertU(u));
    }

    @Override
    public ICurve getSubCurve(double uStart, double uEnd) {
        return new BezierCurve(
                segment,
                (uStart / this.uSize) + this.uStart,
                (uEnd / this.uSize) + this.uStart);
    }
}
