package org.greenblitz.motion;

import org.greenblitz.motion.base.*;

public class AdaptivePurePursuitController {
    private Path m_path;
    private Position m_robotLoc;
    public final double lookAhead;

    public AdaptivePurePursuitController(Path path, Position robotLoc, double lookAhead) {
        m_path = path;
        m_robotLoc = robotLoc;
        this.lookAhead = lookAhead;
    }

    private double[] driveTo(Point target) {
        Point diff = Point.sub(target, m_robotLoc).rotate(-m_robotLoc.getAngle());
        double curvature = 2 * diff.getX() / Point.normSquared(diff);
        if (curvature >= 0)
            return new double[]{1 - 2 * (curvature + 1), 1};
        else
            return new double[]{1, 1 + 2 * (curvature - 1)};
    }

    public double[] nextDriveValues(){
        m_robotLoc = Localizer.getInstance().getLocation();
        return driveTo(m_path.intersection(m_robotLoc, lookAhead));
    }
}
