package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph;
import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph.VelocityChunk;
import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph.VelocityChunk.VelocitySegment;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChassisProfiler2DTest {

    private VelocitySegment makeSegment(VelocityGraph.AccelerationMode mode) {
        return new VelocityGraph(10, 10, 10, 10)
                .makeChunk(7, 0, null).makeSegment(5, mode);
    }

    @Test
    void VSegmentGetVelocityTest() {
        VelocitySegment seg = makeSegment(VelocityGraph.AccelerationMode.INERTIA);
        assertEquals(seg.getVelocity(4), 5);
    }

    @Test
    void vsUpTest() {
        VelocitySegment s = makeSegment(VelocityGraph.AccelerationMode.SPEED_UP);

        assertEquals(s.getVelocity(4), Math.sqrt(5 * 5 + 2 * 4 * 10));
        assertEquals(s.getVelocity(1), Math.sqrt(5 * 5 + 2 * 10));
        assertEquals(s.getVelocity(7), Math.sqrt(5 * 5 + 2 * 7 * 10));

    }

    @Test
    void vsDownTest() {
        VelocitySegment s = makeSegment(VelocityGraph.AccelerationMode.SLOW_DOWN);

        assertEquals(s.getVelocity(4), Math.sqrt(5 * 5 + 2 * (7 - 4) * 10));
        assertEquals(s.getVelocity(1), Math.sqrt(5 * 5 + 2 * (7 - 1) * 10));
        assertEquals(s.getVelocity(7), Math.sqrt(5 * 5 + 2 * (7 - 7) * 10));

    }

    @Test
    void concatForwardsTest() {
        VelocityChunk s1 = new VelocityGraph(10, 10, 10, 10)
                .makeChunk(7, 0, null);
        VelocityChunk s2 = new VelocityGraph(10, 10, 10, 10)
                .makeChunk(7, 10, null);
        s1.concatForwards(s2);

        assertEquals(s1.getEndVelocity(), s2.maxVelocity);

    }

    @Test
    void concatBackwardsTest() {
        VelocityChunk s1 = new VelocityGraph(10, 10, 10, 10)
                .makeChunk(7, 5, null);
        VelocityChunk s2 = new VelocityGraph(10, 10, 10, 10)
                .makeChunk(7, 2, null);
        s2.concatBackwards(s1);

        assertEquals(s1.getEndVelocity(), s2.getStartVelocity());

    }

    @Test
    void velocityGraphTest() {

        List<ICurve> path = new ArrayList<>();

        class ArcCurve implements ICurve {

            double length, curvature;

            public ArcCurve(double length, double curvature) {
                this.length = length;
                this.curvature = curvature;
            }

            @Override
            public Point getLocation(double u) {
                return null;
            }

            @Override
            public double getLinearVelocity(double u) {
                return 0;
            }

            @Override
            public double getAngularVelocity(double u) {
                return 0;
            }

            @Override
            public double getLength(double u) {
                return length;
            }

            @Override
            public double getAngle(double u) {
                return 0;
            }

            @Override
            public double getCurvature() {
                return curvature;
            }

            @Override
            public double getCurvature(double u) {
                return curvature;
            }

            @Override
            public ICurve getSubCurve(double uStart, double uEnd) {
                return null;
            }
        }

        path.add(new ArcCurve(20, 0));
        path.add(new ArcCurve(3, 2));
        path.add(new ArcCurve(7, -3));
        path.add(new ArcCurve(0.5, 0));
        path.add(new ArcCurve(2.7, -0.3));

        VelocityGraph g = new VelocityGraph(path, 10, 10, 10, 10);

        for (double dist = 0; dist <= g.getLength() + 0.1; dist += 0.1)
            System.out.println(dist + ", " + g.getVelocity(dist) + ", " + g.getAcceleration(dist));

        throw new RuntimeException("I've decided that you have FAILED!!!!!");
    }

    @Test
    void ChassisProfiler2DTest() {
        List<State> lst = new ArrayList<>();
        lst.add(new State(0, 0, 0, 0, 0));
        lst.add(new State(3, 5, 0, 0, 0));
        System.out.println(ChassisProfiler2D.generateProfile(lst, 0.01, 0.01, 5, 4, 3, 2));
    }

}
