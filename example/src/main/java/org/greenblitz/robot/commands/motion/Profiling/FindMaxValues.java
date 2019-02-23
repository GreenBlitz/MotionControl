package org.greenblitz.robot.commands.motion.Profiling;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.SmartJoystick;

public class FindMaxValues extends Command {

    double maxVelL = 0;

    double prevVelL = 0;
    double maxAccL = 0;

    double prevAccL = 0;
    double maxJerkL = 0;

    double maxVelR = 0;

    double prevVelR = 0;
    double maxAccR = 0;

    double prevAccR = 0;
    double maxJerkR = 0;

    long lastTime;


    public FindMaxValues() {
        requires(Chassis.getInstance());
        this.maxVelL = 0;
        this.maxVelR = 0;
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void initialize() {
        System.out.println("setGoal");
        this.lastTime = System.currentTimeMillis();
    }

    @Override
    protected void execute() {
        double leftInput = OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y);
        double rightInput = OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.RIGHT_X);

        Chassis.getInstance().arcadeDrive(leftInput, rightInput);

        double newSpeedR = Chassis.getInstance().getRightSpeed();
        double newSpeedL = Chassis.getInstance().getLeftSpeed();

        long deltaTMili = System.currentTimeMillis() - lastTime;
        lastTime += deltaTMili;
        double deltaT = deltaTMili / 1000.0;

        double accL = (newSpeedL - prevVelL) / deltaT;
        prevVelL = newSpeedL;
        double accR = (newSpeedR - prevVelR) / deltaT;
        prevVelR = newSpeedR;

        double jerkL = (accL - prevAccL) / deltaT;
        prevAccL = accL;
        double jerkR = (accR - prevAccR) / deltaT;
        prevAccR = accR;

        if (Math.abs(newSpeedL) > maxVelL) maxVelL = Math.abs(newSpeedL);
        if (Math.abs(newSpeedR) > maxVelR) maxVelR = Math.abs(newSpeedR);

        if (Math.abs(accL) > maxAccL) maxAccL = Math.abs(accL);
        if (Math.abs(accR) > maxAccR) maxAccR = Math.abs(accR);

        if (Math.abs(jerkL) > maxJerkL) maxJerkL = Math.abs(jerkL);
        if (Math.abs(jerkR) > maxJerkR) maxJerkR = Math.abs(jerkR);

        SmartDashboard.putNumber("left V", newSpeedL);
        SmartDashboard.putNumber("right V", newSpeedR);
        SmartDashboard.putNumber("left A", accL);
        SmartDashboard.putNumber("right A", accR);
        SmartDashboard.putNumber("left J", jerkL);
        SmartDashboard.putNumber("right J", jerkR);

        SmartDashboard.putNumber("left V max", maxVelL);
        SmartDashboard.putNumber("right V max", maxVelR);
        SmartDashboard.putNumber("left A max", maxAccL);
        SmartDashboard.putNumber("right A max", maxAccR);
        SmartDashboard.putNumber("left J max", maxJerkL);
        SmartDashboard.putNumber("right J max", maxJerkR);
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
    }
}
