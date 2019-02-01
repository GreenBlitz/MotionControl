package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

/**
 * @author Udi ~ MudiAtalon
 */
public final class AdaptivePurePursuitController extends AbstractPositionPursuitController {

    private final int isBackwards;
    private final double minSpeed;
    private final double maxSpeedDist;
    private final double maxSpeed;


    public AdaptivePurePursuitController(Path path, double lookAhead, double wheelBase,
                                         double tolerance, boolean isBackwards,
                                         double minSpeed, double maxSpeedDist, double maxSpeed) {
        super(path, lookAhead, wheelBase, tolerance);
        this.isBackwards = isBackwards ? -1 : 1;
        this.minSpeed = minSpeed;
        this.maxSpeedDist = maxSpeedDist;
        this.maxSpeed = maxSpeed;
    }

    @Override
    public double getCurvature(Position robotLoc, Position goalPoint) {
        Point diff = Point.subtract(goalPoint, robotLoc).rotate(-robotLoc.getAngle());
        double curv = 2 * diff.getX() / Point.normSquared(diff);
        return curv;
    }

    @Override
    public double getSpeed(Position robotLoc, Position target) {
        return isBackwards * Math.max(

                (1/maxSpeedDist) * maxSpeed * Math.min(
                        maxSpeedDist,
                        Point.dist(robotLoc, m_path.getLast()) / 2
                ),

                minSpeed
        );
    }

    @Override
    public double getLookahead(Position robotLoc) {
        double ret = m_lookahead * Math.max(
                (1/maxSpeedDist)*Math.min(
                        maxSpeedDist,
                        Point.dist(robotLoc, m_path.getLast()) / 2
                ),
                minSpeed/maxSpeed
        );
        return ret;
    }

}