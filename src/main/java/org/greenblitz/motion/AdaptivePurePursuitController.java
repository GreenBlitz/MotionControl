package org.greenblitz.motion;

import org.greenblitz.motion.base.*;
import org.greenblitz.motion.base.abstraction.*;

import java.util.ArrayList;
import java.util.Arrays;

public class AdaptivePurePursuitController {
    private Path m_path;

    private Position m_robotLoc;

    public final double lookAhead;

    private final IChassis chasis;

    public AdaptivePurePursuitController(Path path, Position robotLoc, double lookAhead, IChassis chasis) {
        m_path = path;
        m_robotLoc = robotLoc;
        this.lookAhead = lookAhead;
        this.chasis = chasis;
    }

    public static double[] driveValuesTo(Position robotLoc, Point target, double wheelDist) {
        Point diff = Point.sub(target, robotLoc).rotate(-robotLoc.getAngle());
        double curvature = 2 * diff.getX() / Point.normSquared(diff);
        if (curvature == 0)
            return new double[]{1, 1};
        double radius = 1/curvature;
        double rightRadius = radius + wheelDist/2;
        double leftRadius = radius - wheelDist/2;
        if(curvature > 0)
            return new double[]{leftRadius/rightRadius, 1};
        else
            return new double[]{1, rightRadius/leftRadius};
    }

    public void iteration(){
        m_robotLoc = Localizer.getInstance().getLocation();
        double[] motorValues = driveValuesTo(m_robotLoc, m_path.intersection(m_robotLoc, lookAhead), chasis.getWheelbaseWidth());
        chasis.tankDrive(motorValues[0], motorValues[1]);
    }
}
