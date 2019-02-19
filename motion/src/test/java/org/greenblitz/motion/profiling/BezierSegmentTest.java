package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.BezierSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierSegmentTest {

    final double EPSILON = 1E-7;
    final double BIG_EPSILON = 1E-2;

    @Test
    void getLocationTest(){
        BezierSegment s = new BezierSegment(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)), 0);
        for(double i=0; i<=1; i+=0.1){
            Point p = s.getLocation(i);
            assertEquals(p.getX(), i, EPSILON);
            assertEquals(p.getY(), i*i*(3 - 2*i), EPSILON);
        }
    }

    @Test
    void getVelocityTest() {
        BezierSegment s = new BezierSegment(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)), 0);
        for(double i=0; i<=1; i+=0.1){
            Point p = s.getVelocity(i);
            assertEquals(p.getX(), 1, EPSILON);
            assertEquals(p.getY(), 6*i*(1-i), EPSILON);
        }
    }

    @Test
    void getAccelerationTest() {
        BezierSegment s = new BezierSegment(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)), 0);
        for(double i=0; i<=1; i+=0.1){
            Point p = s.getAcceleration(i);
            assertEquals(p.getX(), 0, EPSILON);
            assertEquals(p.getY(), 6 - 12*i, EPSILON);
        }
    }

    @Test
    void getCurvatureTest() {
        BezierSegment s = new BezierSegment(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)), 0);
        for(double i=0; i<=1; i+=0.1){
            double curvature = s.getCurvature(i);
            Point p0 = s.unsafeGetLocation(i-0.01),
                p1 = s.unsafeGetLocation(i),
                p2 = s.unsafeGetLocation(i+0.01);
            double angle = Point.subtract(p0,p1).toPolarCoords()[1] - Point.subtract(p2,p1).toPolarCoords()[1];
            double estCurvature = 2*Math.sin(angle)/Point.subtract(p2,p0).norm();
            assertEquals(curvature, estCurvature, BIG_EPSILON);
        }
    }

    @Test
    void angularVelocityTest(){
        BezierSegment s = new BezierSegment(new State(0, 0, new Vector2D(1, 0)), new State(1, 1, new Vector2D(1, 0)), 0);
        for(double i=0; i<=1; i+=0.1){
            double angVel = s.getAngularVelocity(i);
            double lVelocity = s.getVelocity(i).norm();
            Point p0 = s.unsafeGetLocation(i-0.01),
                    p1 = s.unsafeGetLocation(i),
                    p2 = s.unsafeGetLocation(i+0.01);
            double angle = Point.subtract(p0,p1).toPolarCoords()[1] - Point.subtract(p2,p1).toPolarCoords()[1];
            double estCurvature = 2*Math.sin(angle)/Point.subtract(p2,p0).norm();
            assertEquals(angVel, estCurvature*lVelocity, BIG_EPSILON);
        }
    }
}