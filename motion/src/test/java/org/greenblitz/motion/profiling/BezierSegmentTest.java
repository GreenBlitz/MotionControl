package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.BezierSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierSegmentTest {

    @Test
    void getVelocity() {
    }

    @Test
    void getAcceleration() {

    }

    @Test
    void getCurvature() {
        var b1 = new BezierSegment(new State(0,0,new Vector2D(0,1)),
                new State(0,1,new Vector2D(0,1)), 0,1);

        assertEquals(0.0, b1.getCurvature(0.5), 10E-6);
    }
}