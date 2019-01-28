package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.opencv.core.Mat;

/**
 * @author Udi ~ MudiAtalon
 * @author Alexey ~ Savioor
 *
 */
public class AdaptivePolynomialPursuitController extends AbstractPositionPursuitController {

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
        return Math.min(speed, 0.5);
    }

    @Override
    protected double getCurvature(Position robotLoc, Position target){
        Position roboMath = robotLoc.frcToMathCoords();
        Position targMath = target.frcToMathCoords();
        Point deltaVect = Point.subtract(targMath,
                roboMath).rotate(-roboMath.getAngle());
        double ang = targMath.getAngle() - roboMath.getAngle();

        double x2 = deltaVect.getX();
        double y2 = deltaVect.getY();
        double v2 = Math.tan(ang);

        return x2 == 0 ? 0 : 2*(v2 * x2 - 3 * y2) / -Math.pow(x2, 2);
    }

}