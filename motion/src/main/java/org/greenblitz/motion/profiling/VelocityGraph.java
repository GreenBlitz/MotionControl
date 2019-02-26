package org.greenblitz.motion.profiling;

import org.greenblitz.motion.profiling.MotionProfile1D.Segment;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.utils.CSVWrapper;

import java.util.ArrayList;
import java.util.List;

public class VelocityGraph {

    private static double maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc;
    private static final double EPSILON = 1E-2;

    public void initialize(double maxLinearVel, double maxAngularVel,
                           double maxLinearAcc, double maxAngularAcc) {
        VelocityGraph.maxLinearVel = maxLinearVel;
        VelocityGraph.maxAngularVel = maxAngularVel;
        VelocityGraph.maxLinearAcc = maxLinearAcc;
        VelocityGraph.maxAngularAcc = maxAngularAcc;
    }

    private List<VelocityGraphRange> m_ranges;
    private int previous;
    public final double length;

    @Deprecated // testing purposes only
    public VelocityGraph(double maxLinearVel, double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        previous = 0;
        initialize(maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc);
        length = 0;
    }

    public VelocityGraph(List<ICurve> track, double maxLinearVel,
                         double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        previous = 0;
        initialize(maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc);
        double tmpLength = 0;


        m_ranges = new ArrayList<>();
        m_ranges.add(new VelocityGraphRange(Double.NEGATIVE_INFINITY, 0));
        for (ICurve curve : track) {
            m_ranges.add(new VelocityGraphRange(
                    m_ranges.get(m_ranges.size() - 1).dEnd,
                    m_ranges.get(m_ranges.size() - 1).dEnd + curve.getLength(1),
                    curve.getCurvature(), curve));
            tmpLength += m_ranges.get(m_ranges.size() - 1).dEnd - m_ranges.get(m_ranges.size() - 1).dStart;
        }
        m_ranges.add(new VelocityGraphRange(m_ranges.get(m_ranges.size() - 1).dEnd, Double.POSITIVE_INFINITY));

//        for(int ind=1; ind<m_ranges.size(); ind++){
//            file.addValues(m_ranges.get(ind).dStart, m_ranges.get(ind).getStartVelocity(), m_ranges.get(ind).getAcceleration(m_ranges.get(ind).dStart));
//        }


        for (int ind = 1; ind < m_ranges.size(); ind++)
            m_ranges.get(ind).concatBackwards(m_ranges.get(ind - 1));
//        for(int ind=1; ind<m_ranges.size(); ind++){
//            file.addValues(m_ranges.get(ind).dStart, m_ranges.get(ind).getStartVelocity(), m_ranges.get(ind).getAcceleration(m_ranges.get(ind).dStart));
//        }

        for (int ind = m_ranges.size() - 2; ind >= 0; ind--)
            m_ranges.get(ind).concatForwards(m_ranges.get(ind + 1));

        generateCSV("velocityByDistance.csv");

        length = tmpLength;
    }

    @Deprecated // testing purposes only
    public VelocityGraphRange makeRange(double length, double curvature, ICurve curve) {
        return new VelocityGraphRange(0, length, curvature, curve);
    }

    @Deprecated // testing purposes only
    public VelocityGraphRange makeRange(double ds, double length, double curvature, ICurve curve) {
        return new VelocityGraphRange(ds, ds + length, curvature, curve);
    }

    public double getLength() {
        return length;
    }

    private VelocityGraphRange quickGetRange(double dist) {
        for (int i = 0; i < m_ranges.size(); i++) {
            if (m_ranges.get((previous + i) % m_ranges.size()).isPartOfRange(dist)) {
                previous = (i + previous) % m_ranges.size();
                return m_ranges.get(previous);
            }
        }
        throw new IndexOutOfBoundsException("No segment with distance " + dist);
    }

    public double getVelocity(double dist, boolean print) {
        return quickGetRange(dist).getVelocity(dist, print);
    }

    public double getVelocity(double dist) {
        return quickGetRange(dist).getVelocity(dist);
    }

    public double getStartVelocity(int ind, boolean print) {
        return m_ranges.get(ind + 1).getStartVelocity(print);
    }

    public double getEndVelocity(int ind, boolean print) {
        return m_ranges.get(ind + 1).getEndVelocity(print);
    }

    public double getAcceleration(double dist) {
        return quickGetRange(dist).getAcceleration(dist);
    }

    public double getCurvature(int ind) {
        return m_ranges.get(ind).curve == null ? Double.NaN : m_ranges.get(ind).curve.getCurvature();
    }

    public void generateCSV(String name) {
        CSVWrapper file = CSVWrapper.generateWrapper(name, 0, "d", "velocity", "acceleration", "mode");
        for (VelocityGraphRange range : m_ranges)
            range.insertToCSV(file);
        file.flush();
    }

    public MotionProfile1D generateProfile(int ind, double tStart) {
        if(ind<0 || ind>m_ranges.size()-2)
            throw new IllegalArgumentException("cannot generate profile for a degenerate case");
        return m_ranges.get(ind + 1).generateProfile(tStart);
    }

    @Override
    public String toString() {
        return "VelocityGraph{" +
                "length=" + length +
                ", m_ranges=" + m_ranges +
                '}';
    }

    public class VelocityGraphRange {

        public final double dStart, dEnd,
                maxVelocity, maxAcceleration;
        private VelocitySegment speedup = null, slowdown = null, inertia, linear = null;
        public final ICurve curve;

        public VelocityGraphRange(double dStart, double dEnd, double curvature, ICurve curve) {
            this.dStart = dStart;
            this.dEnd = dEnd;
            this.curve = curve;
            maxVelocity = ChassisProfiler2D.getMaxVelocity(maxLinearVel, maxAngularVel, curvature);
            maxAcceleration = ChassisProfiler2D.getMaxAcceleration(maxLinearAcc, maxAngularAcc, curvature);
            inertia = new VelocitySegment(maxVelocity, AccelerationMode.INERTIA);
        }

        public VelocityGraphRange(double dStart, double dEnd) {
            this.dStart = dStart;
            this.dEnd = dEnd;
            this.curve = null;
            this.maxVelocity = 0;
            this.maxAcceleration = 0;
            inertia = new VelocitySegment(0, AccelerationMode.INERTIA);
        }

        @Deprecated // testing purposes only
        public VelocitySegment makeSegment(double velocity, AccelerationMode mode) {
            return new VelocitySegment(velocity, mode);
        }

        public boolean isPartOfRange(double dist) {
            return dist >= dStart && dist <= dEnd;
        }

        public double getVelocity(double dist, boolean print) {
            if (print) {
                System.out.println("print");
                System.out.println("acc=" + getAcceleration(dist));
                System.out.println(dist);
            }
            return getVelocity(dist);
        }

        public double getVelocity(double dist) {
            return getActiveRange(dist).getVelocity(dist);
        }

        public double getStartVelocity(boolean print) {
            if (print) {
                System.out.println("acc=" + getAcceleration(dStart));
                System.out.println(dStart);
            }
            return getStartVelocity();
        }

        public double getStartVelocity() {
            return getActiveRange(dStart).getStartVelocity();
        }

        public double getEndVelocity(boolean print) {
            if (print) {
                System.out.println("acc=" + getAcceleration(dEnd));
                System.out.println(dEnd);
            }
            return getEndVelocity();
        }

        public double getEndVelocity() {
            return getActiveRange(dEnd).getEndVelocity();
        }

        public void concatBackwards(VelocityGraphRange other) {
            speedup = new VelocitySegment(other.getEndVelocity(), AccelerationMode.SPEED_UP);
            if (slowdown != null)
                linear = new VelocitySegment(getStartVelocity(), getEndVelocity());
        }

        public void concatForwards(VelocityGraphRange other) {
            slowdown = new VelocitySegment(other.getStartVelocity(), AccelerationMode.SLOW_DOWN);
            if (speedup != null)
                linear = new VelocitySegment(Math.min(speedup.startVelocity, slowdown.startVelocity), Math.min(speedup.endVelocity, slowdown.endVelocity));
        }

        public double getAcceleration(double dist) {
            return getActiveRange(dist).getAcceleration();
        }

        public double getRatio(double dist) {
            return getActiveRange(dist).getRatio();
        }

        public boolean isLinear() {
            return dEnd - dStart < EPSILON && linear != null;
        }

        private VelocitySegment getActiveRange(double dist) {
            if (isLinear()) return linear;
            VelocitySegment min = inertia;
            if (speedup != null && min.getVelocity(dist) > speedup.getVelocity(dist))
                min = speedup;
            if (slowdown != null && min.getVelocity(dist) > slowdown.getVelocity(dist))
                min = slowdown;
            return min;
        }

        public void insertToCSV(CSVWrapper file) {
            if (dStart == Double.NEGATIVE_INFINITY || dEnd == Double.POSITIVE_INFINITY) {
                file.addValues(dStart, getStartVelocity(), getAcceleration(dStart), getRatio(dStart));
                return;
            }
            for (double d = dStart; d < dEnd; d += 0.0001)
                file.addValues(d, getVelocity(d), getAcceleration(d), getRatio(d));
        }

        @Override
        public String toString() {
            return "VelocityRange{" +
                    "dStart=" + dStart +
                    ", dEnd=" + dEnd +
                    ", maxVelocity=" + maxVelocity +
                    ", maxAcceleration=" + maxAcceleration +
                    ", speedup=" + speedup +
                    ", slowdown=" + slowdown +
                    ", inertia=" + inertia +
                    '}';
        }

        public MotionProfile1D generateProfile(double tStart) {
            if (isLinear()) return new MotionProfile1D(linear.generateSegment(tStart));
            return Profiler1D.generateProfile(maxVelocity, maxAcceleration, -maxAcceleration, tStart, new ActuatorLocation(dStart, getStartVelocity()), new ActuatorLocation(dEnd, getEndVelocity()));
        }

        public class VelocitySegment {

            private final double startVelocity, endVelocity;
            private final AccelerationMode mode;
            private final double acceleration;

            public VelocitySegment(double velocity, AccelerationMode mode) {
                if (mode == AccelerationMode.LINEAR)
                    throw new IllegalArgumentException("cannot construct general segment without specified start & end velocities");
                this.mode = mode;
                acceleration = mode.ratio * maxAcceleration;
                if (mode == AccelerationMode.SLOW_DOWN) {
                    if (dStart == Double.NEGATIVE_INFINITY || dEnd == Double.POSITIVE_INFINITY)
                        this.startVelocity = 0;
                    else
                        this.startVelocity = Math.sqrt(velocity * velocity - 2 * acceleration * (dEnd - dStart));
                    if (Double.isNaN(startVelocity))
                        throw new RuntimeException(velocity * velocity + ", " + acceleration + ", " + (dEnd - dStart));
                    this.endVelocity = velocity;
                } else {
                    this.startVelocity = velocity;
                    if (dStart == Double.NEGATIVE_INFINITY || dEnd == Double.POSITIVE_INFINITY)
                        this.endVelocity = 0;
                    else
                        this.endVelocity = Math.sqrt(velocity * velocity + 2 * acceleration * (dEnd - dStart));
                    if (Double.isNaN(endVelocity))
                        throw new RuntimeException(velocity * velocity + ", " + acceleration + ", " + (dEnd - dStart));
                }
            }

            public VelocitySegment(double vStart, double vEnd) {
                this.startVelocity = vStart;
                this.endVelocity = vEnd;
                this.mode = AccelerationMode.LINEAR;
                if (dStart == Double.NEGATIVE_INFINITY || dEnd == Double.POSITIVE_INFINITY)
                    this.acceleration = 0;
                else
                    this.acceleration = (vEnd * vEnd - vStart * vStart) / (2 * (dEnd - dStart));
                if (Double.isNaN(acceleration))
                    throw new RuntimeException("NaN");
            }

            public AccelerationMode getMode() {
                return mode;
            }

            public double getRatio() {
                return acceleration / maxAcceleration;
            }

            public double getVelocity(double dist) {
                if (mode == AccelerationMode.INERTIA)
                    return startVelocity;
                return Math.sqrt(startVelocity * startVelocity + 2 * acceleration * (dist - dStart));
            }

            public double getStartVelocity() {
                return startVelocity;
            }

            public double getEndVelocity() {
                return endVelocity;
            }

            public Segment generateSegment(double tStart) {
                return new Segment(tStart, tStart + 2 * (dEnd - dStart) / (startVelocity + endVelocity), acceleration, startVelocity, dStart);
            }

            public double getAcceleration() {
                switch (mode) {
                    case INERTIA:
                        return 0;
                    default:
                        return acceleration;
                }
            }

            @Override
            public String toString() {
                return "VelocitySegment{" +
                        "startVelocity=" + startVelocity +
                        ", mode=" + mode +
                        '}';
            }
        }

    }

    public enum AccelerationMode {
        SPEED_UP(1),
        SLOW_DOWN(-1),
        /**
         * INERTIA = constant speed
         */
        INERTIA(0),
        /**
         * LINEAR = acceleration is constant per RANGE, not segment.
         */
        LINEAR(Double.NaN);

        double ratio;

        AccelerationMode(double ratio) {
            this.ratio = ratio;
        }
    }
}
