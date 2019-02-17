package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StateTest {

    final double EPSILON = 1E-5;

    @Test
    void setAngleTest(){
        State s = new State(0, 0, 0, 3, 0);
        assertEquals(s.getVelocity().getX(), 0, EPSILON);
        assertEquals(s.getVelocity().getY(), 3, EPSILON);
        s.setAngle(Math.PI/2);
        assertEquals(s.getVelocity().getX(), 3, EPSILON);
        assertEquals(s.getVelocity().getY(), 0, EPSILON);
        s.setAngle(-Math.PI/3);
        assertEquals(s.getVelocity().getX(), -Math.sqrt(3)/2*3, EPSILON);
        assertEquals(s.getVelocity().getY(), 1.5, EPSILON);
    }

    @Test
    void setLinearVelocityTest(){
        double x = Math.sqrt(3)/2;
        double y = 0.5;
        State s = new State(0, 0, Math.PI/3, 3, 0);
        assertEquals(s.getVelocity().getX(), 3*x, EPSILON);
        assertEquals(s.getVelocity().getY(), 3*y, EPSILON);
        s.setLinearVelocity(5);
        assertEquals(s.getVelocity().getX(), 5*x, EPSILON);
        assertEquals(s.getVelocity().getY(), 5*y, EPSILON);
        s.setLinearVelocity(-Math.PI);
        assertEquals(s.getVelocity().getX(), -Math.PI*x, EPSILON);
        assertEquals(s.getVelocity().getY(), -Math.PI*y, EPSILON);
    }

    @Test
    void setVelocityTest(){
    }
}
