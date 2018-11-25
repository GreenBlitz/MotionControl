package org.greenblitz.motion;

import org.greenblitz.utils.SmartEncoder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * runs in a seperate thread calculating the org.greenblitz.robot position
 *
 * @author Udi & Alexey
 */
public class Localizer extends TimerTask {

    private static Localizer instance = null;

    private Localizer() {
    }

    public static Localizer getInstance() {
        if (instance == null)
            instance = new Localizer();
        return instance;
    }


    private Position m_location;//Positive x direction is left

    private double m_wheelDistance;
    private SmartEncoder leftEncoder;
    private SmartEncoder rightEncoder;

    private double prevDistanceLeft;
    private double prevDistanceRight;

    private static long SLEEP_TIME = 20L;

    private final Object LOCK = new Object();

    /**
     * <p>Get the org.greenblitz.robot location. This is the system: </p>
     * ^<br>
     * |<br>
     * |<br>
     * R ---->
     * <br> <br> Where 'R' is the org.greenblitz.robot, up is the y coord and right is the x coord
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
     * @param left
     * @param right
     */
    public void configure(Position initialLocation, double wheelDistance, SmartEncoder left, SmartEncoder right) {
        m_location = initialLocation;
        m_wheelDistance = wheelDistance;
        leftEncoder = left;
        rightEncoder = right;
        prevDistanceLeft = left.getDistance();
        prevDistanceRight = right.getDistance();
    }

    /**
     * @param wheelDistance
     * @param left
     * @param right
     */
    public void configure(double wheelDistance, SmartEncoder left, SmartEncoder right) {
        configure(new Position(0, 0), wheelDistance, left, right);
    }

    /**
     * Reset prevDistanceLeft and prevDistanceRight.
     * You want to call this when reseting encoders for example
     */
    public void resetEncoderDistances() {
        prevDistanceLeft = leftEncoder.getDistance();
        prevDistanceRight = rightEncoder.getDistance();
    }

    /**
     * calculateMovement the location
     *
     * @param rightDist distance right wheel traveled
     * @param leftDist  distance left wheel traveled
     * @param robotAng  the angle of the robot
     * @return x difference, y difference, angle difference
     */
    public static Position calculateMovement(double rightDist, double leftDist, double wheelDistance, double robotAng) {
        if (rightDist == leftDist) {
            return new Position(new Point(0, rightDist).rotate(robotAng), 0);
        }
        double distance = (rightDist + leftDist) / 2;
        double angle = (rightDist - leftDist) / wheelDistance;

        double circleRadius = distance / angle;

        double dy = circleRadius * Math.sin(angle);
        double dx = circleRadius * (1 - Math.cos(angle));

        return new Position(new Point(dx, dy).rotate(robotAng), angle);

    }

    @Override
    public void run() {
        double encL = leftEncoder.getDistance(),
                encR = rightEncoder.getDistance();
        Position dXdYdAngle = calculateMovement(encR - prevDistanceRight, encL - prevDistanceLeft,
                m_wheelDistance, Localizer.getInstance().getLocation().getAngle());
        synchronized (LOCK) {
            m_location.translate(dXdYdAngle);
            m_location.changeAngleBy(dXdYdAngle.angle);
        }

        prevDistanceLeft = encL;
        prevDistanceRight = encR;
    }

    public static void startLocalizer() {
        Timer t = new Timer();
        t.schedule(getInstance(), 0, SLEEP_TIME);
    }

}
