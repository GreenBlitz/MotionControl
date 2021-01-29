package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;

import java.util.ArrayList;
import java.util.List;

public class ReturnProfiler2D {
    public static  MotionProfile2D generateProfile(MotionProfile2D mainProfile, State startLocation, int indexOfMergeSegment,
                                                   double jump, double velocityStart, double velocityEnd,
                                                   double maxLinearVel, double maxAngularVel, double maxLinearAcc, double maxAngularAcc,
                                                   double tStart,
                                                   double tForCurve,
                                                   int smoothingTail){
        ArrayList<State> locations = new ArrayList<State>(2);
        locations.add(startLocation);
        locations.add(mainProfile.getSegments().get(indexOfMergeSegment).getStateLocation());
        MotionProfile2D returnProfile = ChassisProfiler2D.generateProfile(locations, jump, velocityStart, velocityEnd,
                maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, tStart, tForCurve, smoothingTail);

        returnProfile.getSegments().addAll(mainProfile.getSegments().subList(indexOfMergeSegment, mainProfile.getSegments().size()));
        return returnProfile;
    }
}
