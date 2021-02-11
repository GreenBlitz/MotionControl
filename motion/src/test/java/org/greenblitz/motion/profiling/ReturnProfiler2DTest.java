package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

public class ReturnProfiler2DTest {

    static private MotionProfile2D testProfile;

    MotionProfile2D testGenerate(){
        if (testProfile == null) {
            State start = new State(2, 5, Math.PI / 2, 3, Math.PI);
            State end = new State(8, 8, Math.PI / 8, 8 / 8, Math.PI / 8);
            ArrayList<State> locations = new ArrayList<State>(2);
            locations.add(start);
            locations.add(end);

            MotionProfile2D profile = ChassisProfiler2D.generateProfile(locations, .02, 2, Math.PI / 4, 5, Math.PI,
                    1, Math.PI, 0, 1.5, 4);
            State currentStart = new State(5, 6, Math.PI, 5, Math.PI * 2);
            testProfile = (ReturnProfiler2D.generateProfile(profile, currentStart, profile.quickGetIndex(4), profile.quickGetNode(4), .02,
                    15, Math.PI * 15, 1, Math.PI, 0.15, 1.5, 4));


        }
        return testProfile;
    }


    void printTest(){
        System.out.println(testGenerate());
    }


    void offsetTest(){
        System.out.println(testGenerate().quickGetNode(4));
        System.out.println(testGenerate().quickGetNode(3));
        System.out.println(testGenerate().quickGetIndex(3));
    }

    @Test
    void ultimateTest(){
        printTest();
        offsetTest();
    }



}
