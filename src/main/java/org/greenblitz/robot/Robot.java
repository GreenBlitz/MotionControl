package org.greenblitz.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import jaci.pathfinder.Waypoint;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.Position;
import org.greenblitz.robot.commands.FindMaxValues;
import org.greenblitz.robot.subsystems.Chassis;

public class Robot extends IterativeRobot {

    Waypoint[] path = new Waypoint[] {
            new Waypoint(0, 1, 0)
    };


    @Override
    public void robotInit() {
        Chassis.init();
        Localizer.getInstance().configure(RobotStats.Cerberous.Chassis.HORIZONTAL_DISTANCE.value, Chassis.getInstance().getLeftEncoder(), Chassis.getInstance().getRightEncoder());
        Localizer.startLocalizer();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }

    @Override
    public void autonomousInit() {
        Chassis.getInstance().resetSensors();
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();

        Chassis.getInstance().tankDrive(
                OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y),
                OI.getInstance().getMainJS().getAxisValue(SmartJoystick.JoystickAxis.RIGHT_Y));


    }

    @Override
    public void disabledInit() {
        Chassis.getInstance().stop();
        Chassis.getInstance().resetEncoders();
        Localizer.getInstance().reset();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }
}