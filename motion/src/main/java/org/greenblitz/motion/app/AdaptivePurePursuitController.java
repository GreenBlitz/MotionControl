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

    public AdaptivePurePursuitController(Path path, double lookAhead, double wheelBase, boolean isBackwards) {
        m_path = path;
        m_lookAhead = lookAhead;
        m_wheelBase = wheelBase;
        this.isBackwards = isBackwards;
    }

    /**
     * arcDrive. calculates the values the motors should get to get to the target point
     * @param robotLoc the robot location + angle
     * @param target the target point
     * @param maxSpeedDist the minimum distance at witch you still drive at maximum speed
     * @param minSpeed minimum speed
     * @param tolerance the distance to the final point at witch the algorithm stops
     * @return [left motor value, right motor value] iff still driving
     *      null O.W.
     */
    public double[] driveValuesTo(Position robotLoc, Point target, double maxSpeedDist, double minSpeed, double tolerance) {
        if (Point.distSqared(target, robotLoc) <= tolerance*tolerance) {
            return null;
        }
		
        double speed = target != m_path.getLast() ?
                1 : Math.sqrt(Point.distSqared(robotLoc, target)) / maxSpeedDist;
        if(speed < minSpeed)
            speed = minSpeed;
        Point diff = Point.subtract(target, robotLoc).rotate(-robotLoc.getAngle());
        if(isBackwards)
            speed *= -1;

        double curvature = 2 * diff.getX() / Point.normSquared(diff);
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

    public double[] iteration(Position robotLoc) {
        Point target = m_path.getGoalPoint(robotLoc, m_lookAhead);
        return driveValuesTo(robotLoc, target, m_lookAhead, 0.3, 0.2);
    }
}
