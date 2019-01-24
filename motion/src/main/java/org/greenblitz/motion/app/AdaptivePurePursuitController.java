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

    private double getSpeed(Position robotLoc, Point target, double maxSpeedDist, double minSpeed){
        double speed = target != m_path.getLast() ?
                1 : Math.sqrt(Point.distSqared(robotLoc, target)) / maxSpeedDist;
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

        double[] ret = rawArcDriveValuesTo(robotLoc, target, speed);
        return ret;
    }

    private double[] driveByPolinom(Position robotLoc, Position target, double maxSpeedDist, double minSpeed, double tolerance){

        if (Point.distSqared(target, robotLoc) <= tolerance * tolerance)
            return null;

        double speed = getSpeed(robotLoc, target, maxSpeedDist, minSpeed);

        double x1 = robotLoc.getX();
        double x2 = target.getX();
        double y1 = robotLoc.getY();
        double y2 = target.getY();
        double v1 = Math.tan(robotLoc.getAngle());
        double v2 = Math.tan(target.getAngle());

        if (x1 == x2){
            return arcDrive(0, speed);
        }

        double denominator = Math.pow(x1 - x2, 3);
        
        double v1x1 = v1 * x1;
        double v1x2 = v1 * x2;
        double v2x1 = v2 * x1;
        double v2x2 = v2 * x2;

        double a = (v1x1 - v1x2 + v2x1 - v2x2 - 2*y1 + 2*y2)
                / denominator;
        double b = (-v1x1 * x1 - v1x1 * x2 + 2 * v1x2 * x2 - 2 * v2x1 * x1 + v2x1 * x2
                + v2x2 * x2 + 3 * x1 * y1 - 3 * x1 * y2 + 3 * x2 * y1 - 3 * x2 * y2)
                / denominator;

        double curvature = (6*a*x1 + 2*b) / Math.pow(1 + Math.pow(v1, 2), 1.5);
        return arcDrive(curvature, speed);
    }

    private double[] bazierDriveValuesTo(double locInBazierCurve, Position robotLoc, Position target, double maxSpeedDist, double minSpeed, double tolerance) {
        if (Point.distSqared(target, robotLoc) <= tolerance * tolerance) {
            return null;
        }
        double dist = Point.dist(robotLoc, target);
        Point afterRobot = robotLoc.clone().translate(Point.cis(isBackwards ? -dist/3 : dist/3, robotLoc.getAngle()));
        Point beforeTarget = target.clone().translate(Point.cis(isBackwards ? dist/3 : -dist/3, target.getAngle()));
        Point arcTarget = Point.bezierSample(locInBazierCurve, robotLoc, afterRobot, beforeTarget, target);

        double speed = target != m_path.getLast() ?
                1 : Math.sqrt(Point.distSqared(robotLoc, target)) / maxSpeedDist;
        if (speed < minSpeed)
            speed = minSpeed;

        double[] ret = rawArcDriveValuesTo(robotLoc, arcTarget, speed);
        return ret;
    }

    public double[] iteration(Position robotLoc) {
        Position target = m_path.getGoalPoint(robotLoc, m_lookAhead);
        if (first) {
            System.out.println("robot location: " + robotLoc + ", target: " + target);
            first = false;
        }
        return driveByPolinom(robotLoc, target, m_lookAhead, 0.3, 0.2);
    }
}