package org.greenblitz.motion;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.BasicAngleInterpolator;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.motion.pathing.PolynomialInterpolator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathTest {

    static final double EPSILON = 1E-6;

    @Test
    void testSegmentIntersection() {
        double[] intersections = Path.intersections(new Point(5, 7), 5, new Point(10, 5), new Point(8, 3));
        assertEquals(intersections[0], 0.75, EPSILON);
        assertEquals(intersections[1], 1, EPSILON);
        assertEquals(intersections[2], 0.5, EPSILON);
    }

    @Test
    void testPathIntersection() {
        // TODO
    }

    // TODO autocompleteAdd interpolation tests

}
