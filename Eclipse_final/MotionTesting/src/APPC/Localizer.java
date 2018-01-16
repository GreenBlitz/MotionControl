package APPC;

import base.Input;
import base.WrappedEncoder;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by karlo on 10/01/2018.
 * Finding the location of the robot
 */
// TODO: implement a constructor with just 2 WrappedEncoders
// TODO: implement getRealDistance - URGENT
public class Localizer implements Input<Point2D> {
    public static final double PERIOD = 0.005;
    public static final Object LOCK = new Object();

    private Point2D   m_location;
    private WrappedEncoder[] m_leftWrappedEncoders;
    private WrappedEncoder[] m_rightWrappedEncoders;
    private double m_wheelDistance;

    public Localizer(WrappedEncoder[] left, WrappedEncoder[] right, Point2D location,double wheelDistance) {
        m_location = location;
        m_leftWrappedEncoders = left;
        m_rightWrappedEncoders = right;
        m_wheelDistance = wheelDistance;
        Timer m_timer = new Timer();
        m_timer.schedule(new LocalizeTimerTask(), 0,(long) (1000 * PERIOD));    }

    public Localizer(WrappedEncoder left, WrappedEncoder right, Point2D location, double wheelDistance) {
        this(new WrappedEncoder[]{left}, new WrappedEncoder[]{right}, location, wheelDistance);
    }

    public double getLeftDistance() {
        return Arrays.stream(m_leftWrappedEncoders).map(WrappedEncoder :: getDistance).reduce((a, b) -> a + b).orElse(.0) /
                m_leftWrappedEncoders.length;
    }

    public double getRightDistance() {
        return Arrays.stream(m_rightWrappedEncoders).map(WrappedEncoder :: getDistance).reduce((a, b) -> a + b).orElse(.0) /
                m_rightWrappedEncoders.length;
    }

    private class LocalizeTimerTask extends TimerTask {
        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {
            double rightDist = getRightDistance();
            double leftDist = getLeftDistance();
            if (leftDist == rightDist) {
                synchronized (LOCK) {
                    m_location = m_location.moveBy(0, leftDist);
                    return;
                }
            }
            double shortDist, longDist;
            boolean leftIsLong;
            if (leftDist > rightDist){
                leftIsLong = true;
                longDist = leftDist;
                shortDist = rightDist;
            } else {
                leftIsLong = false;
                longDist = rightDist;
                shortDist = leftDist;
            }
            double angle = (longDist - shortDist) / m_wheelDistance;
            double radiusFromCenter = shortDist/angle + m_wheelDistance/2;
            double adjustedRadiusFromCenter = leftIsLong ? -radiusFromCenter : radiusFromCenter;
            Point2D rotationOrigin = m_location.moveBy(adjustedRadiusFromCenter, 0);
            synchronized (LOCK){
                m_location = m_location.rotateRelativeTo(rotationOrigin, angle);
            }
        }
    }

    @Override
    public Point2D recieve() {
        synchronized (LOCK){
            return m_location;
        }
    }
}
