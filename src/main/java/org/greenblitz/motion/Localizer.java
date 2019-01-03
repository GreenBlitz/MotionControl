package org.greenblitz.motion;

import org.greenblitz.motion.base.IEncoder;
import org.greenblitz.motion.base.IGyro;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.util.Timer;
import java.util.TimerTask;

/**
 * runs in a seperate thread calculating the org.greenblitz.robot position
 *
 * @author Udi & Alexey
 */
public class Localizer extends TimerTask {

    private static Localizer instance = new Localizer();

    public static Localizer getInstance() {
        return instance;
    }

    private Localizer() {

    }

    private Position m_location; //Positive x direction is left

    private double m_wheelDistance;
    private IEncoder leftEncoder;
    private IEncoder rightEncoder;
    private IGyro gyro;

    private boolean m_enable = false;

    private double prevDistanceLeft;
    private double prevDistanceRight;

    private static long SLEEP_TIME = 50L;

    private static Timer localizerTimer = new Timer();

    private final Object LOCK = new Object();


    /**
     * <p>Get the org.greenblitz.robot location. This is the system: </p>
     * ^<br>
     * |<br>
     * |<br>
     * <--- R
     * <br> <br> Where 'R' is the org.greenblitz.robot, up is the y coord and left is the x coord
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
    public void configure(Position initialLocation, double wheelDistance, IEncoder left, IEncoder right, IGyro gyro) {
        m_location = initialLocation;
        m_wheelDistance = wheelDistance;
        leftEncoder = left;
        rightEncoder = right;
        prevDistanceLeft = left.getDistance();
        prevDistanceRight = right.getDistance();
        this.gyro = gyro;
    }

    /**
     * @param wheelDistance
     * @param left
     * @param right
     */
    public void configure(double wheelDistance, IEncoder left, IEncoder right, IGyro gyro) {
        configure(new Position(0, 0), wheelDistance, left, right, gyro);
    }

    /**
     * Reset prevDistanceLeft and prevDistanceRight.
     * You want to call this when reseting encoders for example
     */
    public void reset() {
        prevDistanceLeft = leftEncoder.getDistance();
        prevDistanceRight = rightEncoder.getDistance();
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
            return new Position(new Point(0, rightDist).rotate(robotAng), 0);
        }

        double distance = (rightDist + leftDist) / 2;
        double angle = (rightDist - leftDist) / wheelDistance;
        double circleRadius = distance / angle;

        double dy = circleRadius * Math.sin(angle);
        double dx = circleRadius * (1 - Math.cos(angle));
        return new Point(dx, dy).rotate(robotAng);
    }

    @Override
    public void run() {
        if (m_enable) whenEnabled();
        else whenDisabled();
    }

    private void whenEnabled() {
        double encL = getLeftDistance(),
                encR = getRightDistance();
        Point dXdY = calculateMovement(
                encR - prevDistanceRight, encL - prevDistanceLeft,
                m_wheelDistance, Localizer.getInstance().getLocation().getAngle());

        synchronized (LOCK) {
            m_location.translate(dXdY);
            m_location.setAngle((encR - encL) / m_wheelDistance);
        }

        prevDistanceLeft = encL;
        prevDistanceRight = encR;
    }

    private void whenDisabled() {

    }

    private void enable() {
        m_enable = true;
    }

    private void disable() {
        m_enable = false;
    }

    private double getLeftDistance() {
        return leftEncoder.getDistance();
    }

    private double getRightDistance() {
        return rightEncoder.getDistance();
    }

    public static void startLocalizer() {
        localizerTimer.schedule(getInstance(), 0, SLEEP_TIME);
        getInstance().enable();
    }

    public static void stopLocalizer() {
        getInstance().disable();
    }

    public static boolean isActive() {
        return getInstance().m_enable;
    }
}
