package org.greenblitz.example.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Trajectory;
import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.example.utils.GenerateTrajectory;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.app.Path;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

public class Robot extends TimedRobot {

    AdaptivePurePursuitController APPC;

    @Override
    public void robotInit() {
        Chassis.init();
        OI.init();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }

    @Override
    public void autonomousInit() {
        Chassis.getInstance().resetSensors();
        final double S_R_T_I_I_I = 1/(1/(1/Math.sqrt(2)));
        Position[] list = new Position[]{
                new Position(0, 0),
                new Position(2.828, 1.172),
                new Position(4, 4),
                new Position(2.828, 6.828),
                new Position(0, 8),
                new Position(-2.828, -6.282),
                new Position(-4, 4),
                new Position(-2.828, 1.172),
                new Position(2.828, -1.172),
                new Position(4, -4),
                new Position(2.828, -6.828),
                new Position(0, -8),
        };

        Path path = new Path(list);
        path.interpolate(10);
        System.out.println(path.getPath());
        APPC = new AdaptivePurePursuitController(path, 0.8, Chassis.getInstance().getWheelbaseWidth());
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        double speedLimit = 0.9;
        double[] veryFastDrive = APPC.iteration(Chassis.getInstance().getLocation());
        if (veryFastDrive != null)
            Chassis.getInstance().tankDrive(speedLimit * veryFastDrive[0], speedLimit * veryFastDrive[1]);
        else
            Chassis.getInstance().setBrake();
        SmartDashboard.putNumber("Left intended power", veryFastDrive != null ? speedLimit * veryFastDrive[0] : 0);
        SmartDashboard.putNumber("Right intended power", veryFastDrive != null ? speedLimit * veryFastDrive[1] : 0);
        SmartDashboard.putBoolean("Is running?", veryFastDrive != null);
    }

    @Override
    public void testInit() {
        Chassis.getInstance().resetSensors();
        final double S_R_T_I_I_I = 1/(1/(1/Math.sqrt(2)));
        Position[] list = new Position[]{
                new Position(0, 0),
                new Position(-(1-S_R_T_I_I_I), S_R_T_I_I_I),
                new Position(-1, 1),
                new Position(-(1+S_R_T_I_I_I), S_R_T_I_I_I),
                new Position(2, 0)
        };

        Path path = new Path(list);

        APPC = new AdaptivePurePursuitController(path, 0.5, Chassis.getInstance().getWheelbaseWidth());
    }

    @Override
    public void testPeriodic() {
        Scheduler.getInstance().run();
        double speedLimit = 1;
        double[] veryFastDrive = APPC.iteration(Chassis.getInstance().getLocation());
        if (veryFastDrive != null)
            Chassis.getInstance().tankDrive(speedLimit * veryFastDrive[0], speedLimit * veryFastDrive[1]);
        else
            Chassis.getInstance().setBrake();
        SmartDashboard.putNumber("Left intended power", veryFastDrive != null ? speedLimit * veryFastDrive[0] : 0);
        SmartDashboard.putNumber("Right intended power", veryFastDrive != null ? speedLimit * veryFastDrive[1] : 0);
        SmartDashboard.putBoolean("Is running?", veryFastDrive != null);
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
        Chassis.getInstance().setCoast();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        Chassis.getInstance().update();
    }

    @Override
    public void disabledInit() {
        Chassis.getInstance().stop();
        Chassis.getInstance().forceEncodersReset();
        Chassis.getInstance().resetSensors();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }
}