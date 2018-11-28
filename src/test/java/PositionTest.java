import org.greenblitz.motion.Position;
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
        assertTrue(p1 != p1.clone());
        assertTrue(p1.equals(p1.clone()));
    }

}
