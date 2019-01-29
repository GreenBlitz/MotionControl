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
        return Math.min(speed, 0.6);
    }

    @Override
    protected double getCurvature(Position robotLoc, Position target){
        Position roboMath = robotLoc.frcToMathCoords();
        Position targMath = target.frcToMathCoords();
        Point deltaVect = Point.subtract(targMath,
                roboMath).rotate(-roboMath.getAngle());
        double ang = targMath.getAngle() - roboMath.getAngle();

        double v2 = Math.tan(ang);

        if (Math.abs(v2) <= 500) { // Angle is less then 0.999*(PI/2) or more then 1.001*(PI/2)

            double x2 = deltaVect.getX();
            x2 = Math.max(Math.abs(x2), 1E-6)*Math.signum(x2);
            double y2 = deltaVect.getY();

            return 2 * (v2 * x2 - 3 * y2) / Math.pow(x2, 2);
        }

        deltaVect.rotate(Math.PI / 4);
        double x2 = deltaVect.getX();
        x2 = Math.max(Math.abs(x2), 1E-6)*Math.signum(x2);
        double y2 = deltaVect.getY();
        //v2 = Math.tan(ang + (Math.PI / 4)) = -1;
        //v1 = Math.tan(Math.PI / 4) = 1;

        // 2*b/(1 + 1)**1.5 = 0.707*b
        return 0.7071*(2 * x2 - 3 * y2) / Math.pow(x2, 2);
    }

}