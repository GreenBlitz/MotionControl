package org.greenblitz.motion;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.BasicAngleInterpolator;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.motion.pathing.PolynomialInterpolator;
import org.junit.jupiter.api.Test;

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
        /*ArrayList list = new ArrayList<Position>();
        list.add(new Position(0, 0));
        list.add(new Position(0, 5));
        list.add(new Position(5, 5));
        list.add(new Position(5, 10));
        Path path = new Path<Position>(list);
        Point intersection = path.getGoalPoint(new Point(7.5,7.5), 2.5 * Math.sqrt(5) / 2);
        assertEquals(intersection.getX(), 5, EPSILON);
        assertEquals(intersection.getY(), 8.75, EPSILON);*/
    }

    @Test
    void linearInterpolationTest1() {
        Path test = new Path<Position>(new Position(0, 0),
                new Position(0, 1));
        test = PolynomialInterpolator.interpolatePoints(test, 10);
        for (int i = 0; i <= 10; i++) {
            assertTrue(test.getPath().get(i).equals(new Position(0, i / 10.0)));
        }
    }

    @Test
    void linearInterpolationTest2() {
        Path test = new Path<Position>(new Position(0, 0, Math.PI / 4),
                new Position(1, 1, Math.PI / 4));
        test = PolynomialInterpolator.interpolatePoints(test, 15);
        for (int i = 0; i <= 15; i++) {
            assertTrue(test.getPath().get(i).equals(new Position(i / 15.0, i / 15.0)));
        }
    }

    @Test
    void linearInterpolationTest3() {
        Path test = new Path<Position>(new Position(1, 1, Math.PI / 4),
                new Position(0, 0, Math.PI / 4));
        test = PolynomialInterpolator.interpolatePoints(test, 15);
        for (int i = 15; i >= 0; i--) {
            assertTrue(test.getPath().get(15 - i).equals(new Position(i / 15.0, i / 15.0)));
        }
    }

    @Test
    void simpleSquaredTest() {
        Path test = new Path<Position>(new Position(1, 1, Math.atan(1.0 / 2)),
                new Position(3, 9, Math.atan(1.0 / 6)));
        test = PolynomialInterpolator.interpolatePoints(test, 15);
        for (int i = 0; i <= 15; i++) {
            assertTrue(test.getPath().get(i).equals(new Position((1 + (2 * i / 15.0)), (1 + (2 * i / 15.0)) * (1 + (2 * i / 15.0)))));
        }
    }

    @Test
    void csvPath() {
        Path test = new Path<Position>(
                new Position(0, 0),
                new Position(1, 0),
                new Position(1, 1),
                new Position(5, 3),
                new Position(4, 7),
                new Position(3, -1)
        );
        test = BasicAngleInterpolator.interpolateAngles(test);
        test = PolynomialInterpolator.interpolatePoints(test, 999);
        test.saveAsCSV("testPath.csv");
    }

    @Test
    void complexCubicTest() {
        for (int k = 0; k < 100; k++) {
            double a = Math.random() * 20 - 10;
            double b = Math.random() * 20 - 10;
            double c = Math.random() * 20 - 10;
            double d = Math.random() * 20 - 10;
            Path<Position> test = new Path<>(new Position(1, a + b + c + d, Math.atan(1.0 / (3 * a + 2 * b + c))),
                    new Position(10, a * Math.pow(100, 3) + b * 100 * 100 + c * 100 + d, Math.atan(1.0 / (3 * 100 * 100 * a + 2 * 100 * b + c))));
            test = PolynomialInterpolator.interpolatePoints(test, 10);
            for (int i = 0; i <= 10; i++) {
                assertTrue(Point.fuzzyEquals(test.getPath().get(i), new Position(
                        1 + (9.0 * i / 10),
                        a * Math.pow(1 + (9.0 * i / 10), 3) + b * Math.pow(1 + (9.0 * i / 10), 2) + c * (1 + (9.0 * i / 10)) + d
                ), 1E-2));
            }
        }
    }
}
