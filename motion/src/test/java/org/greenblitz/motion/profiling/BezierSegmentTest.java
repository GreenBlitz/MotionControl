package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.BezierSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierSegmentTest {

    final double EPSILON = 1E-7;

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
            Point p0 = s.getLocation(i-0.1),
                p1 = s.getLocation(i),
                p2 = s.getLocation(i+0.1);

            assertEquals(p.getX(), 0, EPSILON);
            assertEquals(p.getY(), 6 - 12*i, EPSILON);
        }
    }

    @Test
    void angularVelocityTest(){

    }
}