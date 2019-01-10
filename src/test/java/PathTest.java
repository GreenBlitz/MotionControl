import org.greenblitz.motion.app.Path;
import org.greenblitz.motion.base.*;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

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
        Point intersection = path.getGoalPoint(new Point(0,10), 5 * Math.sqrt(5) / 2);
        assertEquals(intersection.getX(), 5, EPSILON);
        assertEquals(intersection.getY(), 7.5, EPSILON);
    }

    @Test
    void linearInterpolationTest1(){
        Path test = new Path(new Position(0,0),
                new Position(0, 1));
        test.interpolatePoints(10);
        for (int i = 0; i <= 10; i++){
            assertTrue(test.getPath().get(i).equals(new Position(0, i/10.0)));
        }
    }

    @Test
    void linearInterpolationTest2(){
        Path test = new Path(new Position(0,0, Math.PI/4),
                new Position(1, 1, Math.PI/4));
        test.interpolatePoints(15);
        for (int i = 0; i <= 15; i++){
            assertTrue(test.getPath().get(i).equals(new Position(i/15.0, i/15.0)));
        }
    }

    @Test
    void linearInterpolationTest3(){
        Path test = new Path(new Position(1,1, Math.PI/4),
                new Position(0, 0, Math.PI/4));
        test.interpolatePoints(15);
        for (int i = 15; i >= 0; i--){
            assertTrue(test.getPath().get(15 - i).equals(new Position(i/15.0, i/15.0)));
        }
    }

    @Test
    void simpleSquaredTest(){
        Path test = new Path(new Position(1,1, Math.atan(2)),
                new Position(3, 9, Math.atan(6)));
        test.interpolatePoints(15);
        for (int i = 0; i <= 15; i++){
            assertTrue(test.getPath().get(i).equals(new Position((1 + (2*i/15.0)),(1 + (2*i/15.0))*(1 + (2*i/15.0)))));
        }
    }

    @Test
    void complexCubicTest(){
        for (int k = 0; k < 100; k++){
            double a = Math.random()*100 - 50;
            double b = Math.random()*100 - 50;
            double c = Math.random()*100 - 50;
            double d = Math.random()*100 - 50;
            Path test = new Path(new Position(1, a+b+c+d, Math.atan(3*a + 2*b + c)),
                    new Position(100, a*Math.pow(100, 3) + b*100*100 + c*100 + d, Math.atan(3*100*100*a + 2*100*b + c)));
            test.interpolatePoints(100);
            for (int i = 0; i <= 100; i++){
                assertTrue(Point.fuzzyEquals(test.getPath().get(i), new Position(
                        1 + (99.0*i/100.0),
                        a*Math.pow(1 + (99.0*i/100.0), 3) + b*Math.pow(1 + (99.0*i/100.0), 2) + c*(1 + (99.0*i/100.0)) + d
                ), 10E-2*0.5));
            }
        }
    }
}
