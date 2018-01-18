package APPC;

import base.Input;
import base.WrappedEncoder;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by karlo on 10/01/2018.
 * Finding the location of the robot
 */
// TODO: add wheel distance to robotmap
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
    
    public static Localizer of(WrappedEncoder left, WrappedEncoder right, double wheelDist) {
    	return new Localizer(left, right, new Point2D(0,0,0), wheelDist);
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

    	private double leftDist;
    	private double rightDist;
        @Override
        public void run() {
        	//System.out.println("i");
        	// Equivalent to reading the encoder value and storing it only once - then assigning the difference between the last distance and the current one
            double rightDistDiff = -rightDist;
            double leftDistDiff = -leftDist;
            leftDist = getLeftDistance();
        	rightDist = getRightDistance();
        	rightDistDiff += rightDist;
        	leftDistDiff += leftDist;
        	

            if (leftDistDiff == rightDistDiff) {
                synchronized (LOCK) {
                    m_location = m_location.moveBy(0, leftDistDiff);
                    return;
                }
            }
            
        	
    		boolean leftIsLong = leftDistDiff > rightDistDiff;
    		double shortDist = leftIsLong ? rightDistDiff : leftDistDiff;
    		
    		 
    		double angle = (rightDistDiff - leftDistDiff) / m_wheelDistance;

    		double radiusFromCenter = -(shortDist/angle + Math.signum(angle) * m_wheelDistance/2);
    		double adjustedRadiusFromCenter = radiusFromCenter;
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