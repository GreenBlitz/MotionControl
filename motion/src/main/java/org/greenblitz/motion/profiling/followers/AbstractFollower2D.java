package org.greenblitz.motion.profiling.followers;

import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.debug.RemoteCSVTargetBuffer;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pid.CollapsingPIDController;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.motorFormula.AbstractMotorFormula;
import org.greenblitz.utils.Time;

public abstract class AbstractFollower2D {



    protected double startTime;
    protected AbstractMotorFormula formula;
    protected MotionProfile2D profile;
    protected boolean started;


    protected RemoteCSVTargetBuffer wheelTarget;
    protected RemoteCSVTargetBuffer errorTarget;
    protected RemoteCSVTargetBuffer globalTarget;
//    protected RemoteCSVTargetBuffer leftOutputTarget;
//    protected RemoteCSVTargetBuffer rightOutputTarget;
    protected double dataDelay = 0;

    /**
     * Use with EXTREME CAUTION. this is used for dynamic motion profiling and is
     * generally not that safe.
     * @param profile
     */
    public void setProfile(MotionProfile2D profile) {
        this.profile = profile;
    }

    /**
     *  used to synchronise two followers
     * @param startTime
     */

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }


    /**
     * Resets all relevant data, call before every run.
     */
    public abstract void init();

    /**
     * For this function, the time is the time since the last call to init().
     *
     * @param left       The left wheel velocity
     * @param right      The right wheel velocity
     * @param angularVel The angular velocity
     * @return A vector of power to each motor in the format (left, right)
     * @see PidFollower2D#init()
     */
    public Vector2D run(double left, double right, double angularVel) {
        return run(left, right, angularVel, System.currentTimeMillis());
    }

    /**
     * @param left       The left wheel velocity
     * @param right      The right wheel velocity
     * @param angularVel The angular velocity
     * @param currTime   The current time <b>in seconds</b>
     * @return A vector of power to each motor in the format (left, right)
     */
    public Vector2D run(double left, double right, double angularVel, double currTime) {
        if(!started){
            startTime = currTime - 0.001;
            started = true;
        }
        return forceRun(left, right, angularVel, currTime - startTime);
    }

    public abstract Vector2D forceRun(double leftCurr, double rightCurr, double angularVel, double timeNow);

    /**
     * @return true if the profile finished running, false otherwise
     */
    public boolean isFinished() {
        return profile.isOver(Time.getTime() - startTime);
    }

    /**
     * If this is true, data will be sent to CSVLogger about the profile following performance. If this is false
     * no data will be sent. By default, this is false.
     * <p>
     * NOTE: Don't call this function after calling init()!
     *
     * @param val whether to send data or not
     */
    public void setSendData(boolean val) {
        dataDelay = val ? 0.05 : 0;
    }

    public void setDataDelay(int val){dataDelay = val;}


    public void sendCSV(){
        if(dataDelay != 0) {
            globalTarget.passToCSV(true);
            wheelTarget.passToCSV(true);
//            errorTarget.passToCSV(true);
//            leftOutputTarget.passToCSV(true);
//            rightOutputTarget.passToCSV(true);
        }
    }
}
