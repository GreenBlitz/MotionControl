import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.subsystems.Chassis;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Scanner;

public class PathfinderTests {

    /*Scanner scan = new Scanner(System.in);
    Trajectory.Config config;
    Trajectory m_trajectory;
    TankModifier mod;
    Trajectory m_leftTrajectory;
    Trajectory m_rightTrajectory;

    public void createPaths(Trajectory.FitMethod fit, int samples, double dt, Waypoint[] waypoints) {
        this.config = new Trajectory.Config(fit, samples, dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY.value,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION.value,
                RobotStats.Picasso.Chassis.MAX_JERK.value);
        this.m_trajectory = Pathfinder.generate(waypoints, config);
        this.mod = new TankModifier(this.m_trajectory);
        this.mod.modify(RobotStats.Picasso.Chassis.VERTICAL_DISTANCE.value);
        this.m_leftTrajectory  = mod.getLeftTrajectory();
        this.m_rightTrajectory = mod.getRightTrajectory();

        Chassis.getInstance().resetEncoders();
    }

    @Test
    void printPath(){
        createPaths(Trajectory.FitMethod.HERMITE_CUBIC,
                10000,
                5,
                new Waypoint[] {
                        new Waypoint(0, 0, 0),
                        new Waypoint(1, 1, 0)
                });

        Point center = new Point(500, 500);

        for (int i = 0; i < m_trajectory.length() - 1; i++) {
            Trajectory.Segment s = m_trajectory.get(i);
            Trajectory.Segment e = m_trajectory.get(i + 1);
            // 1 meter = 100 pixels
            Line l = new Line(new Point(s.x * 100, s.y * 100), new Point(e.x * 100, e.y * 100));
            double red = ((s.velocity + e.velocity) / 2.0) / RobotStats.Picasso.Chassis.MAX_VELOCITY.value;
            double green = ((s.acceleration + e.acceleration) / 2.0) / RobotStats.Picasso.Chassis.MAX_ACCELERATION.value;
            double blue = ((s.jerk + e.jerk) / 2.0) / RobotStats.Picasso.Chassis.MAX_JERK.value;
            l.setColor(new Color((int) (red * 255), (int) (green * 255), (int) (blue * 255)));
            l.translate(center);
            l.queuePaint();
        }
        Canvas.getInstance().setSize(1000, 1000);
        Canvas.getInstance().repaint();
        scan.next();
    }*/

}
