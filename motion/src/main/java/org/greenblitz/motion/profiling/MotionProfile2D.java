package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

import java.util.ArrayList;
import java.util.List;

public class MotionProfile2D {

    private final List<BezierSegment> segments;

    private int lastSegment;

    // TODO: 2/13/2019 test the thing
    public MotionProfile2D(List<State> positions) {
        this.lastSegment = 0;
        this.segments = new ArrayList<>();
        for (int index = 0; index < positions.size() - 1; index++) {
            BezierSegment current = new BezierSegment(positions.get(index), positions.get(index + 1),
                    index);
            this.segments.add(current);
        }
    }

    private BezierSegment getSegment(double t) {
        for (int ind = 0; ind < segments.size(); ind++) {
            if (!segments.get((ind + lastSegment) % segments.size()).isTimeOutOfSegment(t)) {
                lastSegment = (lastSegment + ind) % segments.size();
                return segments.get(lastSegment);
            }
        }
        throw new RuntimeException();
    }

    public double getTStart(){
        return segments.get(0).getTStart();
    }

    public double getTEnd(){
        return segments.get(segments.size()-1).getTEnd();
    }

    public Point getLocation(double t) {
        return getSegment(t).getLocation(t);
    }

    public Point getVelocity(double t) {
        return getSegment(t).getVelocity(t);
    }

    public double getAngularVelocity(double t) {
        return getSegment(t).getAngularVelocity(t);
    }

    public Point getAcceleration(double t) {
        return getSegment(t).getAcceleration(t);
    }

    public boolean isOutOfProfile(double t) {
        return t < getTStart() || t > getTEnd();
    }

    @Override
    public String toString() {
        return "MotionProfile2D{" +
                "segments=" + segments +
                '}';
    }

}
