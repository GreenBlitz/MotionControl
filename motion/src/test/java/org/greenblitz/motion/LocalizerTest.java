package org.greenblitz.motion;

import org.greenblitz.motion.app.Localizer;
import org.greenblitz.motion.base.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LocalizerTest {

    static final double EPSILON = 1E-6;


    @Test
    void calculateDiffForwardTest() {
        Point diff = Localizer.calculateMovement(3, 3, 5, 0);
        assertEquals(diff.getX(), 0, EPSILON);
        assertEquals(diff.getY(), 3, EPSILON);
    }

    @Test
    void calculateDiffRotateTest() {
        Point diff = Localizer.calculateMovement(3, -3, 5, 0);
        assertEquals(diff.getX(), 0, EPSILON);
        assertEquals(diff.getY(), 0, EPSILON);
    }

    @Test
    void calculateDiffGalgalTsir1Test() {
        Point diff = Localizer.calculateMovement(7 * (Math.PI / 2), 0, 7, 0);
        assertEquals(diff.getX(), 3.5, EPSILON);
        assertEquals(diff.getY(), 3.5, EPSILON);
    }

    @Test
    void calculateDiffGalgalTsir2Test() {
        Point diff = Localizer.calculateMovement(0, 9 * (Math.PI / 6), 9, 0);
        assertEquals(diff.getX(), -4.5 * (1 - (Math.sqrt(3) / 2)), EPSILON);
        assertEquals(diff.getY(), 4.5 * 0.5, EPSILON);
    }

    @Test
    void calculateDiffNotGalgalTzirTest() {
        Point diff = Localizer.calculateMovement((3.7 - 2.5) * (-Math.PI / 3), 2.5 * (Math.PI / 3), 3.7, 0);
        assertEquals(diff.getX(), -(2.5 - (3.7 / 2)) * 0.5, EPSILON);
        assertEquals(diff.getY(), (2.5 - (3.7 / 2)) * (Math.sqrt(3) / 2), EPSILON);
    }

    //not finished
    @Test
    void calculateDiffAngleTest() {
        Point diff = Localizer.calculateMovement((8.5 - 4.4) * (-Math.PI / 3), 4.4 * (Math.PI / 3), 8.5, Math.PI * 2 / 3);
        assertEquals(diff.getX(), (4.4 - (8.5 / 2)) * 1, EPSILON);
        assertEquals(diff.getY(), (4.4 - (8.5 / 2)) * 0, EPSILON);
    }

}