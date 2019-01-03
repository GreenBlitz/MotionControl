import org.greenblitz.motion.base.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathTest {

    static final double EPSILON = 1E-6;

    @Test
    void testSegmentIntersection(){
        double[] intersections = Path.intersections(new Point(5,7), 5, new Point(10,5), new Point(8, 3));
        assertEquals(intersections[0], 1, EPSILON);
        assertEquals(intersections[1], 0.5, EPSILON);
    }

    @Test
    void testPathIntersection(){
        ArrayList list = new ArrayList<Point>();
        list.add(new Point(0, 0));
        list.add(new Point(0, 5));
        list.add(new Point(5, 5));
        list.add(new Point(5, 10));
        Path path = new Path(list);
        Point intersection = path.intersection(new Point(0,10), 5 * Math.sqrt(5) / 2);
        assertEquals(intersection.getX(), 5, EPSILON);
        assertEquals(intersection.getY(), 7.5, EPSILON);
    }
}
