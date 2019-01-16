import org.greenblitz.motion.base.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class PointTest {

    private static boolean fuzzyPointEquals(Point actual, Point expected, double fuzz) {
//        System.out.println("Actual: " + actual + "; Expected: " + expected);
        return (Math.abs(actual.getX() - expected.getX()) < fuzz)
                && (Math.abs(actual.getY() - expected.getY()) < fuzz);
    }

    private static Point cis(double ang, double len){
        return new Point(len*Math.sin(ang), len*Math.cos(ang));
    }

    @Test
    void translateTest() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(13, -4);
        p1.translate(p2);
        assertEquals(p1, p2, "translate test");
        p2.translate(-10, 0);
        assertEquals(p1.getY(), p2.getY(), 0.0000001);
        assertEquals(p1.getX() - 10, p2.getX());
        p2.translate(p2);
        assertEquals(6, p2.getX());
        assertEquals(-8, p2.getY());
    }

    @Test
    void cloneTest() {
        Point p1 = new Point(Math.random(), Math.random());
        assertNotSame(p1, p1.clone());
        assertEquals(p1, p1.clone());
    }

    @Test
    void rotateTest() {
        double fuzz = Math.pow(10, -8);
        Point p1 = new Point(1, 1);
        p1.rotate(Math.PI);
        assertTrue(fuzzyPointEquals(p1, new Point(-1, -1), fuzz));
        p1 = new Point(-1, 0);
        p1.rotate(Math.PI / 2);
        assertTrue(fuzzyPointEquals(p1, new Point(0, 1), fuzz));
        p1 = cis(Math.PI / 8, 7);
        p1.rotate(Math.PI / 4);
        assertTrue(fuzzyPointEquals(p1, cis(3*Math.PI / 8, 7), fuzz));
        for (int i = 0; i < 100; i++){
            double ang = Math.random() * 100 * Math.PI;
            double len = Math.random() * 50;
            p1 = cis(ang, len);
            double rot = Math.random() * 100 - 50;
            p1.rotate(rot);
            assertTrue(fuzzyPointEquals(p1, cis(ang + rot, len), fuzz));
        }
    }

}
