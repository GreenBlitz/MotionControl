package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BezierCurveTest {

    final double EPSILON = 1E-7;

    final BezierCurve baseCurve = new BezierCurve(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)));

    @Test
    void getSubCurveTest() {
        ICurve curve = baseCurve;
        double i = 0.3;
        double i2 = 0.3;
        Point location = curve.getLocation(i);
        assertEquals(location.getX(), i2, EPSILON);
        assertEquals(location.getY(), i2 * i2 * (3 - 2 * i2), EPSILON);

        curve = curve.getSubCurve(0.5, 0.75);
        i2 = 0.575;
        location = curve.getLocation(i);
        assertEquals(location.getX(), i2, EPSILON);
        assertEquals(location.getY(), i2 * i2 * (3 - 2 * i2), EPSILON);

        curve = curve.getSubCurve(0.4, 0.7);
        i2 = 0.5 + (0.4 + (i * (0.7 - 0.4))) * (0.75 - 0.5);
        location = curve.getLocation(i);
        assertEquals(location.getX(), i2, EPSILON);
        assertEquals(location.getY(), i2 * i2 * (3 - 2 * i2), EPSILON);

    }

    @Test
    void getLengthTest(){

    }
}
