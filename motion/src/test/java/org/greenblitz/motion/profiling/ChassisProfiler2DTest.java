package org.greenblitz.motion.profiling;

import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph.VelocityChunk.VelocitySegment;
import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph.VelocityChunk;
import org.greenblitz.motion.profiling.ChassisProfiler2D.VelocityGraph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChassisProfiler2DTest {

    private VelocitySegment makeSegment(){
        return new VelocityGraph(10,10,10,10)
                .makeChunk(7, 0).makeSegment(5, VelocityGraph.AccelerationMode.INERTIA);
    }

    @Test
    void VSegmentGetVelocityTest(){
        VelocitySegment seg = makeSegment();
        assertEquals(seg.getVelocity(4), 5);
    }

}
