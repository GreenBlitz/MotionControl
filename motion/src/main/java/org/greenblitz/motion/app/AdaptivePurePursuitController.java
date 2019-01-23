package org.greenblitz.motion.app;

import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.rmi.Remote;

/**
 * @author Udi ~ MudiAtalon
 */
public class AdaptivePurePursuitController {
    private Path m_path;

    public final double m_lookAhead;
    private final double m_wheelBase;

    public final boolean isBackwards;
    private final long timeOffset;
    private long time = Integer.MIN_VALUE;

    private RemoteCSVTarget arcdrive_logger;
    private RemoteCSVTarget main_logger;

    public AdaptivePurePursuitController(Path path, double lookAhead, double wheelBase, boolean isBackwards) {
        m_path = path;
        m_lookAhead = lookAhead;
        m_wheelBase = wheelBase;
        this.isBackwards = isBackwards;
        RemoteCSVTarget.initTarget("APPC_Path", "x", "y");
        RemoteCSVTarget.initTarget("bezier_spinner", "time", "robotX", "robotY", "targetX", "targetY");
        RemoteCSVTarget.initTarget("BEN_GURION_FOLLOW_PATH", "time", "robotX", "robotY", "targetX", "targetY");

        RemoteCSVTarget path_Logger = RemoteCSVTarget.getTarget("APPC_Path");
        for(Point point: m_path) {
            System.out.println(point);
            path_Logger.report(point.getX(), point.getY());
        }

        arcdrive_logger = RemoteCSVTarget.getTarget("bezier_spinner");
        main_logger = RemoteCSVTarget.getTarget("BEN_GURION_FOLLOW_PATH");
        timeOffset = System.currentTimeMillis();
    }

    public double[] rawArcDriveValuesTo(Position robotLoc, Point target, double speed) {
        speed *= isBackwards ? -1 : 1;

        Point diff = Point.subtract(target, robotLoc).rotate(-robotLoc.getAngle());
        double curvature = 2 * diff.getX() / Point.normSquared(diff);
        if (curvature == 0)
            return new double[]{speed, speed};
        double radius = 1 / curvature;
        double rightRadius = radius + m_wheelBase / 2;
        double leftRadius = radius - m_wheelBase / 2;
        arcdrive_logger.report(time, robotLoc.getX(), robotLoc.getY(), target.getX(), target.getY());
        if (curvature > 0)
            return new double[]{speed * leftRadius / rightRadius, speed};
        else
            return new double[]{speed, speed * rightRadius / leftRadius};
    }

    /**
     * arcDrive. calculates the values the motors should get to get to the target point in an arc
     *
     * @param robotLoc     the robot location + angle
     * @param target       the target point
     * @param maxSpeedDist the minimum distance at witch you still drive at maximum speed
     * @param minSpeed     minimum speed
     * @return [left motor value, right motor value] iff still driving
     * null O.W.
     */
    public double[] arcDriveValuesTo(Position robotLoc, Point target, double maxSpeedDist, double minSpeed) {
        double speed = target != m_path.getLast() ?
                1 : Point.dist(robotLoc, target) / maxSpeedDist;
        if (speed < minSpeed)
            speed = minSpeed;

        return rawArcDriveValuesTo(robotLoc, target, speed);
    }

    public double[] bezierDriveValuesTo(double locInBazierCurve, Position robotLoc, Position target, double maxSpeedDist, double minSpeed) {
        double dist = Point.dist(robotLoc, target);
        Point afterRobot = robotLoc.clone().translate(Point.cis(isBackwards ? -dist/3 : dist/3, robotLoc.getAngle()));
        Point beforeTarget = target.clone().translate(Point.cis(isBackwards ? dist/3 : -dist/3, target.getAngle()));
        Point arcTarget = Point.bezierSample(locInBazierCurve, robotLoc, afterRobot, beforeTarget, target);

        double speed = target != m_path.getLast() ?
                1 : Math.sqrt(Point.distSqared(robotLoc, target)) / maxSpeedDist;
        if (speed < minSpeed)
            speed = minSpeed;

        return rawArcDriveValuesTo(robotLoc, arcTarget, speed);
    }

    public double[] iteration(Position robotLoc, double PowerScale) {
        time = System.currentTimeMillis() - timeOffset;
        Position target = m_path.getGoalPoint(robotLoc, m_lookAhead);

        main_logger.report(time, robotLoc.getX(), robotLoc.getY(), target.getX(), target.getY());

        if (Point.distSqared(target, robotLoc) <= 0.2 * 0.2) {
            return null;
        }

        double[] ret = arcDriveValuesTo(robotLoc, target, m_lookAhead, 0.3);
        ret[0]*=PowerScale;
        ret[1]*=PowerScale;
        return ret;
    }
}
