package org.greenblitz.motion.pathfinder;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import org.greenblitz.motion.RobotStats;

public class GenerateTrajectory {

    private static void unsafeGenerateTrajectory(Waypoint[] waypoints, Trajectory.FitMethod fit, int samples, double dt, Trajectory[] out){
        Trajectory.Config config = new Trajectory.Config(fit, samples, dt, RobotStats.Picasso.Chassis.MAX_VELOCITY,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);
        out[0] = Pathfinder.generate(waypoints, config);
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints, Trajectory.FitMethod fit, int samples, double dt)
    throws PathfinderException {
        Trajectory[] ret = new Trajectory[1];
        ret[0] = null;

        Thread thread = new Thread(
                () -> unsafeGenerateTrajectory(waypoints, fit, samples, dt, ret)
        );
        thread.start();
        long currentTime = System.currentTimeMillis();

        while (ret[0] == null){
            if (System.currentTimeMillis() - currentTime > 1000){
                break;
            }
        }

        thread.interrupt();

        if (ret[0] == null)
            throw new PathfinderException("generator in infinite loop");

        return ret[0];
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints, int samples, double dt) throws PathfinderException{
        return generateTrajectory(waypoints, Trajectory.FitMethod.HERMITE_CUBIC, samples, dt);
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints, double dt) throws PathfinderException{
        return generateTrajectory(waypoints, Trajectory.Config.SAMPLES_HIGH, dt);
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints) throws PathfinderException{
        return generateTrajectory(waypoints, 0.05);
    }

}
