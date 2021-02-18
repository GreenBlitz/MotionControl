package org.greenblitz.motion.profiling;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.utils.LinkedList;

public class ThreadedReturnProfiler extends Thread {
    private  MotionProfile2D profile;

    private long startTime;
    private double destinationTimeOffset;
    private double linearVelocity;
    private double angularVelocity;
    private double maxLinearVel;
    private double maxAngularVel;
    private double maxLinearAcc;
    private double maxAngularAcc;
    private double tForCurve;
    private double stateSampling;


    public MotionProfile2D getProfile() {
        return profile;
    }

    public ThreadedReturnProfiler(MotionProfile2D profile, long startTime, double destinationTimeOffset, double maxLinearVel, double maxAngularVel,
                                  double maxLinearAcc, double maxAngularAcc, double tForCurve, double stateSampling) {
        this.profile = profile;
        this.startTime = startTime;
        this.destinationTimeOffset = destinationTimeOffset;
        this.maxLinearVel = maxLinearVel;
        this.maxAngularVel = maxAngularVel;
        this.maxLinearAcc = maxLinearAcc;
        this.maxAngularAcc = maxAngularAcc;
        this.tForCurve = tForCurve;
        this.stateSampling = stateSampling;
    }

    public void update(double linearVelocity, double angularVelocity){
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
    }

    @Override
    public void run() {
        profile = generateNewProfile();
    }

    private MotionProfile2D generateNewProfile(){
        double tEnd = profile.getTEnd();
        int t = (int) (System.currentTimeMillis() - startTime);
        double tMerge;
        if(t + destinationTimeOffset > tEnd){
            tMerge = tEnd;
        }else{
            tMerge = t + destinationTimeOffset;
        }
        int indexOfMergeSegment = profile.quickGetIndex(tMerge);
        LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode = profile.quickGetNode(tMerge);
        return ReturnProfiler2D.generateProfile(
                profile, new org.greenblitz.motion.base.State(Localizer.getInstance().getLocation().translate(profile.getJahanaRelation().negate()), linearVelocity, angularVelocity) //TODO test if negate needed
                , indexOfMergeSegment, mergeSegmentNode, 0.01, System.currentTimeMillis()-startTime, maxLinearVel, maxAngularVel,
                maxLinearAcc, maxAngularAcc, tForCurve, 4);
    }


    private org.greenblitz.motion.base.State getMergeState(){
        double minMergeError = Double.POSITIVE_INFINITY;
        org.greenblitz.motion.base.State optimalState = null;
        for (double t = (double) (System.currentTimeMillis()); t <= profile.getTEnd(); t += stateSampling* (profile.getTEnd())) {
            org.greenblitz.motion.base.State curr = profile.quickGetSegment(t).getStateLocation(profile.quickGetSegment(t).getTStart());
            Position actual = Localizer.getInstance().getLocation().translate(profile.getJahanaRelation().negate());
            double curror = 2*Math.atan((actual.getY()-curr.getY())/(actual.getX()-curr.getX())) - actual.getAngle()-curr.getAngle();
            if(minMergeError > curror){
                minMergeError = curror;
                optimalState = curr;
            }
        }
        return optimalState;
    }
}
