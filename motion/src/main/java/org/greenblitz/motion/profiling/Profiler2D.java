package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

public class Profiler2D {

    public class BezierSegment {

        private Point p1, p2, p3, p4;
        private double tStart, tEnd;
        private final IndexOutOfBoundsException timeException =
                new IndexOutOfBoundsException("Time not in this segment");

        public BezierSegment(Point p1, Point p2, Point p3, Point p4, double tStart, double tEnd) {
            this.tStart = tStart;
            this.tEnd = tEnd;
            this.p1=p1;
            this.p2=p2;
            this.p3=p3;
            this.p4=p4;
        }

        public BezierSegment(State start, State end, double tStart, double tEnd){
            this(start,
                    Point.add(start, start.velocity.scale(1/3)),
                    Point.subtract(end, end.velocity.scale(1/3)),
                    end,
                    tStart,
                    tEnd);
        }

        public boolean isTimePartOfSegment(double t) {
            return t >= tStart && t <= tEnd;
        }

        public double getVelocity(double t) {
            if (!isTimePartOfSegment(t))
                throw timeException;
            return startVelocity + (t - tStart) * accel;
        }

        public double getLocation(double t) {
            if (!isTimePartOfSegment(t))
                throw timeException;
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
            return "BezierSegment{" +
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

            BezierSegment bezierSegment = (BezierSegment) o;

            if (Double.compare(bezierSegment.tStart, tStart) != 0) return false;
            if (Double.compare(bezierSegment.tEnd, tEnd) != 0) return false;
            if (Double.compare(bezierSegment.accel, accel) != 0) return false;
            if (Double.compare(bezierSegment.startVelocity, startVelocity) != 0) return false;
            return Double.compare(bezierSegment.startLocation, startLocation) == 0;
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
