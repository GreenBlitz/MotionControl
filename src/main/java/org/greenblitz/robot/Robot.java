package org.greenblitz.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.Position;
import org.greenblitz.robot.subsystems.Chassis;

public class Robot extends IterativeRobot {

    @Override
    public void robotInit() {
        Chassis.init();
        Localizer.getInstance().configure(new Position(0, 0), 40.0, Chassis.getInstance().getLeftEncoder(), Chassis.getInstance().getRightEncoder());
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
        System.out.println(Localizer.getInstance().getLocation());
    }

    @Override
    public void teleopPeriodic() {
        System.out.println(Localizer.getInstance().getLocation());
        //Scheduler.getInstance().run();
        Chassis.getInstance().tankDrive(
                OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y),
                OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.RIGHT_Y));
    }

    @Override
    public void disabledInit() {
        Chassis.getInstance().resetEncoders();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }
}