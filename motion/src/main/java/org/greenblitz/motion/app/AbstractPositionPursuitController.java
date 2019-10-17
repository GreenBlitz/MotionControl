package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

/**
 * This represents any controller that goes after a Path using lookahead and curve driving
 *
 * @author - Alexey
 */
public abstract class AbstractPositionPursuitController<T extends Position> {

    protected Path<T> m_path;
    protected double m_wheelBase;
    protected double m_tolerance;
    protected double m_toleranceSquared;
    protected double m_lookahead;

    public AbstractPositionPursuitController(Path<T> m_path, double m_lookahead, double m_wheelBase, double m_tolerance) {
        this.m_path = m_path;
        this.m_wheelBase = m_wheelBase;
        this.m_tolerance = m_tolerance;
        this.m_lookahead = m_lookahead;
        this.m_toleranceSquared = m_tolerance * m_tolerance;
    }

    protected abstract double getCurvature(T robotLoc, T goalPoint);

    protected double getSpeed(T robotLoc, T goalPoint) {
        System.err.println("Using default speed function of motion is not recommended!");
        return 1;
    }

    protected double getLookahead(T robotLoc) {
        return m_lookahead;
    }

    /**
     * Should be ran every cycle by a command
     *
     * @param robotLoc current robot location
     * @return The values to be passed to the motors
     */
    public double[] iteration(T robotLoc) {
        if (isFinished(robotLoc))
            return new double[]{0, 0};
        T goalPoint = getGoalPoint(robotLoc, getLookahead(robotLoc));
        return arcDrive(getCurvature(robotLoc, goalPoint), getSpeed(robotLoc, goalPoint));
    }

    /**
     * finds the goal point (the point to witch the robot drives) according to the motion algorithm
     *
     * @param robotLoc  the robot location
     * @param lookAhead look ahead distance
     * @return the last intersection on the m_path with the look ahead circle iff such intersection exists
     * the closest point on the m_path O.W.
     */
    protected T getGoalPoint(T robotLoc, double lookAhead) {
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= lookAhead * lookAhead) {
            return m_path.get(m_path.size() - 1);
        }
        T closest = m_path.get(m_path.size() - 1);
        double[] ptlInt; //potential intersections
        for (int ind = m_path.size() - 2; ind >= 0; ind--) {
            ptlInt = Path.intersections(robotLoc, lookAhead, m_path.get(ind), m_path.get(ind + 1));
            if (Point.distSqared(m_path.getSegmentMinimum(m_path.get(ind), m_path.get(ind + 1), ptlInt[0]), robotLoc) < Point.distSqared(closest, robotLoc))
                closest = m_path.get(ind);
            if (ptlInt.length == 1)
                continue;
            if (ptlInt[1] >= 0 && ptlInt[1] <= 1) {
                return (T) m_path.get(ind).weightedAvg(m_path.get(ind + 1), ptlInt[1]).clone();
            }
            if (ptlInt[2] >= 0 && ptlInt[2] <= 1)
                return (T) m_path.get(ind).weightedAvg(m_path.get(ind + 1), ptlInt[2]).clone();
        }
        return closest;
    }


    public final boolean isFinished(T robotLoc) {
        return Position.distSqared(robotLoc, m_path.getLast()) <= m_toleranceSquared;
    }

    protected final double[] arcDrive(double curvature, double speed) {
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

    public Path<T> getPath() {
        return m_path;
    }

    public double getWheelBase() {
        return m_wheelBase;
    }

    public double getTolerance() {
        return m_tolerance;
    }

    public double getLookahead() {
        return m_lookahead;
    }
}
