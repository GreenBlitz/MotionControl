package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.sql.ResultSetMetaData;

/**
 * runs in a seperate thread calculating the org.greenblitz.example.robot position
 *
 * @author Udi    ~ MudiAtalon
 * @author Alexey ~ savioor
 */
public class Localizer {

    private Position m_location = new Position(0, 0, 0); // Positive x direction is left

    private double m_wheelDistance;

    private double prevDistanceLeft;
    private double prevDistanceRight;

    private final Object LOCK = new Object();

    public Localizer(double wheelBase) {
        m_wheelDistance = wheelBase;
    }


    /**
     * <p>Get the org.greenblitz.example.robot location. This is the system: </p>
     * ^<br>
     * |<br>
     * |<br>
     * <--- R
     * <br> <br> Where 'R' is the org.greenblitz.example.robot, up is the y coord and left is the x coord
     *
     * @return
     */
    public Position getLocation() {
        synchronized (LOCK) {
            return m_location.clone();
        }
    }

    /**
     * sets initial values of Localizer, functions as constructor.
     *
     * @param initialLocation
     * @param leftTicks
     * @param rightTicks
     */
    public void configure(Position initialLocation, double wheelDistance, int leftTicks, int rightTicks) {
        m_location = initialLocation;
        m_wheelDistance = wheelDistance;
        prevDistanceLeft = leftTicks;
        prevDistanceRight = rightTicks;
    }

    /**
     * Reset prevDistanceLeft and prevDistanceRight.
     * You want to call this when reseting encoders for example
     */
    public void reset(double currentLeftDistance, double currentRightDistance) {
        prevDistanceLeft = currentLeftDistance;
        prevDistanceRight = currentRightDistance;
        m_location.set(0, 0, 0);
    }

    /**
     * calculateMovement the location
     *
     * @param rightDist distance right wheel traveled
     * @param leftDist  distance left wheel traveled
     * @param robotAng  the angle of the robot
     * @return x difference, y difference, angle difference
     */
    public static Point calculateMovement(double rightDist, double leftDist, double wheelDistance, double robotAng) {
        if (rightDist == leftDist) {
            return new Point(0, rightDist).rotate(robotAng);
        }

        double distance = (rightDist + leftDist) / 2;
        double angle = (rightDist - leftDist) / wheelDistance;
        double circleRadius = distance / angle;

        double dy = circleRadius * Math.sin(angle);
        double dx = circleRadius * (1 - Math.cos(angle));

        return new Point(dx, dy).rotate(robotAng);
    }

    public void update(double currentLeftDistance, double currentRightDistance) {
        Point dXdY = calculateMovement(
                currentRightDistance - prevDistanceRight, currentLeftDistance - prevDistanceLeft,
                m_wheelDistance, m_location.getAngle());

        synchronized (LOCK) {
            m_location.translate(dXdY);
            m_location.setAngle((currentRightDistance - currentLeftDistance) / m_wheelDistance);
        }

        prevDistanceLeft = currentLeftDistance;
        prevDistanceRight = currentRightDistance;
    }
}
