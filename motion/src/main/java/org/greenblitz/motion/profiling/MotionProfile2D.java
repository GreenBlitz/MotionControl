package org.greenblitz.motion.profiling;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.utils.LinkedList;

import java.util.List;

/**
 * @author Alexey
 * @author Asaf
 * @author Peleg
 */
public class MotionProfile2D {

    private LinkedList<Segment2D> segments;
    private double tEnd;
    private Position jahanaRelation;


    /**
     * This is package protected on purpose.
     *
     * @param segments
     */
    MotionProfile2D(List<Segment2D> segments) {
        this.segments = new LinkedList<>(segments);
        this.tEnd = segments.get(segments.size()-1).getTEnd();
    }

    MotionProfile2D(){this.segments = new LinkedList<Segment2D>();}

    MotionProfile2D(MotionProfile1D first, MotionProfile1D second){
        this.segments = new LinkedList<>();
        for (int i = 0; i < first.segments.size(); i++) {
            this.segments.add(new Segment2D(first.segments.get(i), second.segments.get(i), null));
        }
        this.tEnd = segments.getLast().getTEnd();
    }

    public LinkedList<Segment2D> getSegments() {
        return segments;
    }

    public void updateTEnd(){
        tEnd = segments.getLast().getTEnd();
    }

    public void updateJahana(){
        if (jahanaRelation == null) {
            Localizer localizer = Localizer.getInstance();
            jahanaRelation = localizer.getLocation().translate(segments.getFirst().getLocation().negate());
        }
    }
    /**
     * @return The time in which the profile finishes
     */
    public double getTEnd() {
        return tEnd;
    }

    /**
     * @param t point in time (in seconds)
     * @return whether or not the profile is finished by that time
     */
    public boolean isOver(double t) {
        return t >= getTEnd();
    }


    private LinkedList.Node<Segment2D> previous;
    private int index;
    private double accumulatedOffset = 0;
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

    public LinkedList.Node<Segment2D> quickGetNode(double t) {
        if (segments.isEmpty()){throw new IndexOutOfBoundsException("List empty");}
        for (int i = 0; i < segments.size(); i++) {
            if (previous == null){
                previous = segments.getNodeFirst();
                accumulatedOffset = 0;
            }
            accumulatedOffset += previous.getItem().profileOffset;
            if (previous.getItem().isTimePartOfSegment(t + accumulatedOffset)) {
                return previous;
            }
            previous = previous.getNext();
            index = (index+1)%segments.size();
        }
        throw new IndexOutOfBoundsException("No segment with time " + t);
    }

    public Segment2D quickGetSegment(double t){return quickGetNode(t).getItem();}

    public int quickGetIndex(double t) {
        quickGetNode(t);
        return index;
    }

    /**
     * since motionProfile2D is now LinkedList this is not useful
     *
     * Uses binary searching. before using this,
     *
     * @param t point in time (in seconds)
     * @return The segment matching that point of time
     * @throws IndexOutOfBoundsException if the current time doesn't apply to any segment.
     * @see MotionProfile2D#quickGetSegment(double)
     * <p>
     * This runs in average time of O(n log n)
     */
    public Segment2D getSegmentRandom(double t) {
        int lower = 0;
        int upper = segments.size() - 1;
        int testing;
        while (true) {
            if (lower == upper)
                if (segments.get(lower).isTimePartOfSegment(t))
                    return segments.get(lower);
                else
                    throw new IndexOutOfBoundsException("No segment with such time");

            testing = (lower + upper) / 2;
            if (segments.get(testing).isTimePartOfSegment(t))
                return segments.get(testing);
            if (segments.get(testing).getTStart() > t)
                upper = testing - 1;
            else
                lower = testing + 1;
        }
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



    public double getStartTime(double t){
        return quickGetSegment(t).firstSegment.getTStart();
    }

    /**
     *
     * @param t time
     * @return position using localizer
     */

    public Position getActualLocation(double t){
        return getRelativeLocation(t).translate(jahanaRelation.negate());
    }

    /**
     *
     * @param t time in a segment
     * @return startLocation at segment index relative to path
     */
    public Position getRelativeLocation(double t) { //TODO make location available in all times
        return quickGetSegment(t).getLocation();
    }


    /**
     * For testing purposes only! Don't use otherwise
     * Package protected in purpose.
     *
     * @param t
     * @param epsilon
     * @return
     */
    Position debugGetActualLocation(double t, double epsilon) {
        return debugGetActualLocation(t, new Position(0, 0, 0), 0, epsilon);
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
    Position debugGetActualLocation(double t, Position prev, double prevT, double epsilon) {
        if (prevT > t)
            throw new UnsupportedOperationException();
        Position ret = prev;
        final double dt = epsilon;
        for (double t2 = prevT; t2 < t; t2 += dt) {
            ret = ret.moveBy(quickGetSegment(t).firstSegment.getVelocity(t), quickGetSegment(t).secondSegment.getVelocity(t), dt);
        }
        return ret;
    }

    /**
     * @param t time
     * @return location in 1d profile of linear and angular profiles
     */

    public Vector2D getLocation1D(double t){
        Segment2D segment = quickGetSegment(t);
        MotionProfile1D.Segment first = segment.firstSegment;
        MotionProfile1D.Segment second = segment.secondSegment;
        return new Vector2D(first.getLocation(t), second.getLocation(t));
    }

    /**
     * adds segment to end without check
     * @param seg segment to add
     */
    public void unsafeAddSegment(Segment2D seg){segments.add(seg);}

    public void unsafeAddSegments(List<Segment2D> segments){this.segments.addAll(segments);}

    public void merge(MotionProfile2D mergedProfile, int startIndexOfMergedProfile, LinkedList.Node<Segment2D> startNodeOfMergedList){
        double offset = startNodeOfMergedList.getItem().getTStart() - this.getTEnd();
        startNodeOfMergedList.getItem().setOffset(offset);
        this.segments.merge(mergedProfile.segments, startIndexOfMergedProfile, startNodeOfMergedList);
        this.tEnd = this.tEnd-offset;

    }

    /**
     * Removes all segments with time length less then a milisecond.
     *
     * @see MotionProfile1D#removeBugSegments()
     */
    public void removeBugSegments() {
        double tStart = segments.get(0).getTStart();
        LinkedList<Segment2D> goodSegments = new LinkedList<>();
        for (Segment2D s : segments) {
            if (Math.abs(s.getTEnd() - s.getTStart()) > 0)
                goodSegments.add(s);
        }
        if (goodSegments.size() != 0) {
            goodSegments.get(0).setTStart(tStart);
            for (LinkedList.Node<Segment2D> curr = goodSegments.getNodeFirst(); curr != null; curr = curr.getNext())
                curr.getItem().setTStart(curr.getPrev().getItem().getTEnd());
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
        private double profileOffset;

        public Segment2D(MotionProfile1D.Segment first, MotionProfile1D.Segment second, State startLocation){
            this.firstSegment = first;
            this.secondSegment = second;
            this.startLocation = startLocation;
            this.profileOffset = 0;
        }

        public double getTStart(){return firstSegment.getTStart();}

        public void setTStart(double t) {
            firstSegment.setTStart(t);
            secondSegment.setTStart(t);
        }

        public void setOffset(double offset){this.profileOffset = offset;}

        public double getTEnd(){return firstSegment.getTEnd();}

        public boolean isTimePartOfSegment(double t) {
            return t - firstSegment.tStart >= -EPSILON && t - firstSegment.tEnd <= EPSILON;
        }

        public double getAccelerationFirst(){return firstSegment.getAccel();}

        public double getAccelerationSecond(){return secondSegment.getAccel();}

        public double getVelocityFirst(double t){return firstSegment.getVelocity(t);}

        public double getVelocitySecond(double t){return secondSegment.getVelocity(t);}

        public Position getLocation() {return startLocation;}

        public State getStateLocation(){
            return (new State(getLocation(), getVelocityFirst(getTStart()), getVelocitySecond(getTStart())));
        }


        //might not be important
        /*public State getLocation(double t){

        }*/

    }
}
