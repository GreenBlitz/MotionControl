package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;

import java.util.ArrayList;

public class MotionProfile2D {

    private final ArrayList<BezierSegment> segments;

    private int lastSegment;
    // TODO: 2/13/2019 test the thing
    public MotionProfile2D (ArrayList<State> positions) {
        this.lastSegment = 0;
        this.segments = new ArrayList<>();
        for (int index = 0; index<positions.size()-1; index++) {
            BezierSegment current = new BezierSegment(positions.get(index), positions.get(index + 1),
                    index, index+1);
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

    @Override
    public String toString() {
        return "MotionProfile2D{" +
                "segments=" + segments +
                '}';
    }

}
