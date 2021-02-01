package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.base.Vector2D;

public abstract class AbstractFollower2D {
    protected long startTime;

    /**
     * If this is true, data will be sent to CSVLogger about the profile following performance. If this is false
     * no data will be sent. By default, this is false.
     * <p>
     * NOTE: Don't call this function after calling init()!
     *
     * @param val whether to send data or not
     */
    public abstract void setSendData(boolean val);

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
    public abstract boolean isFinished();

}
