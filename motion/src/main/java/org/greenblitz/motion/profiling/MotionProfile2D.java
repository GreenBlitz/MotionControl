package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey
 */
public class MotionProfile2D {

    private List<Segment2D> segments;

    /**
     * This is package protected on purpose.
     *
     * @param segments
     */
    MotionProfile2D(List<Segment2D> segments) {
        this.segments = segments;
    }

    MotionProfile2D(int capacity){this.segments = new ArrayList<Segment2D>(capacity);}

    MotionProfile2D(MotionProfile1D first, MotionProfile1D second){
        this.segments = new ArrayList<>();
        for (int i = 0; i < first.segments.size(); i++) {
            this.segments.add(new Segment2D(first.segments.get(i), second.segments.get(i), null));
        }
    }

    /**
     * @return The time in which the profile finishes
     */
    public double getTEnd() {
        return segments.get(segments.size()-1).getTEnd();
    }

    /**
     * @param t point in time (in seconds)
     * @return whether or not the profile is finished by that time
     */
    public boolean isOver(double t) {
        return t >= getTEnd();
    }


    private int previous = 0;
    /**
     * copied from MotionProfile1D
     *
     * The idea of this function is that you will never go back in time, therefore
     * after you used some time segment, you won't use all segments before it.
     * In addition because the controller runs decently fast, you are most likely to find
     * the desired segment right after the previous you used.
     * <p>
     * Therefore, this is O(1) average time.
     *
     * @param t point in time (in seconds)
     * @return The segment matching that point of time
     * @throws IndexOutOfBoundsException if the current time doesn't apply to any segment.
     */


    public Segment2D quickGetSegment(double t) {
        for (int i = 0; i < segments.size(); i++) {
            if (segments.get((previous + i) % segments.size()).isTimePartOfSegment(t)) {
                previous = (i + previous) % segments.size();
                return segments.get(previous);
            }
        }
        throw new IndexOutOfBoundsException("No segment with time " + t);
    }


    /**
     * @param t point in time (in seconds)
     * @return the acceleration vector (first acc, second acc) at that time
     */
    public Vector2D getAcceleration(double t) {
        return new Vector2D(quickGetSegment(t).getAccelerationFirst(), quickGetSegment(t).getAccelerationSecond());
    }

    /**
     * @param t point in time (in seconds)
     * @return the velocity vector (first vel, second vel) at that time
     */
    public Vector2D getVelocity(double t) {
        return new Vector2D(quickGetSegment(t).getVelocityFirst(t), quickGetSegment(t).getVelocitySecond(t));
    }


    /**
     *
     * @param index an index of a segment
     * @return startLocation at segment index
     */
    public Position getActualLocation(int index) {
        return segments.get(index).getLocation();
    }


    /**
     * For testing purposes only! Don't use otherwise
     * Package protected in purpose.
     *
     * @param t
     * @param epsilon
     * @return
     */
    Position getActualLocation(double t, double epsilon) {
        return getActualLocation(t, new Position(0, 0, 0), 0, epsilon);
    }

    /**
     * For testing purposes only! Don't use otherwise
     * Package protected in purpose.
     *
     * @param t
     * @param prev
     * @param prevT
     * @param epsilon
     * @return
     */
    Position getActualLocation(double t, Position prev, double prevT, double epsilon) {
        if (prevT > t)
            throw new UnsupportedOperationException();
        Position ret = prev;
        final double dt = epsilon;
        for (double t2 = prevT; t2 < t; t2 += dt) {
            ret = ret.moveBy(quickGetSegment(t).firstSegment.getVelocity(t), quickGetSegment(t).secondSegment.getVelocity(t), dt);
        }
        return ret;
    }

    public Vector2D getLocation(double t){
        MotionProfile1D.Segment first = quickGetSegment(t).firstSegment;
        MotionProfile1D.Segment second = quickGetSegment(t).secondSegment;
        return new Vector2D(first.getLocation(t), second.getLocation(t));
    }

    /**
     * adds segment to end without check
     * @param seg segment to add
     */
    public void unsafeAddSegment(Segment2D seg){segments.add(seg);}

    /**
     * Removes all segments with time length less then a milisecond.
     *
     * @see MotionProfile1D#removeBugSegments()
     */
    public void removeBugSegments() {
        double tStart = segments.get(0).getTStart();
        List<Segment2D> goodSegments = new ArrayList<>();
        for (Segment2D s : segments) {
            if (Math.abs(s.getTEnd() - s.getTStart()) > 0)
                goodSegments.add(s);
        }
        if (goodSegments.size() != 0) {
            goodSegments.get(0).setTStart(tStart);
            for (int i = 1; i < goodSegments.size(); i++)
                goodSegments.get(i).setTStart(goodSegments.get(i - 1).getTEnd());
        }
        segments = goodSegments;
    }

    @Override
    public String toString() {
        StringBuilder firstProfile = new StringBuilder("MotionProfile1D{");
        StringBuilder secondProfile = new StringBuilder("MotionProfile1D{");
        for (Segment2D s : segments) {
            firstProfile.append("\n\t").append(s.firstSegment);
            secondProfile.append("\n\t").append(s.secondSegment);
        }
        return "MotionProfile2D{" +
                "firstProfile=" + firstProfile +
                "\n, secondProfile=" + secondProfile +
                '}';
    }

    public static class Segment2D{
        private MotionProfile1D.Segment firstSegment, secondSegment;
        private Position startLocation;
        private final static double EPSILON = 1E-8;

        public Segment2D(MotionProfile1D.Segment first, MotionProfile1D.Segment second, State startLocation){
            this.firstSegment = first;
            this.secondSegment = second;
            this.startLocation = startLocation;
        }

        public double getTStart(){return firstSegment.getTStart();}

        public void setTStart(double t) {
            firstSegment.setTStart(t);
            secondSegment.setTStart(t);
        }

        public double getTEnd(){return firstSegment.getTEnd();}

        public boolean isTimePartOfSegment(double t) {
            return t - firstSegment.tStart >= -EPSILON && t - firstSegment.tEnd <= EPSILON;
        }

        public double getAccelerationFirst(){return firstSegment.getAccel();}

        public double getAccelerationSecond(){return secondSegment.getAccel();}

        public double getVelocityFirst(double t){return firstSegment.getVelocity(t);}

        public double getVelocitySecond(double t){return secondSegment.getVelocity(t);}

        public Position getLocation() {return startLocation;}


        //might not be important
        /*public State getLocation(double t){

        }*/

    }
}
