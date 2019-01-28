package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

/**
 * @author Udi ~ MudiAtalon
 * @author Alexey ~ Savioor
 *
 */
public class AdaptivePolynomialPursuitController extends AbstractPurePursuitController {

    protected final boolean isBackwards;
    protected final double minSpeed;
    protected final double maxSpeedDist;


    public AdaptivePolynomialPursuitController(Path path, double lookAhead, double wheelBase,
                                         double tolerance, boolean isBackwards,
                                         double minSpeed, double maxSpeedDist) {
        super(path, lookAhead, wheelBase, tolerance);
        this.isBackwards = isBackwards;
        this.minSpeed = minSpeed;
        this.maxSpeedDist = maxSpeedDist;
    }

    @Override
    protected double getSpeed(Position robotLoc, Position target){
        double speed = target != m_path.getLast() ?
                1 : Math.sqrt(Point.distSqared(robotLoc, target)) / maxSpeedDist;
        if (speed < minSpeed)
            speed = minSpeed;
        return speed;
    }

    @Override
    protected double getCurvature(Position robotLoc, Position target){

        double x1 = robotLoc.getX();
        double x2 = target.getX();
        double y1 = robotLoc.getY();
        double y2 = target.getY();
        double v1 = Math.tan(robotLoc.getAngle()%(2*Math.PI) == 0 ? 1E-5 : robotLoc.getAngle());
        double v2 = Math.tan(target.getAngle()%(2*Math.PI) == 0 ? 1E-5 : target.getAngle());

        if (x1 == x2){
            return 0;
        }

        double denominator = Math.pow(x1 - x2, 3);

        double a = (v1 * x1 - v1 * x2 + v2 * x1 - v2 * x2 - 2 * y1 + 2 * y2)
                / denominator;
        double b = (-v1 * x1 * x1 - v1 * x1 * x2 + 2 * v1 * x2 * x2 - 2 * v2 * x1 * x1 + v2 * x1 * x2
                + v2 * x2 * x2 + 3 * x1 * y1 - 3 * x1 * y2 + 3 * x2 * y1 - 3 * x2 * y2)
                / denominator;

        return (6*a*x1 + 2*b) / Math.pow(1 + Math.pow(v1, 2), 1.5);
    }

}