package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.opencv.core.Mat;

/**
 * @author Udi ~ MudiAtalon
 */
public class AdaptivePurePursuitController extends AbstractPositionPursuitController {

    protected final boolean isBackwards;
    protected final double minSpeed;
    protected final double maxSpeedDist;

    public AdaptivePurePursuitController(Path path, double lookAhead, double wheelBase,
                                         double tolerance, boolean isBackwards,
                                         double minSpeed, double maxSpeedDist) {
        super(path, lookAhead, wheelBase, tolerance);
        this.isBackwards = isBackwards;
        this.minSpeed = minSpeed;
        this.maxSpeedDist = maxSpeedDist;
    }

    @Override
    protected double getCurvature(Position robotLoc, Position goalPoint) {
        Point diff = Point.subtract(goalPoint, robotLoc).rotate(-robotLoc.getAngle());
        return 2 * diff.getX() / Point.normSquared(diff);
    }

    @Override
    protected double getSpeed(Position robotLoc, Position target){
        double speed = target != m_path.getLast() ?
                1 : Math.sqrt(Point.distSqared(robotLoc, target)) / maxSpeedDist;
        if (speed < minSpeed)
            speed = minSpeed;
        return Math.min(speed, 0.45);
    }

    @Override
    protected double getLookahead(Position robotLoc){
        return m_lookahead;
    }

}