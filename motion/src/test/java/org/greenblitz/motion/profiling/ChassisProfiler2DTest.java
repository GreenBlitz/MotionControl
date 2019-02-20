package org.greenblitz.motion.profiling;

import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph.VelocityChunk.VelocitySegment;
import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph.VelocityChunk;
import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChassisProfiler2DTest {

    private VelocitySegment makeSegment(){
        return new VelocityGraph(10,10,10,10)
                .makeChunk(7, 0).makeSegment(5, VelocityGraph.AccelerationMode.INERTIA);
    }

    @Test
    void VSegmentGetVelocityTest(){
        VelocitySegment seg = makeSegment();
        System.out.println(seg);
        assertEquals(seg.getVelocity(4), 5);
    }

    @Test
    void vsUpTest(){
        VelocitySegment s = new VelocityGraph(10,10,10,10)
                .makeChunk(7, 0).makeSegment(5, VelocityGraph.AccelerationMode.SPEED_UP);

        assertEquals(s.getVelocity(4), Math.sqrt(5*5 + 2*4*10));
        assertEquals(s.getVelocity(1), Math.sqrt(5*5 + 2*10));
        assertEquals(s.getVelocity(7), Math.sqrt(5*5 + 2*7*10));

    }

    @Test
    void vsDownTest(){
        VelocitySegment s = new VelocityGraph(10,10,10,10)
                .makeChunk(7, 0).makeSegment(5, VelocityGraph.AccelerationMode.SLOW_DOWN);

        assertEquals(s.getVelocity(4), Math.sqrt(5*5 + 2*(7-4)*10));
        assertEquals(s.getVelocity(1), Math.sqrt(5*5 + 2*(7-1)*10));
        assertEquals(s.getVelocity(7), Math.sqrt(5*5 + 2*(7-7)*10));

    }

    @Test
    void concatForwardsTest(){
        VelocityChunk s1 = new VelocityGraph(10,10,10,10)
                .makeChunk(7, 0);
        VelocityChunk s2 = new VelocityGraph(10,10,10,10)
                .makeChunk(7, 10);
        s1.concatForwards(s2);

        assertEquals(s1.getEndVelocity(), s2.maxVelocity);

    }

    @Test
    void concatBackwardsTest(){
        VelocityChunk s1 = new VelocityGraph(10,10,10,10)
                .makeChunk(7, 5);
        VelocityChunk s2 = new VelocityGraph(10,10,10,10)
                .makeChunk(7, 2);
        s2.concatBackwards(s1);

        assertEquals(s1.getEndVelocity(), s2.getStartVelocity());

    }

}
