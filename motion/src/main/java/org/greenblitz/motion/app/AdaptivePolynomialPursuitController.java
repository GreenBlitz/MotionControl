package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

/**
 * @author Udi ~ MudiAtalon
 * @author Alexey ~ Savioor
 *
 */
public class AdaptivePolynomialPursuitController extends AbstractPositionPursuitController<Position> {

    protected final boolean isBackwards;
    protected final double minSpeed, maxSpeed;
    protected final double maxSpeedDist;

    private static final double ROOT_HALF = Math.sqrt(0.5);
    private static final double QUARTER_PI = Math.PI / 4;
    private static final double ZERO_APPROX = 1E-7;


    public AdaptivePolynomialPursuitController(Path path, double lookAhead, double wheelBase,
                                         double tolerance, boolean isBackwards,
                                         double minSpeed, double maxSpeedDist,
                                               double maxSpeed) {
        super(path, lookAhead, wheelBase, tolerance);
        this.isBackwards = isBackwards;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.maxSpeedDist = maxSpeedDist;
    }

    @Override
    protected double getSpeed(Position robotLoc, Position target){
        return (isBackwards ? -1 : 1) * Math.max(

                (1/maxSpeedDist) * maxSpeed * Math.min(
                        maxSpeedDist,
                        Point.dist(robotLoc, m_path.getLast()) / 2
                ),

                minSpeed
        );
    }

    @Override
    protected double getCurvature(Position robotLoc, Position target){
        Position roboMath = robotLoc.localizerToMathCoords();
        Position targMath = target.localizerToMathCoords();

        if (isBackwards)
            roboMath = (Position) roboMath.rotate(Math.PI / 2);

        Point deltaVect = Point.subtract(targMath,
                roboMath).rotate(-roboMath.getAngle());

        double ang = targMath.getAngle() - roboMath.getAngle();

        double v2 = Math.tan(ang);
        double ret;

        if (Math.abs(v2) <= 500) { // Angle is less then 0.999*(PI/2) or more then 1.001*(PI/2)

            double x2 = deltaVect.getX();
            x2 = Math.max(Math.abs(x2), ZERO_APPROX)*Math.signum(x2);
            double y2 = deltaVect.getY();

            ret = 2 * (v2 * x2 - 3 * y2) / Math.pow(x2, 2);
            if (ret == 0 || 1/ret > m_wheelBase){
                return ret; // make sure we are not spinning in a circle
            }
            // better to use regular motion than to spin
            return -2 * deltaVect.getX() / Point.normSquared(deltaVect);
        }
        // We got here if tan is too high
        deltaVect.rotate(QUARTER_PI);
        double x2 = deltaVect.getX();
        x2 = Math.max(Math.abs(x2), ZERO_APPROX) * Math.signum(x2);
        double y2 = deltaVect.getY();

        ret = ROOT_HALF * (x2 - 3 * y2) / Math.pow(x2, 2);

        if (ret == 0 || 1/ret > m_wheelBase){
            return ret; // make sure we are not spinning in a circle
        }
        // better to use regular motion than to spin
        deltaVect.rotate(-QUARTER_PI); // Rotate back to correct position
        return -2 * deltaVect.getX() / Point.normSquared(deltaVect);
    }

}