package org.greenblitz.motion.profiling;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.utils.HowToCloneAGenericInJava101;
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

    public Position getJahanaRelation() {
        return jahanaRelation;
    }

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
            System.out.println(segments.getFirst());
            jahanaRelation = Localizer.getInstance().getLocation().translate(segments.getFirst().getStartLocation().negate());
        }
    }

    public void setJahanaRelation(Position jahanaRelation){
        this.jahanaRelation = jahanaRelation;
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
     *
     * The idea of this function is that you are unlikely to go back in time, therefore
     * after you used some time segment, you won't use all segments before it.
     * In addition because the controller runs decently fast, you are most likely to find
     * the desired segment right after the previous you used.
     * <p>
     * Therefore, this is O(1) average time.
     *
     * when combining two profiles the time of each segment is not matching and to save runtime we dont want to run through each segment
     * instead we add a field offset to the segments and add offset to the merge segment 
     * @see #merge the offset is calculated so when going over the segments in this method the we add all of the offset that we have gone through and we get the correct time.
     * @param t point in time (in seconds)
     * @return The segment matching that point of time
     * @throws IndexOutOfBoundsException if the current time doesn't apply to any segment.
     */

    public LinkedList.Node<Segment2D> quickGetNode(double t) {
        if (segments.isEmpty()){throw new IndexOutOfBoundsException("List empty");}
        for (int i = 0; i <= segments.size(); i++) { //TODO: remove <= (should be <)
            if (previous == null){
                previous = segments.getNodeFirst();
                accumulatedOffset = 0;
            }
            accumulatedOffset += previous.getItem().profileOffset;
            if (previous.getItem().isTimePartOfSegment(t + accumulatedOffset)) {
                accumulatedOffset -= previous.getItem().profileOffset;
                return previous;
            }
            previous = previous.getNext();
            index = (index+1)%segments.size();
        }
        System.out.println(this);
        System.out.println(accumulatedOffset);
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
        return getRelativeLocation(t).translate(jahanaRelation); //TODO test if negate or not
    }

    /**
     *
     * @param t time in a segment
     * @return startLocation at segment index relative to path
     */
    public Position getRelativeLocation(double t) { //TODO make location available in all times
        return quickGetSegment(t).getStartLocation();
    }

    public State getStateLocation(double t){
        State relative = quickGetSegment(t).getStateLocation(t);
        if(jahanaRelation == null){
            System.out.println("jahanaRelation is null in getStateLocation");
        }
        return new State(relative.translate(jahanaRelation), relative.getLinearVelocity(), relative.getAngularVelocity());
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

    /**
     * adds an additional profile in the end of this profile starting in a specific segment of the added profile
     * without disrupting the timings using offset
     * @see #quickGetNode
     * @param mergedProfile the new profile to be added
     * @param mergeNodeIndex the index at which you connect to the profile
     * @param mergeNode the linked list node that houses the segment you want to connect to, the node is given to reduce time
     */
    public void merge(MotionProfile2D mergedProfile, int mergeNodeIndex, LinkedList.Node<Segment2D> mergeNode){
        LinkedList.Node<Segment2D> cloned = mergeNode.clone();
        double offset = cloned.getItem().getTStart() - this.tEnd;
        cloned.getItem().setOffset(offset);
        this.segments.merge(mergedProfile.segments, mergeNodeIndex, cloned);
        this.tEnd = mergedProfile.getTEnd()-offset;

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
        return "MotionProfile2D:\n" + segments;
    }

    public static class Segment2D  extends HowToCloneAGenericInJava101 {
        private MotionProfile1D.Segment firstSegment, secondSegment;
        private Position startLocation;
        private final static double EPSILON = 1E-8;
        private double profileOffset;

        public Segment2D(MotionProfile1D.Segment first, MotionProfile1D.Segment second, Position startLocation){
            this.firstSegment = first;
            this.secondSegment = second;
            this.startLocation = startLocation;
            this.profileOffset = 0;
        }

        public Segment2D(MotionProfile1D.Segment first, MotionProfile1D.Segment second, Position startLocation, double offset){
            this.firstSegment = first;
            this.secondSegment = second;
            this.startLocation = startLocation;
            this.profileOffset = offset;
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

        private Position getStartLocation() {return startLocation;}

        /**
         * location is relative use carefully
         * also ignores curvature change and uses avg angle
         * @return state of start of segment relative to profile
         */
         State getStateLocation(double t){
             double dt = t-getTStart();
             Point startPoint = startLocation;
             double angS = startLocation.getAngle();
             double angE = angS + getVelocitySecond(getTStart())*(dt) + 0.5*getAccelerationSecond()*(dt)*(dt);
             double avgAng = (angE+angS)/2;
             double dist = dt*getVelocityFirst(getTStart())*dt + 0.5*getAccelerationFirst()*(dt)*(dt);
             return new State(startPoint.translate(dist*Math.cos(avgAng), dist*Math.sin(avgAng)), angE, getVelocityFirst(t), getVelocitySecond(t));

        }

        @Override
        public String toString(){
            return "Linear: " + firstSegment.toString() +
                    "Angular: " + secondSegment.toString() +
                    "Position: " + startLocation.toStringRadians() +
                    "Offset: " + profileOffset + "\n";
        }

        //might not be important
        /*public State getLocation(double t){

        }*/

        @Override
        public Segment2D clone(){
            return new Segment2D(firstSegment.clone(), secondSegment.clone(), startLocation.clone(), profileOffset);
        }

    }
}
