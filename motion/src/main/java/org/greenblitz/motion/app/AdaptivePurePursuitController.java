package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

/**
 * @author Udi ~ MudiAtalon
 */
public class AdaptivePurePursuitController {
    private Path m_path;

    public final double m_lookAhead;
    private final double m_wheelBase;

    public final boolean isBackwards;

    boolean first = true;
    boolean alsoFirst = true;

    public AdaptivePurePursuitController(Path path, double lookAhead, double wheelBase, boolean isBackwards) {
        m_path = path;
        m_lookAhead = lookAhead;
        m_wheelBase = wheelBase;
        this.isBackwards = isBackwards;
    }

    private double[] rawArcDriveValuesTo(Position robotLoc, Point target, double speed) {
        speed *= isBackwards ? -1 : 1;

        Point diff = Point.subtract(target, robotLoc).rotate(-robotLoc.getAngle());
        double curvature = 2 * diff.getX() / Point.normSquared(diff);
        System.out.println("robot location: " + robotLoc + ", target: " + target);
        return arcDrive(curvature, speed);
    }

    private double[] arcDrive(double curvature, double speed){
        if (alsoFirst) {
            System.out.println("curvature: " + curvature);
            alsoFirst = false;
        }
        if (curvature == 0)
            return new double[]{speed, speed};
        double radius = 1 / curvature;
        double rightRadius = radius + m_wheelBase / 2;
        double leftRadius = radius - m_wheelBase / 2;
        if (curvature > 0)
            return new double[]{speed * leftRadius / rightRadius, speed};
        else
            return new double[]{speed, speed * rightRadius / leftRadius};
    }

    private double getDynamicLookahead(Position robotLoc){
        double tmpLookahead = Point.dist(m_path.getLast(), robotLoc);
        return Math.min(Math.max(tmpLookahead, 0.1), m_lookAhead);
    }

    private double getSpeed(Position robotLoc, Point target, double maxSpeedDist, double minSpeed){
        double speed = target != m_path.getLast() ?
                1 : Point.dist(robotLoc, target) / maxSpeedDist;
        if (speed < minSpeed)
            speed = minSpeed;
        return speed;
    }

    /**
     * arcDrive. calculates the values the motors should get to get to the target point in an arc
     *
     * @param robotLoc     the robot location + angle
     * @param target       the target point
     * @param maxSpeedDist the minimum distance at witch you still drive at maximum speed
     * @param minSpeed     minimum speed
     * @param tolerance    the distance to the final point at witch the algorithm stops
     * @return [left motor value, right motor value] iff still driving
     * null O.W.
     */
    public double[] arcDriveValuesTo(Position robotLoc, Point target, double maxSpeedDist, double minSpeed, double tolerance) {
        if (Point.distSqared(target, robotLoc) <= tolerance * tolerance) {
            return null;
        }

        double speed = getSpeed(robotLoc, target, maxSpeedDist, minSpeed);

        return rawArcDriveValuesTo(robotLoc, target, speed);
    }


    public double[] iteration(Position robotLoc) {
        Position target = m_path.getGoalPoint(robotLoc, m_lookAhead);
        if (first) {
            System.out.println("robot location: " + robotLoc + ", target: " + target);
            first = false;
        }
        return arcDriveValuesTo(robotLoc, target, m_lookAhead, 0.3, 0.2*0.3);
    }
}