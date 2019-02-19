package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BezierCurveTest {

    final double EPSILON = 1E-7;

    @Test
    void getSubcurveTest(){
        ICurve curve = new BezierCurve(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)));
        double i = 0.3;
        Point location = curve.getLocation(i);
        assertEquals(location.getX(), i, EPSILON);
        assertEquals(location.getY(), i*i*(3 - 2*i), EPSILON);

    }
}
