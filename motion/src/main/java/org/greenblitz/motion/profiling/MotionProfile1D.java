package org.greenblitz.motion.profiling;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.exceptions.ProfilingException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexey
 * @author Udi
 */
public class MotionProfile1D {

    /**
     * The segments that make up this profile
     */
    protected List<Segment> segments;

    // Note, I didn't forget access modifiers, it's package protected on purpose. - Alexey

    MotionProfile1D(List<Segment> segs) {
        segments = segs;
    }

    MotionProfile1D(Segment... segs) {
        this();
        segments.addAll(Arrays.asList(segs));
    }

    MotionProfile1D() {
        this(new ArrayList<>());
    }

    MotionProfile1D(int capacity, Segment... segs) {
        this(capacity);
        segments.addAll(Arrays.asList(segs));
    }

    MotionProfile1D(int capacity) {
        this(new ArrayList<>(capacity));
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("MotionProfile1D{");
        for(Segment s: segments)
            ret.append("\n\t").append(s);
        return ret.toString();
    }

    /**
     * @return An ArrayList of copies of the segments. Changing this ArrayList or the Segments within in won't
     * affect the original Profile
     */
    public List<Segment> getSegments() {
        List<Segment> toRet = new ArrayList<>();
        for (Segment seg : segments) {
            toRet.add(seg.clone());
        }
        return toRet;
    }

    /**
     * Adds the given profile to this one, and if needed generates a in-between profile to bridge a potential gap.
     *
     * @see MotionProfile1D#safeAdd(MotionProfile1D)
     * @param second
     * @param maxV
     * @param maxA
     * @param minA
     */
    public void autocompleteAdd(MotionProfile1D second, double maxV, double maxA, double minA) {
        MotionProfile1D first = this;
        if (!Point.isFuzzyEqual(first.getLocation(first.getTEnd()), second.getLocation(0))) {
            List<ActuatorLocation> startAndEnd = new ArrayList<>();
            startAndEnd.add(new ActuatorLocation(first.getLocation(first.getTEnd()), first.getVelocity(first.getTEnd())));
            startAndEnd.add(new ActuatorLocation(second.getLocation(0), second.getVelocity(0)));
            MotionProfile1D inBetweenProfile = Profiler1D.generateProfile(startAndEnd, maxV, maxA, minA);
            safeAdd(inBetweenProfile);
        }
        safeAdd(second);
    }

    /**
     * Adds a profile to the end of this one, preforming needed checks and changing the times to adapt
     * @throws ProfilingException When the end of this profile is not the same as the start of the next one.
     * @param second
     */
    public void safeAdd(MotionProfile1D second) {
        MotionProfile1D first = this;
        if (!Point.isFuzzyEqual(first.getLocation(first.getTEnd()), second.getLocation(0))) {
            throw new ProfilingException("Locations not equal");
        }
        if (!Point.isFuzzyEqual(first.getVelocity(first.getTEnd()), second.getVelocity(0))) {
            throw new ProfilingException("Velocities not equal");
        }

        List<Segment> secondSegs = second.getSegments();

        for (Segment s : secondSegs) {
            s.setTStart(s.getTStart() + getTEnd());
            s.setTEnd(s.getTEnd() + getTEnd());
        }

        segments.addAll(secondSegs);
    }

    /**
     * Will append the segments from second directly to this profile. take care when using this function.
     * @param second
     */
    public void unsafeAdd(MotionProfile1D second){
        segments.addAll(second.getSegments());
    }

    /**
     * Directly adds the given segment to the profile. Use with care.
     * @param seg
     */
    public void unsafeAddSegment(MotionProfile1D.Segment seg){
        segments.add(seg);
    }

    private int previous = 0;

    /**
     * The idea of this function is that you will never go back in time, therefore
     * after you used some time segment, you won't use all segments before it.
     * In addition because the controller runs decently fast, you are most likely to find
     * the desired segment right after the previous you used.
     *
     * Therefore, this is O(1) average time.
     *
     * @see MotionProfile1D#getSegmentRandom(double)
     *
     * @param t point in time (in seconds)
     * @throws IndexOutOfBoundsException if the current time doesn't apply to any segment.
     * @return The segment matching that point of time
     */
    public Segment quickGetSegment(double t) {
        for (int i = 0; i < segments.size(); i++) {
            if (segments.get((previous + i) % segments.size()).isTimePartOfSegment(t)) {
                previous = (i + previous) % segments.size();
                return segments.get(previous);
            }
        }
        throw new IndexOutOfBoundsException("No segment with time " + t);
    }

    /**
     * Uses binary searching. before using this,
     * @see MotionProfile1D#quickGetSegment(double)
     *
     * This runs in average time of O(n log n)
     *
     * @param t point in time (in seconds)
     * @throws IndexOutOfBoundsException if the current time doesn't apply to any segment.
     * @return The segment matching that point of time
     */
    public Segment getSegmentRandom(double t) {
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
            if (segments.get(testing).tStart > t)
                upper = testing - 1;
            else
                lower = testing + 1;
        }
    }

    /**
     * @return The time in which the profile finishes
     */
    public double getTEnd() {
        if (segments.isEmpty())
            return 0;
        return segments.get(segments.size() - 1).tEnd;
    }

    /**
     * @param t point in time (in seconds)
     * @return whether or not the profile is finished by that time
     */
    public boolean isOver(double t) {
        return t >= getTEnd();
    }

    /**
     * @param t point in time (in seconds)
     * @return the acceleration at that time
     */
    public double getAcceleration(double t) {
        return quickGetSegment(t).getAcceleration(t);
    }

    /**
     * @param t point in time (in seconds)
     * @return the velocity at that time
     */
    public double getVelocity(double t) {
        return quickGetSegment(t).getVelocity(t);
    }

    /**
     * @param t point in time (in seconds)
     * @return the location at that time
     */
    public double getLocation(double t) {
        return quickGetSegment(t).getLocation(t);
    }

    /**
     * Removes all segments with time length less then a milisecond.
     */
    public void removeBugSegments() {
        double tStart = segments.get(0).getTStart();
        List<Segment> goodSegments = new ArrayList<>();
        for (Segment s : segments) {
            if (Math.abs(s.tEnd - s.tStart) > 0)
                goodSegments.add(s);
        }
        if (goodSegments.size() != 0) {
            goodSegments.get(0).setTStart(tStart);
            for (int i = 1; i < goodSegments.size(); i++)
                goodSegments.get(i).setTStart(goodSegments.get(i - 1).getTEnd());
        }
        segments = goodSegments;
    }

    /**
     * creates a CSV file holding the current path.
     *
     * @param name name of the file
     * @param dt   time in between points
     * @return true for success and false for failure
     */
    public boolean generateCSV(String name, double dt) {
        CSVPrinter printer;

        try {
            printer = CSVFormat.EXCEL.withHeader(
                    "t",
                    "Location",
                    "Velocity",
                    "Acceleration"
            ).print(new File(name), Charset.defaultCharset());
        } catch (IOException e) {
            return false;
        }

        double t = 0;
        while (!isOver(t)) {
            try {
                printer.printRecord(t, getLocation(t), getVelocity(t), getAcceleration(t));
            } catch (IOException e) {
                return false;
            }
            t += dt;
        }

        return true;
    }

    /**
     * @see MotionProfile1D#generateCSV(String, double)
     * @param name
     * @param samplesPerSecond
     * @return
     */
    public boolean generateCSV(String name, int samplesPerSecond) {
        return generateCSV(name, 1.0 / samplesPerSecond);
    }


    /**
     * Represents a part of a motion profile, in which the acceleration is a constant
     */
    public static class Segment {

        protected double tStart, tEnd, accel, startVelocity, startLocation;
        private final static double EPSILON = 1E-8;

        public Segment(double tStart, double tEnd, double accel, double startVelocity, double startLocation) {
            this.tStart = tStart;
            this.tEnd = tEnd;
            this.accel = accel;
            this.startVelocity = startVelocity;
            this.startLocation = startLocation;
        }

        @Override
        public Segment clone() {
            return new Segment(getTStart(), getTEnd(), getAccel(), getStartVelocity(), getStartLocation());
        }

        /**
         *
         * @param t time in seconds
         * @return whether this time stamp is part of this segment or not
         */
        public boolean isTimePartOfSegment(double t) {
            return t - tStart >= -EPSILON && t - tEnd <= EPSILON;
        }

        /**
         * @throws IndexOutOfBoundsException when the time isn't part of the segment
         * @param t the time in seconds
         * @return the calculated acceleration of the segment at the given time
         */
        public double getAcceleration(double t) {
            if (!isTimePartOfSegment(t))
                throw new IndexOutOfBoundsException("Time not in this segment");
            return accel;
        }

        /**
         * @throws IndexOutOfBoundsException when the time isn't part of the segment
         * @param t the time in seconds
         * @return the calculated velocity of the segment at the given time
         */
        public double getVelocity(double t) {
            if (!isTimePartOfSegment(t))
                throw new IndexOutOfBoundsException("Time not in this segment");
            return startVelocity + (t - tStart) * accel;
        }

        /**
         * @throws IndexOutOfBoundsException when the time isn't part of the segment
         * @param t the time in seconds
         * @return the calculated location of the segment at the given time
         */
        public double getLocation(double t) {
            if (!isTimePartOfSegment(t))
                throw new IndexOutOfBoundsException("Time not in this segment");
            double timePassed = (t - tStart);
            return startLocation + timePassed * startVelocity + 0.5 * timePassed * timePassed * accel;
        }

        public double getTStart() {
            return tStart;
        }

        public void setTStart(double tStart) {
            this.tStart = tStart;
        }

        public double getTEnd() {
            return tEnd;
        }

        public void setTEnd(double tEnd) {
            this.tEnd = tEnd;
        }

        public double getAccel() {
            return accel;
        }

        public void setAccel(double accel) {
            this.accel = accel;
        }

        public double getStartVelocity() {
            return startVelocity;
        }

        public void setStartVelocity(double startVelocity) {
            this.startVelocity = startVelocity;
        }

        public double getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(double startLocation) {
            this.startLocation = startLocation;
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "tStart=" + tStart +
                    ", tEnd=" + tEnd +
                    ", accel=" + accel +
                    ", startVelocity=" + startVelocity +
                    ", startLocation=" + startLocation +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Segment segment = (Segment) o;

            if (Double.compare(segment.tStart, tStart) != 0) return false;
            if (Double.compare(segment.tEnd, tEnd) != 0) return false;
            if (Double.compare(segment.accel, accel) != 0) return false;
            if (Double.compare(segment.startVelocity, startVelocity) != 0) return false;
            return Double.compare(segment.startLocation, startLocation) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(tStart);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(tEnd);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(accel);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(startVelocity);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(startLocation);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

}
