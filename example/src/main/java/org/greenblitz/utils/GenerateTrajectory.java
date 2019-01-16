package org.greenblitz.example.utils;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.profiling.exception.ProfilingException;
import org.greenblitz.example.robot.RobotStats;

public class GenerateTrajectory {

    private static void unsafeGenerateTrajectory(Waypoint[] waypoints, Trajectory.FitMethod fit, int samples, double dt, Trajectory[] out){
        Trajectory.Config config = new Trajectory.Config(fit, samples, dt, RobotStats.Picasso.Chassis.MAX_VELOCITY,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);
        out[0] = Pathfinder.generate(waypoints, config);
    }

    public static Trajectory unsafeGenerate(Waypoint[] waypoints, Trajectory.FitMethod fit, int samples, double dt){
        Trajectory.Config config = new Trajectory.Config(fit, samples, dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY / 3.5,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);
        return Pathfinder.generate(waypoints, config);
    }

    public static Trajectory unsafeGenerate(Position[] waypoints, Trajectory.FitMethod fit, int samples, double dt){
        Waypoint[] pp = new Waypoint[waypoints.length];
        for (int i = 0; i < waypoints.length; i++)
            pp[i] = new Waypoint(waypoints[i].getX(), waypoints[i].getY(), waypoints[i].getAngle());
        return unsafeGenerate(pp, fit, samples, dt);
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints, Trajectory.FitMethod fit, int samples, double dt)
    throws ProfilingException {
        Trajectory[] ret = new Trajectory[1];
        ret[0] = null;

        Thread thread = new Thread(
                () -> unsafeGenerateTrajectory(waypoints, fit, samples, dt, ret)
        );
        thread.start();
        long currentTime = System.currentTimeMillis();

        while (ret[0] == null){
            if (System.currentTimeMillis() - currentTime > 10000){
                break;
            }
        }

        thread.interrupt();

        if (ret[0] == null)
            throw new ProfilingException("generator in infinite loop");

        return ret[0];
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints, int samples, double dt) throws ProfilingException {
        return generateTrajectory(waypoints, Trajectory.FitMethod.HERMITE_CUBIC, samples, dt);
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints, double dt) throws ProfilingException {
        return generateTrajectory(waypoints, Trajectory.Config.SAMPLES_HIGH, dt);
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints) throws ProfilingException {
        return generateTrajectory(waypoints, 0.05);
    }

}
