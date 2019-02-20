package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2D {

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return generateProfile(locs, curvatureTolerance, jump, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, 0);
    }

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart) {

        MotionProfile1D linearProfile = new MotionProfile1D();
        MotionProfile1D angularProfile = new MotionProfile1D();
        MotionProfile1D tempProfile;
        State first, second;
        ICurve curve;
        List<ICurve> subCurves = new ArrayList<>();
        ArrayList<ActuatorLocation> path = new ArrayList<>();
        List<MotionProfile1D.Segment> rotationSegs;

        double t0 = tStart;
        for (int i = 0; i < locs.size() - 1; i++) {

            first = locs.get(i);
            second = locs.get(i + 1);

            curve = new BezierCurve(first, second);

            subCurves.clear(); // All subcurves with kinda equal curvature
            divideToEqualCurvatureSubcurves(subCurves, curve, jump, curvatureTolerance);
            System.out.println(subCurves);

            double currentMaxLinearVelocity, currenctMaxLinearAccel, curvature;
            path.clear();
            path.add(new ActuatorLocation(0, 0));
            path.add(new ActuatorLocation(0, 0));
            double lenSoFar = 0;
            for (ICurve subCur : subCurves) {
                curvature = subCur.getCurvature();
                currentMaxLinearVelocity = getMaxVelocity(maxLinearVel, maxAngularVel, curvature);
                currenctMaxLinearAccel = getMaxAcceleration(maxLinearAcc, maxAngularAcc, curvature);

                path.get(0).setX(lenSoFar);
                path.get(0).setV(subCur.getLinearVelocity(0));
                path.get(1).setX(lenSoFar + subCur.getLength(1));
                path.get(1).setV(subCur.getLinearVelocity(1));
                lenSoFar = path.get(1).getX();

                tempProfile = Profiler1D.generateProfile(
                        path,
                        currentMaxLinearVelocity, currenctMaxLinearAccel, -currenctMaxLinearAccel, t0
                );
                t0 = tempProfile.getTEnd();

                linearProfile.unsafeAdd(tempProfile);

                rotationSegs = tempProfile.getSegments();
                MotionProfile1D.Segment seg0 = rotationSegs.get(0);
                seg0.setAccel(seg0.getAccel() * curvature);
                seg0.setStartLocation(first.getAngle());
                seg0.setStartVelocity(first.getAngularVelocity());
                MotionProfile1D.Segment curr, prev;
                for (int k = 1; k < rotationSegs.size(); k++) {
                    curr = rotationSegs.get(k);
                    prev = rotationSegs.get(k - 1);
                    curr.setAccel(curr.getAccel() * curvature);
                    curr.setStartVelocity(prev.getVelocity(prev.getTEnd()));
                    curr.setStartLocation(prev.getLocation(prev.getTEnd()));
                }
                angularProfile.unsafeAdd(new MotionProfile1D(rotationSegs));
            }
        }

        return new MotionProfile2D(linearProfile, angularProfile);
    }

    private static double getMaxVelocity(double maxLinearVel, double maxAngularVel, double curvature) {
        return 1.0 / (1.0 / maxLinearVel + Math.abs(curvature) / maxAngularVel);
    }

    private static double getMaxAcceleration(double maxLinearAcc, double maxAngularAcc, double curvature) {
        return 1.0 / (1.0 / maxLinearAcc + Math.abs(curvature) / maxAngularAcc);
    }

    /**
     * This function takes one curve, and stores it's subcurves in a list,
     * such as each subcurve continues the previous one and each subcurve will have
     * roughly equal curvature.
     *
     * @param returnList         The list to which the subcurves will be added
     * @param source             The main curve to be divided
     * @param jump               Jump intervals, when sampling the curvature the function will sample
     *                           every 'jump' units.
     * @param curvatureTolerance The maximum curvature difference within each subcurve.
     * @return returnList
     */
    private static List<ICurve> divideToEqualCurvatureSubcurves(List<ICurve> returnList, ICurve source, double jump, double curvatureTolerance) {
        double t0 = 0;
        double curveStart, prevt0;
        while (t0 < 1.0) {
            curveStart = source.getCurvature(t0);
            prevt0 = t0;

            for (double j = t0 + jump; j <= 1; j += jump) {
                if (Math.abs(source.getCurvature(j) - curveStart) > curvatureTolerance) {
                    returnList.add(source.getSubCurve(t0, j));
                    t0 = j;
                    break;
                }
            }

            if (t0 == prevt0) {
                returnList.add(source.getSubCurve(t0, 1));
                break;
            }
        }
        return returnList;
    }

    private static VelocityGraph getVelocityGraph(List<ICurve> track, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return new VelocityGraph(track, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc);
    }


    public static class VelocityGraph {

        private static double maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc;

        public void initialize(double maxLinearVel, double maxAngularVel,
                               double maxLinearAcc, double maxAngularAcc) {
            VelocityGraph.maxLinearVel = maxLinearVel;
            VelocityGraph.maxAngularVel = maxAngularVel;
            VelocityGraph.maxLinearAcc = maxLinearAcc;
            VelocityGraph.maxAngularAcc = maxAngularAcc;
        }

        private List<VelocityChunk> m_chunks;
        private int previous;

        public VelocityGraph(List<ICurve> track, double maxLinearVel,
                             double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
            previous = 0;
            initialize(maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc);

            m_chunks = new ArrayList<>();
            m_chunks.add(new VelocityChunk(0, 0, 0));
            for (ICurve curve : track)
                m_chunks.add(new VelocityChunk(
                        m_chunks.get(m_chunks.size() - 1).dEnd,
                        m_chunks.get(m_chunks.size() - 1).dEnd + curve.getLength(1),
                        curve.getCurvature()));

            m_chunks.add(new VelocityChunk(m_chunks.get(m_chunks.size() - 1).dEnd));

            for (int ind = 1; ind < m_chunks.size(); ind++)
                m_chunks.get(ind).concatBackwards(m_chunks.get(ind - 1));

            for (int ind = m_chunks.size() - 2; ind >= 0; ind--)
                m_chunks.get(ind).concatForwards(m_chunks.get(ind + 1));
        }

        private VelocityChunk quickGetChunk(double dist) {
            for (int i = 0; i < m_chunks.size(); i++) {
                if (m_chunks.get((previous + i) % m_chunks.size()).isPartOfChunk(dist)) {
                    previous = (i + previous) % m_chunks.size();
                    return m_chunks.get(previous);
                }
            }
            throw new IndexOutOfBoundsException("No segment with distance " + dist);
        }

        public double getVelocity(double dist) {
            return quickGetChunk(dist).getVelocity(dist);
        }

        public class VelocityChunk {

            public final double dStart, dEnd,
                    maxVelocity, maxAcceleration;
            private VelocitySegment speedup = null, slowdown = null, inertia;

            public VelocityChunk(double dStart, double dEnd, double curvature) {
                this.dStart = dStart;
                this.dEnd = dEnd;
                maxVelocity = getMaxVelocity(maxLinearVel, maxAngularVel, curvature);
                maxAcceleration = getMaxAcceleration(maxLinearAcc, maxAngularAcc, curvature);
                inertia = new VelocitySegment(maxVelocity, AccelerationMode.INERTIA);
            }

            public VelocityChunk(double d) {
                this.dStart = d;
                this.dEnd = d;
                this.maxVelocity = 0;
                this.maxAcceleration = 0;
                inertia = new VelocitySegment(0, AccelerationMode.INERTIA);
            }

            public boolean isPartOfChunk(double dist) {
                return dist >= dStart && dist <= dEnd;
            }

            public double getVelocity(double dist) {
                double ret = inertia.getVelocity(dist);
                if (speedup != null)
                    ret = Math.min(ret, speedup.getVelocity(dist));
                if (slowdown != null)
                    ret = Math.min(ret, slowdown.getVelocity(dist));
                return ret;
            }

            public double getStartVelocity() {
                double ret = inertia.getStartVelocity();
                if (speedup != null)
                    ret = Math.min(ret, speedup.getStartVelocity());
                if (slowdown != null)
                    ret = Math.min(ret, slowdown.getStartVelocity());
                return ret;
            }

            public double getEndVelocity() {
                double ret = inertia.getEndVelocity();
                if (speedup != null)
                    ret = Math.min(ret, speedup.getEndVelocity());
                if (slowdown != null)
                    ret = Math.min(ret, slowdown.getEndVelocity());
                return ret;
            }

            public void concatBackwards(VelocityChunk other) {
                speedup = new VelocitySegment(other.getEndVelocity(), AccelerationMode.SPEED_UP);
            }

            public void concatForwards(VelocityChunk other) {
                slowdown = new VelocitySegment(other.getStartVelocity(), AccelerationMode.SLOW_DOWN);
            }

            public class VelocitySegment {

                private final double velocity;
                private final AccelerationMode mode;

                public VelocitySegment(double velocity, AccelerationMode mode) {
                    this.velocity = velocity;
                    this.mode = mode;
                }

                public AccelerationMode getMode() {
                    return mode;
                }

                public double getVelocity(double dist) {
                    switch (mode) {
                        case INERTIA:
                            return velocity;
                        case SPEED_UP:
                            return Math.sqrt(velocity * velocity + 2 * maxAcceleration * (dist - dStart));
                        case SLOW_DOWN:
                            return Math.sqrt(velocity * velocity + 2 * maxAcceleration * (dEnd - dist));
                    }
                    throw new RuntimeException("congratulations! you have discovered a hidden part of the code!");
                }

                public double getStartVelocity() {
                    return mode != AccelerationMode.SLOW_DOWN ? velocity : getVelocity(dStart);
                }

                public double getEndVelocity() {
                    return mode != AccelerationMode.SPEED_UP ? velocity : getVelocity(dEnd);
                }

            }

        }

        public enum AccelerationMode {
            SPEED_UP,
            SLOW_DOWN,
            /**
             * INERTIA = constant speed
             */
            INERTIA
        }
    }
}
