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
    private ProfilingData profileData;
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
        this.profileData = new ProfilingData(maxLinearVel,maxLinearAcc,maxAngularVel,maxAngularAcc);
        this.tForCurve = tForCurve;
    }

    public void update(org.greenblitz.motion.base.State currState){
        this.currState = currState;
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
            return profile;//tMerge = tEnd;
        }else{
            tMerge = t + destinationTimeOffset;
        }
        int indexOfMergeSegment = profile.quickGetIndex(tMerge);
        LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode = profile.quickGetNode(tMerge);
        MotionProfile2D newProfile = ReturnProfiler2D.generateProfile(
                profile, currState.translate(new org.greenblitz.motion.base.State(profile.getJahanaRelation().negate(), //TODO test if negate needed
                        0, 0)), indexOfMergeSegment, mergeSegmentNode,
                0.05, profileData.getMaxLinearVelocity(), profileData.getMaxAngularVelocity(),
                profileData.getMaxLinearAccel(), profileData.getMaxAngularAccel(),
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
