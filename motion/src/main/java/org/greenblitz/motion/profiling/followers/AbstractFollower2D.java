package org.greenblitz.motion.profiling.followers;

import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pid.CollapsingPIDController;
import org.greenblitz.motion.profiling.MotionProfile2D;

public abstract class AbstractFollower2D {



    protected long startTime;
    protected double kVl, kAl;
    protected double kVr, kAr;
    protected MotionProfile2D profile;


    protected RemoteCSVTarget wheelTarget;
    protected RemoteCSVTarget globalTarget;
    protected RemoteCSVTarget leftOutputTarget;
    protected RemoteCSVTarget rightOutputTarget;
    protected boolean sendData = false;

    /**
     * Use with EXTREME CAUTION. this is used for dynamic motion profiling and is
     * generally not that safe.
     *
     * @param profile
     */
    public void setProfile(MotionProfile2D profile) {
        this.profile = profile;
    }

    /**
     *  used to synchronise two followers
     * @param startTime
     */

    public void setStartTime(long startTime) {
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
        return run(left, right, angularVel, (long) (currTime * 1000.0));
    }

    /**
     * @param leftCurr   The left wheel velocity
     * @param rightCurr  The right wheel velocity
     * @param angularVel The angular velocity
     * @param curTime    The current time <b>in miliseconds</b>
     * @return A vector of power to each motor in the format (left, right)
     */
     public Vector2D run(double leftCurr, double rightCurr, double angularVel, long curTime) {
        return forceRun(leftCurr, rightCurr, angularVel, (curTime - startTime) / 1000.0);
    }

    public abstract Vector2D forceRun(double leftCurr, double rightCurr, double angularVel, double timeNow);

    /**
     * @return true if the profile finished running, false otherwise
     */
    public boolean isFinished() {
        boolean isFinished = profile.isOver((System.currentTimeMillis() - startTime) / 1000.0);
        if(isFinished){
            end();
        }
        return isFinished;
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
        sendData = val;
    }

    public abstract void end();

}
