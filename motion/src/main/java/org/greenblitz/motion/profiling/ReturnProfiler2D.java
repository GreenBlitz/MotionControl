package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.utils.LinkedList;

import java.util.ArrayList;
import java.util.List;

public class ReturnProfiler2D {
    public static  MotionProfile2D generateProfile(MotionProfile2D mainProfile, State startLocation, int indexOfMergeSegment,
                                                   LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode, double jump, double velocityStart,
                                                   double velocityEnd, double maxLinearVel, double maxAngularVel,
                                                   double maxLinearAcc, double maxAngularAcc, double tStart,
                                                   double tForCurve, int smoothingTail){
        ArrayList<State> locations = new ArrayList<State>(2);
        locations.add(startLocation);
        locations.add(mergeSegmentNode.getItem().getStateLocation());
        MotionProfile2D returnProfile = ChassisProfiler2D.generateProfile(locations, jump, velocityStart, velocityEnd,
                maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, tStart, tForCurve, smoothingTail);

        returnProfile.getSegments().merge(mainProfile.getSegments(), indexOfMergeSegment, mergeSegmentNode);
        return returnProfile;
    }
}
