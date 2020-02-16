package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

/**
 * This code was written before the 2019 season, and used during the 2019 season. Sadly, we didn't document it well.
 * Non the less, THIS WORKS WELL. It was tried and tested and can achieve nice results.<br>
 * For example see the auto here: https://www.thebluealliance.com/match/2019tes_qm108 <br><br>
 * <p>
 * If you wish to use this function yourself, you can see this link:<br>
 * https://github.com/GreenBlitz/Deep-Space-Robot/blob/detroit/src/main/java/edu/greenblitz/knockdown/commands/simple/chassis/motion/APPCCommand.java<br>
 * as a reference.<br>
 * - Alexey <br>
 *
 * @author Udi ~ MudiAtalon
 */
public final class AdaptivePurePursuitController extends AbstractPositionPursuitController<Position> {

    private final int isBackwards;
    private final double minSpeed;
    private final double maxSpeedDist;
    private final double maxSpeed;


    public AdaptivePurePursuitController(Path<Position> path, double lookAhead, double wheelBase,
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
        Point diff = Point.subtract(goalPoint, robotLoc).rotate(-robotLoc.getAngle()); // This line is here because if the robot
        // goes backwards he is "facing" the opposite direction
        return 2 * diff.getX() / Point.normSquared(diff);
    }

    public boolean isBackwards() {
        return isBackwards == -1;
    }

    @Override
    public double getSpeed(Position robotLoc, Position target) {
        return isBackwards * Math.max(

                (1 / maxSpeedDist) * maxSpeed * Math.min(
                        maxSpeedDist,
                        Point.dist(robotLoc, m_path.getLast()) / 2
                ),

                minSpeed
        );
    }

    @Override
    public double getLookahead(Position robotLoc) {
        double ret = m_lookahead * Math.max(
                (1 / maxSpeedDist) * Math.min(
                        maxSpeedDist,
                        Point.dist(robotLoc, m_path.getLast()) / 2
                ),
                minSpeed / maxSpeed
        );
        return ret;
    }

}