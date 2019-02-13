package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;

import java.util.ArrayList;

public class MotionProfile2D {

    private final ArrayList<BezierSegment> segments;

    private ArrayList<State> realProfile;
    private int lastSegment;

    private MotionProfile2D(ArrayList<BezierSegment> segments) {
        this.segments = segments;
        lastSegment = 0;
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

    @Override
    public String toString() {
        return "MotionProfile2D{" +
                "segments=" + segments +
                '}';
    }

}
