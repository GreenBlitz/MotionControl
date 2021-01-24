package org.greenblitz;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.ChassisProfiler2D;
import org.greenblitz.motion.profiling.MotionProfile2D;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static long runTime;

    public static long runOnce(){
        long startTime = System.currentTimeMillis();
        State start = new State(0, 0, 0, 0, 0);
        State end = new State(1, 0.435, 1.2*Math.PI, 0, 0);
        double jump = 0.001;
        double startVel = start.getLinearVelocity();
        double endVel = end.getLinearVelocity();
        double maxLinearVel = 0.3;
        double maxAngularVel = 0.25*Math.PI;
        double maxLinearAcc = 0.3;
        double maxAngularAcc = 0.15*Math.PI;
        double tStart = 0;
        double tForCurve = 1.6;
        int smoothingTail = 4;
        ArrayList<State> locations = new ArrayList<State>(2);
        locations.add(start);
        locations.add(end);
        MotionProfile2D profile = ChassisProfiler2D.generateProfile(locations, jump, startVel, endVel, maxLinearVel,
                maxAngularVel, maxLinearAcc, maxAngularAcc, tStart, tForCurve, smoothingTail);
        return System.currentTimeMillis()-startTime;
    }

    public static void main(String[] args){
        System.out.println(runOnce());
        /*for (int i = 0; i < 1000; i++ ) {
            runTime += runOnce(i);
        }
        double avg = (double)runTime/1000;
        System.out.println("test: "+ avg);
        System.out.println(runOnce(0));*/
    }

}
