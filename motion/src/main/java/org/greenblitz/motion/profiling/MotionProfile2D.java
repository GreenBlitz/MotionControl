package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;

import java.util.ArrayList;

public class MotionProfile2D {

    private final ArrayList<BezierSegment> segments;

    private ArrayList<State> realProfile;
    private int lastSegment;

    private MotionProfile2D(ArrayList<BezierSegment> segments) {
        this.segments = segments;
        lastSegment = 0;
    }

    private BezierSegment getSegment(double t) {
        for (int ind = 0; ind < segments.size(); ind++) {
            if (!segments.get((ind + lastSegment) % segments.size()).isTimeOutOfSegment(t)) {
                lastSegment = (lastSegment + ind) % segments.size();
                return segments.get(lastSegment);
            }
        }
        throw new RuntimeException();
    }

    @Override
    public String toString() {
        return "MotionProfile2D{" +
                "segments=" + segments +
                '}';
    }

    public class BezierSegment {

        private Point p1, p2, p3, p4;
        private Point p12, p23, p34;
        private double tStart, tEnd, tSize;
        private final IndexOutOfBoundsException timeException =
                new IndexOutOfBoundsException("Time not in this segment");

        public BezierSegment(Point p1, Point p2, Point p3, Point p4, double tStart, double tEnd) {
            this.tStart = tStart;
            this.tEnd = tEnd;
            tSize = tEnd - tStart;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.p4 = p4;
            p12 = Point.subtract(p2, p1).scale(3);
            p23 = Point.subtract(p3, p2).scale(6);
            p34 = Point.subtract(p4, p3).scale(3);
        }

        public BezierSegment(Point p1, Point p2, Point p3, Point p4, double tStart) {
            this(p1, p2, p3, p4, tStart, tStart+1);
        }
       public BezierSegment(State start, State end, double tStart, double tEnd) {
            this(start,
                    Point.add(start, start.velocity.scale((tEnd-tStart) / 3)),
                    Point.subtract(end, end.velocity.scale((tEnd-tStart) / 3)),
                    end,
                    tStart,
                    tEnd);
        }
        public BezierSegment(State start, State end, double tStart) {
            this(start, end, tStart, tStart+1);
        }

        public boolean isTimeOutOfSegment(double t) {
            return t < tStart || t > tEnd;
        }

        public Point getVelocity(double t) {
            if (isTimeOutOfSegment(t)/*yes*/)
                throw timeException;
            t = (t - tStart) / tSize;
            double tt = 1 - t;
            return Point.add(Point.add(p12.scale(tt * tt), p23.scale(t * tt)), p34.scale(t * t));
        }

        public Point getLocation(double t) {
            if (isTimeOutOfSegment(t))
                throw timeException;
            return Point.bezierSample((t - tStart) / tSize, p1, p2, p3, p4);
        }

        public double getTStart() {
            return tStart;
        }

        public double getTEnd() {
            return tEnd;
        }

        public Point getStartVelocity() {
            return p12;
        }

        public Point getStartLocation() {
            return p1;
        }

        @Override
        public String toString() {
            return "BezierSegment{" +
                    "p1=" + p1 +
                    ", p2=" + p2 +
                    ", p3=" + p3 +
                    ", p4=" + p4 +
                    ", tStart=" + tStart +
                    ", tEnd=" + tEnd +
                    '}';
        }


    }

    public static void main(String[] args) {
        System.out.println("Process finished with exit code 404\n");
        throw new RuntimeException("\nProcess finished with exit code 1\n");
    }
}
