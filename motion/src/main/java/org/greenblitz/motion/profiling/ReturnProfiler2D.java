package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.utils.LinkedList;

import java.util.ArrayList;
import java.util.List;

public class ReturnProfiler2D {
    /**
     * generates a new profile from the current location back to the profile and merges it with the old profile
     * @param mainProfile         the main profile
     * @param startLocation       the current location
     * @param indexOfMergeSegment the index of the segment in the main profile that we want to merge into
     * @param mergeSegmentNode    the node of the segment in the main profile that we want to merge into
     * @param jump                jump for the new profile
     * @param maxLinearVel        max linear velocity in the new profile
     * @param maxAngularVel       max angular velocity in the new profile
     * @param maxLinearAcc        max linear acceleration in the new profile
     * @param maxAngularAcc       max angular acceleration in the new profile
     * @param tStart              the start time of the new profile
     * @param tForCurve           the time range for the polynomials in the new profile
     * @param smoothingTail       the bigger the smoother the velocity graph will be, but a little slower in the new profile
     * @return the new profile
     */
    public static  MotionProfile2D generateProfile(MotionProfile2D mainProfile, State startLocation, int indexOfMergeSegment,
                                                   LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode, double jump,
                                                   double maxLinearVel, double maxAngularVel, double maxLinearAcc,
                                                   double maxAngularAcc, double tStart, double tForCurve, int smoothingTail){
        ArrayList<State> locations = new ArrayList<State>(2);
        locations.add(startLocation);
        locations.add(mergeSegmentNode.getItem().getStateLocation(mergeSegmentNode.getItem().getTStart()));
        MotionProfile2D returnProfile = ChassisProfiler2D.generateProfile(locations, jump, locations.get(0).getLinearVelocity(),
                locations.get(1).getLinearVelocity(), maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, tStart,
                tForCurve, smoothingTail);

        returnProfile.merge(mainProfile, indexOfMergeSegment, mergeSegmentNode);
        returnProfile.setJahanaRelation(mainProfile.getJahanaRelation());
        return returnProfile;
    }
}
