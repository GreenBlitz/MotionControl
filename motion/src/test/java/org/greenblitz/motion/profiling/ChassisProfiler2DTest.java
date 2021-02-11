package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChassisProfiler2DTest {

    @Test
    void VSegmentGetVelocityTest() {

    }

    @Test
    void vsUpTest() {

    }

    @Test
    void vsDownTest() {

    }

    @Test
    void concatForwardsTest() {

    }

    @Test
    void concatBackwardsTest() {
    }

    @Test
    void velocityGraphTest() {

    }

    @Test
    void ChassisProfiler2DTest() {
        List<State> lst = new ArrayList<>();
        lst.add(new State(0, 0, 0, 0, 0));
        lst.add(new State(3, 5, Math.PI/2, 0, 0));
        System.out.println(ChassisProfiler2D.generateProfile(lst, 0.001, 5, 4, 3, 2));
    }

}
