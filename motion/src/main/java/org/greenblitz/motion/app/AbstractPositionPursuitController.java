package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

/**
 * This represents any controller that goes after a Path using lookahead and curve driving
 */
public abstract class AbstractPositionPursuitController {

    protected Path m_path;
    protected double m_wheelBase;
    protected double m_tolerance;
    protected double m_toleranceSquared;
    protected double m_lookahead;

    /**
     *
     * @param m_path
     * @param m_lookahead
     * @param m_wheelBase
     * @param m_tolerance
     */
    public AbstractPositionPursuitController(Path m_path, double m_lookahead, double m_wheelBase, double m_tolerance) {
        this.m_path = m_path;
        this.m_lookahead = m_tolerance;
        this.m_wheelBase = m_wheelBase;
        this.m_tolerance = m_tolerance;
        this.m_lookahead = m_lookahead;
        this.m_toleranceSquared = m_tolerance * m_tolerance;
    }

    /**
     * Given data, this should return the curvature
     * @param robotLoc
     * @param goalPoint
     * @return
     */
    protected abstract double getCurvature(Position robotLoc, Position goalPoint);

    /**
     * What should be the power of the fast side of the robot?
     * @param robotLoc
     * @param goalPoint
     * @return
     */
    protected double getSpeed(Position robotLoc, Position goalPoint){
        System.err.println("Using default speed function of APPC is not recommended!");
        return 1;
    }

    /**
     * Used for dynamic lookahead
     * @param robotLoc
     * @return
     */
    protected double getLookahead(Position robotLoc){
        System.err.println("Using default lookahead function of APPC is not recommended!");
        return m_lookahead;
    }

    /**
     * Should be ran every cycle by a command
     * @param robotLoc
     * @return The values to be passed to the motors
     */
    public double[] iteration(Position robotLoc) {
        if (isFinished(robotLoc))
            return new double[] {0, 0};
        Position goalPoint = getGoalPoint(robotLoc, getLookahead(robotLoc));
        System.out.println("Curve: " + getCurvature(robotLoc, goalPoint));
        return arcDrive(getCurvature(robotLoc, goalPoint), getSpeed(robotLoc, goalPoint));
    }

    /**
     * finds the goal point (the point to witch the robot drives) according to the APPC algorithm
     *
     * @param robotLoc  the robot location
     * @param lookAhead look ahead distance
     * @return the last intersection on the m_path with the look ahead circle iff such intersection exists
     * the closest point on the m_path O.W.
     */
    protected Position getGoalPoint(Point robotLoc, double lookAhead) {
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= lookAhead * lookAhead) {
            return m_path.get(m_path.size() - 1);
        }
        Position closest = m_path.get(m_path.size() - 1);
        double[] ptlInt; //potential intersections
        for (int ind = m_path.size() - 2; ind >= 0; ind--) {
            ptlInt = Path.intersections(robotLoc, lookAhead, m_path.get(ind), m_path.get(ind + 1));
            if (Point.distSqared(Path.getSegmentMinimum(m_path.get(ind), m_path.get(ind + 1), ptlInt[0]), robotLoc) < Point.distSqared(closest, robotLoc))
                closest = m_path.get(ind);
            if (ptlInt.length == 1)
                continue;
            if (ptlInt[1] >= 0 && ptlInt[1] <= 1) {
                return new Position(Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), ptlInt[1]), m_path.get(ind).getAngle());
            }
            if (ptlInt[2] >= 0 && ptlInt[2] <= 1)
                return new Position(Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), ptlInt[2]), m_path.get(ind).getAngle());
        }
        return closest;
    }

    /**
     * Quite obvious, remember to use in utilizing command
     * @param robotLoc
     * @return
     */
    public final boolean isFinished(Position robotLoc){
        return Position.distSqared(robotLoc, m_path.getLast()) <= m_toleranceSquared;
    }

    /**
     * Drives along a curvature
     * @param curvature
     * @param speed
     * @return
     */
    protected final double[] arcDrive(double curvature, double speed){
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

    public Path getM_path() {
        return m_path;
    }

    public double getM_wheelBase() {
        return m_wheelBase;
    }

    public double getM_tolerance() {
        return m_tolerance;
    }

    public double getM_lookahead() {
        return m_lookahead;
    }
}
