package org.greenblitz.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.subsystems.ElevatorPrototype;

import java.util.Timer;

public class Robot extends TimedRobot {

    @Override
    public void robotInit() {
        ElevatorPrototype.init();
        OI.init();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }


    Timer t = new Timer();

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        ElevatorPrototype.getInstance().resetEncoder();
        prevTime = System.currentTimeMillis();
    }

    long prevTime;

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        SmartDashboard.putNumber("Ticks", ElevatorPrototype.getInstance().getDistance());
        if (ElevatorPrototype.getInstance().getSpeed() >
                SmartDashboard.getNumber("Vel", 0))
            SmartDashboard.putNumber("Vel", ElevatorPrototype.getInstance().getSpeed());
        double acc = ElevatorPrototype.getInstance().getSpeed() /
                ((System.currentTimeMillis() - prevTime) / 1000.0);
        if (acc > SmartDashboard.getNumber("Acc", 0))
            SmartDashboard.putNumber("Acc", acc);
        prevTime = System.currentTimeMillis();

    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        ElevatorPrototype.getInstance().update();
    }

    public static void main(String[] args) {
        RobotBase.startRobot(Robot::new);
    }
}