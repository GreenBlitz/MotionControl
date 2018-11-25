import org.greenblitz.motion.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PointTest {

    @Test
    void translateTest() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(13, -4);
        p1.translate(p2);
        assertTrue(p1.equals(p2), "translate test");
        p2.translate(-10, 0);
        assertTrue(p1.getY() == p2.getY());
        assertEquals(p1.getX() - 10, p2.getX());
        p2.translate(p2);
        assertEquals(6, p2.getX());
        assertEquals(-8, p2.getY());
    }

    @Test
    void cloneTest() {
        Point p1 = new Point(Math.random(), Math.random());
        assertTrue(p1 != p1.clone());
        assertTrue(p1.equals(p1.clone()));
    }

}
