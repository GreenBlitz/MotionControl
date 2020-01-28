package org.greenblitz.motion.base;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    void normalizeTest() {
        double delta = Math.pow(10, -8);
        assertAll("angles",
                () -> assertEquals(Position.normalizeAngle(Math.PI), Math.PI, delta),
                () -> assertEquals(Position.normalizeAngle(Math.PI * 2), 0, delta),
                () -> assertEquals(Position.normalizeAngle(-Math.PI), Math.PI, delta),
                () -> assertEquals(Position.normalizeAngle(-2 * Math.PI), 0, delta),
                () -> assertEquals(Position.normalizeAngle(-0.5 * Math.PI - 8 * Math.PI), -0.5 * Math.PI, delta),
                () -> assertEquals(Position.normalizeAngle(1.3 + Math.PI * 16), 1.3, delta));
    }

    @Test
    void cloneTest() {
        Position p1 = new Position(Math.random(), Math.random(), Math.random());
        assertNotSame(p1, p1.clone());
        assertEquals(p1, p1.clone());
    }

    @Test
    void testWeigtedAvg(){
        Position p1 = new Position(0, 0);
        Position p2 = new Position(2, 2, 2);
        assertEquals(p1.weightedAvg(p2, 0.5), new Position(1, 1, 1));
        assertEquals(p1.weightedAvg(p2, 0.75), new Position(1.5, 1.5, 1.5));
        assertEquals(p1.weightedAvg(p2, 0.25), new Position(0.5, 0.5, 0.5));
        p1 = new Position(1, 0);
        assertEquals(p1.weightedAvg(p2, 0.5), new Position(1.5, 1, 1));
        assertEquals(p1.weightedAvg(p2, 0.75), new Position(1.75, 1.5, 1.5));
        assertEquals(p1.weightedAvg(p2, 0.25), new Position(1.25, 0.5, 0.5));
        p1 = new Position(1, 5, 1);
        assertEquals(p1.weightedAvg(p2, 0.5), new Position(1.5, 3.5, 1.5));
        assertEquals(p1.weightedAvg(p2, 0.75), new Position(1.75, 2.75, 1.75));
        assertEquals(p1.weightedAvg(p2, 0.25), new Position(1.25, 4.25, 1.25));
    }

    @Test
    void frcToMathTest(){
        Position point = new Position(3, 2, Math.PI / 2);
        assertEquals(point.changeCoords(Point.CoordinateSystems.LOCALIZER, Point.CoordinateSystems.MATH),
                new Position(-3, 2, Math.PI));
        point = new Position(-10, 5, Math.PI / 4);
        assertEquals(point.changeCoords(Point.CoordinateSystems.LOCALIZER, Point.CoordinateSystems.MATH),
                new Position(10, 5, Math.PI / 4 + Math.PI / 2));
        point = new Position(2, -4, Math.PI / 4);
        assertEquals(point.changeCoords(Point.CoordinateSystems.LOCALIZER, Point.CoordinateSystems.MATH),
                new Position(-2, -4, Math.PI / 4 + Math.PI / 2));
        point = new Position(2, -4, 3);
        assertEquals(point.changeCoords(Point.CoordinateSystems.LOCALIZER, Point.CoordinateSystems.MATH),
                new Position(-2, -4, 3 + Math.PI / 2));
    }

    @Test
    void mathToFrcTest(){
        Position point = new Position(3, 2, Math.PI / 2);
        assertEquals(point.changeCoords(Point.CoordinateSystems.MATH, Point.CoordinateSystems.LOCALIZER),
                new Position(-3, 2, 0));
        point = new Position(-10, 5, Math.PI / 4);
        assertEquals(point.changeCoords(Point.CoordinateSystems.MATH, Point.CoordinateSystems.LOCALIZER),
                new Position(10, 5, Math.PI / 4 - Math.PI / 2));
        point = new Position(2, -4, Math.PI / 4);
        assertEquals(point.changeCoords(Point.CoordinateSystems.MATH, Point.CoordinateSystems.LOCALIZER),
                new Position(-2, -4, Math.PI / 4 - Math.PI / 2));
        point = new Position(2, -4, 3);
        assertEquals(point.changeCoords(Point.CoordinateSystems.MATH, Point.CoordinateSystems.LOCALIZER),
                new Position(-2, -4, 3 - Math.PI / 2));
    }

}
