package org.greenblitz.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.Position;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.TimerTask;

public class Robot extends IterativeRobot {

    @Override
    public void robotInit() {
        Chassis.init();
        Localizer.getInstance().configure(RobotMap.WHEELBASE_WIDTH, Chassis.getInstance().getLeftEncoder(), Chassis.getInstance().getRightEncoder());
        Localizer.startLocalizer();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }

    @Override
    public void autonomousInit() {
        Chassis.getInstance().resetSensors();
        /*Chassis.getInstance().resetLocalizer();
        Chassis.getInstance().enableLocalizer();*/
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
        Localizer.getInstance().resetEncoderDistances();
        System.out.println(Localizer.getInstance().getLocation());
    }

    @Override
    public void teleopPeriodic() {
        System.out.println(Localizer.getInstance().getLocation());
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {
        Chassis.getInstance().resetEncoders();
        Localizer.getInstance().resetEncoderDistances();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }
}