package org.greenblitz.motion;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;


public class PointTest {

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
        double fuzz = 1E-8;
        Point p1 = new Point(1, 1);
        p1.rotate(Math.PI);
        assertTrue(Point.fuzzyEquals(p1, new Point(-1, -1), fuzz));
        p1 = new Point(-1, 0);
        p1.rotate(Math.PI / 2);
        assertTrue(Point.fuzzyEquals(p1, new Point(0, 1), fuzz));
        p1 = Point.cis(Math.PI / 8, 7);
        p1.rotate(Math.PI / 4);
        assertTrue(Point.fuzzyEquals(p1, Point.cis(3*Math.PI / 8, 7), fuzz));
        for (int i = 0; i < 100; i++){
            double ang = Math.random() * 100 * Math.PI;
            double len = Math.random() * 50;
            p1 = Point.cis(ang, len);
            double rot = Math.random() * 100 - 50;
            p1.rotate(rot);
            assertTrue(Point.fuzzyEquals(p1, Point.cis(ang + rot, len), fuzz));
        }
    }

    @Test
    void bezierTest(){
        double fuzz = 1E-8;
        Point first = new Point(3,5);
        assertTrue(Point.fuzzyEquals(Point.bezierSample(0.3, first), first, fuzz));
        Point second = new Point(7,3);
        assertTrue(Point.fuzzyEquals(Point.bezierSample(0.3, first, second), new Point(3 + 0.3*(7-3), 5 + 0.3*(3-5)), fuzz));
        Point third = new Point(6,7);
        assertTrue(Point.fuzzyEquals(Point.bezierSample(0.3, second, third), new Point(7 + 0.3*(6-7), 3 + 0.3*(7-3)), fuzz));
        Point fs = new Point(first.getX() + 0.3*(second.getX()-first.getX()), first.getY() + 0.3*(second.getY()-first.getY()));
        Point st = new Point(second.getX() + 0.3*(third.getX()-second.getX()), second.getY() + 0.3*(third.getY()-second.getY()));
        Point res = new Point(fs.getX() + 0.3*(st.getX()-fs.getX()), fs.getY() + 0.3*(st.getY()-fs.getY()));
        assertTrue(Point.fuzzyEquals(Point.bezierSample(0.3, Point.bezierSample(0.3, first, second), Point.bezierSample(0.3, second, third)), res, fuzz));
        assertTrue(Point.fuzzyEquals(Point.bezierSample(0.3, first, second, third), res, fuzz));
        Point forth = new Point(0, 10);

        try {
            first = new Point(0,0);
            second = new Point(0,1);
            third = new Point(-1, 1);
            forth = new Point(0, 1);
            File f = new File("filename.csv");
            CSVPrinter p = CSVFormat.EXCEL.withHeader("x", "y").print(f, Charset.defaultCharset());
            Point print;
            for(double i = 0; i<=1+1E-5; i+=0.02){
                print = Point.bezierSample(i, first, second, third, forth);
                p.printRecord(print.getX(), print.getY());
                System.out.println(print);
            }
            p.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
