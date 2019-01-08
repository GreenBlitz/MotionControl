package org.greenblitz.motion.app;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

public class AdaptivePurePursuitController {
    private Path m_path;

    public final double m_lookAhead;
    private final double m_wheelBase;

    public AdaptivePurePursuitController(Path path, double lookAhead, double wheelBase) {
        m_path = path;
        m_lookAhead = lookAhead;
        m_wheelBase = wheelBase;
    }

    public double[] driveValuesTo(Position robotLoc, Point target, double maxSpeedDist, double minSpeed) {
        SmartDashboard.putNumber("target x", target.getX());
        SmartDashboard.putNumber("target y", target.getY());

        double speed = target != m_path.getLast() ?
                1 : Point.dist(robotLoc, target) / maxSpeedDist;
        if(speed < minSpeed)
            speed = minSpeed;
        Point diff = Point.subtract(target, robotLoc).rotate(-robotLoc.getAngle());
        /*if(target.getY() < 0 && Math.abs(target.getX()) < Math.abs(target.getY()))
            speed *= -1;
        */
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
        Point target = m_path.getGoalPoint(robotLoc, m_lookAhead, 0.2);
        if (target == null)
            return null;
        return driveValuesTo(robotLoc, target, m_lookAhead, 0.3);
    }
}
