package org.greenblitz.example.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import jaci.pathfinder.Trajectory;
import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.example.utils.GenerateTrajectory;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.app.Path;
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
        Position[] list = new Position[]{
                new Position(0, 0, -Math.PI/2),
                new Position(1, 1, 0),
                new Position(0, 2, Math.PI/2),
                new Position(-1, 1, Math.PI),
                new Position(0, 0, -Math.PI/2)
        };

        Path path = Path.pathfinderPathToGBPath(GenerateTrajectory.unsafeGenerate(
                list, Trajectory.FitMethod.HERMITE_CUBIC, 100, 0.02
        ));

        APPC = new AdaptivePurePursuitController(path, 0.6, Chassis.getInstance().getWheelbaseWidth());
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        double speedLimit = 0.5;
        double[] veryFastDrive = APPC.iteration(Chassis.getInstance().getLocation());
        if (veryFastDrive != null)
            Chassis.getInstance().tankDrive(speedLimit * veryFastDrive[0], speedLimit * veryFastDrive[1]);
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
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