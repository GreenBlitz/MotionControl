package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.SmartJoystick;
import org.greenblitz.robot.subsystems.Chassis;

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
        System.out.println("constructed");
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void initialize() {
        System.out.println("init");
        this.lastTime = System.currentTimeMillis();
    }

    @Override
    protected void execute() {
        Chassis.getInstance().arcadeDrive(
                0.5 * OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.RIGHT_X),
                0.5 * OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y));
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

        if (Math.abs(newSpeedL) > maxVelL) maxVelL = newSpeedL;
        if (Math.abs(newSpeedR) > maxVelR) maxVelR = newSpeedR;

        if (Math.abs(accL) > maxAccL) maxAccL = accL;
        if (Math.abs(accR) > maxAccR) maxAccR = accR;

        if (Math.abs(jerkL) > maxJerkL) maxJerkL = jerkL;
        if (Math.abs(jerkR) > maxJerkR) maxJerkR = jerkR;

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
