package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.utils.LinkedList;

import java.io.PipedOutputStream;

public class ThreadedReturnProfiler implements Runnable {
    private  MotionProfile2D profile;

    private Thread thread;
    private long startTime;
    private double destinationTimeOffset;
    private double linearVelocity;
    private double angularVelocity;
    private double maxLinearVel;
    private double maxAngularVel;
    private double maxLinearAcc;
    private double maxAngularAcc;
    private double tForCurve;
    private org.greenblitz.motion.base.State currState;


    public MotionProfile2D getProfile() {
        return profile;
    }

    public ThreadedReturnProfiler(MotionProfile2D profile, long startTime, double destinationTimeOffset, double maxLinearVel, double maxAngularVel,
                                  double maxLinearAcc, double maxAngularAcc, double tForCurve) {
        this.profile = profile;
        this.startTime = startTime;
        this.destinationTimeOffset = destinationTimeOffset;
        this.maxLinearVel = maxLinearVel;
        this.maxAngularVel = maxAngularVel;
        this.maxLinearAcc = maxLinearAcc;
        this.maxAngularAcc = maxAngularAcc;
        this.tForCurve = tForCurve;
    }

    public void update(org.greenblitz.motion.base.State currState){
        this.currState = currState;
        this.linearVelocity = currState.getLinearVelocity();
        this.angularVelocity = currState.getAngularVelocity();
    }

    @Override
    public void run() {
        profile = generateNewProfile();
    }

    private MotionProfile2D generateNewProfile(){
        double tEnd = profile.getTEnd();
        double t = (System.currentTimeMillis() - startTime)/1000.0;
        double tMerge;
        if(t + destinationTimeOffset > tEnd){
            tMerge = tEnd;
        }else{
            tMerge = t + destinationTimeOffset;
        }
        int indexOfMergeSegment = profile.quickGetIndex(tMerge);
        LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode = profile.quickGetNode(tMerge);
        MotionProfile2D newProfile = ReturnProfiler2D.generateProfile(
                profile, new org.greenblitz.motion.base.State(currState.translate(profile.getJahanaRelation().negate()), linearVelocity, angularVelocity) //TODO test if negate needed
                , indexOfMergeSegment, mergeSegmentNode, 0.05, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc,
                (System.currentTimeMillis()-startTime)/1000.0, tForCurve, 4);
        System.out.println("finished swapping");
        return newProfile;
    }

    public void start(){
        thread = new Thread(this);
        thread.start();
    }

    public boolean isAlive(){
        if (thread == null){
            return false;
        }
        return thread.isAlive();
    }
}
