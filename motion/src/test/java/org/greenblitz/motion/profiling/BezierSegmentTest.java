package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.BezierSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BezierSegmentTest {

    final double EPSILON = 1E-7;
    final double BIG_EPSILON = 1E-2;

    final BezierSegment s1 = new BezierSegment(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)), 0);
    final BezierSegment s2 = new BezierSegment(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(0, -1)), 0);

    @Test
    void getLocationTest() {
        for (double i = 0; i <= 1; i += 0.1) {
            Point p = s1.getLocation(i);
            assertEquals(p.getX(), i, EPSILON);
            assertEquals(p.getY(), i * i * (3 - 2 * i), EPSILON);
        }
        for (double i = 0; i <= 1; i += 0.1) {
            Point p = s2.getLocation(i);
            assertEquals(p.getX(), i + i*i - i*i*i, EPSILON);
            assertEquals(p.getY(), i * i * (4 - 3 * i), EPSILON);
        }
    }

    @Test
    void getVelocityTest() {
        for (double i = 0; i <= 1; i += 0.1) {
            Point p = s1.getVelocity(i);
            assertEquals(p.getX(), 1, EPSILON);
            assertEquals(p.getY(), 6 * i * (1 - i), EPSILON);
        }
        for (double i = 0; i <= 1; i += 0.1) {
            Point p = s2.getVelocity(i);
            assertEquals(p.getX(), 1+ 2*i - 3*i*i, EPSILON);
            assertEquals(p.getY(), 8*i - 9*i*i, EPSILON);
        }
    }

    @Test
    void getAccelerationTest() {
        for (double i = 0; i <= 1; i += 0.1) {
            Point p = s1.getAcceleration(i);
            assertEquals(p.getX(), 0, EPSILON);
            assertEquals(p.getY(), 6 - 12 * i, EPSILON);
        }
        for (double i = 0; i <= 1; i += 0.1) {
            Point p = s2.getAcceleration(i);
            assertEquals(p.getX(), 2 - 6*i, EPSILON);
            assertEquals(p.getY(), 8 - 18*i, EPSILON);
        }
    }

    @Test
    void getCurvatureTest() {
        for (double i = 0; i <= 1; i += 0.1) {
            double curvature = s1.getCurvature(i);
            Point p0 = s1.unsafeGetLocation(i - 0.01),
                    p1 = s1.unsafeGetLocation(i),
                    p2 = s1.unsafeGetLocation(i + 0.01);
            double angle = Point.subtract(p0, p1).toPolarCoords()[1] - Point.subtract(p2, p1).toPolarCoords()[1];
            double estCurvature = -2 * Math.sin(angle) / Point.subtract(p2, p0).norm();
            assertEquals(curvature, estCurvature, BIG_EPSILON);
        }
        for (double i = 0; i <= 1; i += 0.1) {
            double curvature = s2.getCurvature(i);
            Point p0 = s2.unsafeGetLocation(i - 0.0001),
                    p1 = s2.unsafeGetLocation(i),
                    p2 = s2.unsafeGetLocation(i + 0.0001);
            double angle = Point.subtract(p0, p1).toPolarCoords()[1] - Point.subtract(p2, p1).toPolarCoords()[1];
            double estCurvature = -2 * Math.sin(angle) / Point.subtract(p2, p0).norm();
            assertEquals(curvature, estCurvature, BIG_EPSILON);
        }
    }

    @Test
    void angularVelocityTest() {
        for (double i = 0; i <= 1; i += 0.1) {
            double angVel = s1.getAngularVelocity(i);
            double lVelocity = s1.getVelocity(i).norm();
            Point p0 = s1.unsafeGetLocation(i - 0.01),
                    p1 = s1.unsafeGetLocation(i),
                    p2 = s1.unsafeGetLocation(i + 0.01);
            double angle = Point.subtract(p0, p1).toPolarCoords()[1] - Point.subtract(p2, p1).toPolarCoords()[1];
            double estCurvature = -2 * Math.sin(angle) / Point.subtract(p2, p0).norm();
            assertEquals(angVel, estCurvature * lVelocity, BIG_EPSILON);
        }
        for (double i = 0; i <= 1; i += 0.1) {
            double angVel = s2.getAngularVelocity(i);
            double lVelocity = s2.getVelocity(i).norm();
            Point p0 = s2.unsafeGetLocation(i - 0.00001),
                    p1 = s2.unsafeGetLocation(i),
                    p2 = s2.unsafeGetLocation(i + 0.00001);
            double angle = Point.subtract(p0, p1).toPolarCoords()[1] - Point.subtract(p2, p1).toPolarCoords()[1];
            double estCurvature = -2 * Math.sin(angle) / Point.subtract(p2, p0).norm();
            assertEquals(angVel, estCurvature * lVelocity, BIG_EPSILON);
        }
    }
}