package org.greenblitz.robot;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.robot.RobotStats;

public class RobotPath {

    private static Trajectory[] test = new Trajectory[2];

    public static final double dt = 20 / 1000.0;

    static {
        init();
    }

    public static Trajectory[] getTestTrajectory() {
        return test;
    }

    private static void init() {
        Waypoint[] tests = new Waypoint[]{
                new Waypoint(0, 0, 0),
                new Waypoint(2, 2, -Math.PI / 2),
        };

        initPF(tests);
    }

    /**
     * @param fit       CUBIC_SPLINE or something else
     * @param samples   number of new points in each segment
     * @param dt        time period between segments/points
     * @param waypoints path waypoints
     */
    private static void initPF(Trajectory.FitMethod fit, int samples, double dt, Waypoint[] waypoints) {
        Trajectory.Config config = new Trajectory.Config(fit, samples, dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY / 2,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);

        Trajectory trajectory = Pathfinder.generate(waypoints, config);
        TankModifier mod = new TankModifier(trajectory).modify(RobotStats.Picasso.Chassis.HORIZONTAL_DISTANCE);

        test[0] = mod.getLeftTrajectory();
        test[1] = mod.getRightTrajectory();
    }

    private static void initPF(Waypoint[] waypoints) {
        initPF(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, dt, waypoints);
    }

}
