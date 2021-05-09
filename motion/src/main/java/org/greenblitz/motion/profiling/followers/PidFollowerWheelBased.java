package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pid.CollapsingPIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.MotionProfile2D;

public class PidFollowerWheelBased extends AbstractFollower2D{

    private double PIDLimit;
    protected CollapsingPIDController leftController, rightController;

    /**
     * coef  = coefficient
     * ff    = feed forward
     * vel   = velocity
     * acc   = acceleration
     * ang   = angle\angular
     *
     * @param kVl          coef for linear vel ff
     * @param kAl          coef for linear acc ff
     * @param kVr          coef for angular vel ff
     * @param kAr          coef for angular acc ff
     * @param vals         values for the PID controller on the wheel velocities
     * @param collapseVals The threshold of error at which the I value of the wheel vel PID resets
     * @param pidLimit     The maximum output of the PID controllers (for each one)
     * @param profile      The motion profile to follow
     */
    public PidFollowerWheelBased(double kVl, double kAl, double kVr, double kAr,
                                 PIDObject vals, double collapseVals, double pidLimit,
                                 MotionProfile2D profile){
        this.kVl = kVl;
        this.kAl = kAl;
        this.kVr = kVr;
        this.kAr = kAr;
        this.profile = profile;
        this.leftController = new CollapsingPIDController(vals, collapseVals);
        this.rightController = new CollapsingPIDController(vals, collapseVals);
        this.PIDLimit = pidLimit;

        if(Double.isNaN(kVl + kAl + kVr + kAr + collapseVals)){
            throw new RuntimeException("Something is NaN");
        }
    }

    @Override
    public void init() {
        startTime = System.currentTimeMillis(); //TODO: replace millis with sec
        leftController.configure(0, 0, -PIDLimit, PIDLimit, Double.NaN);
        rightController.configure(0, 0, -PIDLimit, PIDLimit, Double.NaN);

        if(dataDelay != 0){
            leftController.setDataDelay(dataDelay, "leftPID");
            rightController.setDataDelay(dataDelay, "rightPID");
        }
    }

    @Override
    public Vector2D forceRun(double leftCurr, double rightCurr, double angularVel, double timeNow) {
        if (profile.isOver(timeNow)) {
            return new Vector2D(0, 0);
        }

        //in a format of <left, right>
        Vector2D velocities = profile.getVelocity(timeNow);
        Vector2D accels = profile.getAcceleration(timeNow);

        double leftMotorV = velocities.getX();
        double leftMotorA = accels.getX();
        double rightMotorV = velocities.getY();
        double rightMotorA = accels.getY();

        if (Double.isNaN(leftMotorV + leftMotorA + rightMotorA + rightMotorV)) {
            throw new RuntimeException("One of the motor ff vals are NaN");
        }

        leftController.setGoal(leftMotorV);
        rightController.setGoal(rightMotorV);

        double leftPID = leftController.calculatePID(leftCurr);
        double rightPID = rightController.calculatePID(rightCurr);

        if (Double.isNaN(leftPID + rightPID)) {
            throw new RuntimeException("LeftPID or RightPID are NaN");
        }

        return new Vector2D(leftMotorV * kVl + leftMotorA * kAl + leftPID,
                rightMotorV * kVr + rightMotorA * kAr + rightPID);
    }
}
