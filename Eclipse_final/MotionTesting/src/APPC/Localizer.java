package APPC;

import base.Input;
import base.IterativeController;
import base.ScaledEncoder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * Finding the location of the robot
 * the front is the gear side
 */
// TODO: add wheel distance to robotmap
public class Localizer implements Input<Point2D> {	
    public static final double PERIOD = IterativeController.DEFAULT_PERIOD / 2;
    public static final Object LOCK = new Object();

    private Point2D m_location;
   
    private ScaledEncoder[] m_leftWrappedEncoders;
    private ScaledEncoder[] m_rightWrappedEncoders;
    private double m_wheelDistance;

    public Localizer(ScaledEncoder[] left, ScaledEncoder[] right, Point2D location,double wheelDistance) {
        m_location = location;
        m_leftWrappedEncoders = left;
        m_rightWrappedEncoders = right;
        m_wheelDistance = wheelDistance;
        Timer m_timer = new Timer();
        m_timer.schedule(new LocalizeTimerTask(), 0,(long) (1000 * PERIOD)); 
    }

    public Localizer(ScaledEncoder left, ScaledEncoder right, Point2D location, double wheelDistance) {
        this(new ScaledEncoder[]{left}, new ScaledEncoder[]{right}, location, wheelDistance);
    }
    
    public static Localizer of(ScaledEncoder left, ScaledEncoder right, double wheelDist) {
    	return new Localizer(left, right, new Point2D(0,0,0), wheelDist);
    }

    public double getLeftDistance() {
        return Arrays.stream(m_leftWrappedEncoders).map(ScaledEncoder :: getDistance).reduce((a, b) -> a + b).orElse(.0) /
                m_leftWrappedEncoders.length;
    }

    public double getRightDistance() {
        return Arrays.stream(m_rightWrappedEncoders).map(ScaledEncoder :: getDistance).reduce((a, b) -> a + b).orElse(.0) /
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
        	SmartDashboard.putNumber("Encoder Left", getLeftDistance());
        	SmartDashboard.putNumber("Right encoder", getRightDistance());
        	SmartDashboard.putNumber("X-pos R", m_location.getX());
            SmartDashboard.putNumber("Y-pos R", m_location.getY());
        	if (DriverStation.getInstance().isDisabled()) reset();
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
                    m_location = m_location.add(0, leftDistDiff);
                    return;
                }
            }
            
        	
    		boolean leftIsLong = leftDistDiff > rightDistDiff;
    		double shortDist = leftIsLong ? rightDistDiff : leftDistDiff;
    		
    		 
    		double angle = (rightDistDiff - leftDistDiff) / m_wheelDistance;

    		double radiusFromCenter = -(shortDist/angle + Math.signum(angle) * m_wheelDistance/2);
    		double adjustedRadiusFromCenter = radiusFromCenter;
    		Point2D rotationOrigin = m_location.add(adjustedRadiusFromCenter, 0);
            synchronized (LOCK){
                m_location = m_location.rotateRelativeToChange(rotationOrigin, angle);
            }
            System.out.println("WARNING - robot location: " +  m_location);
        }
    }

    @Override
    public Point2D recieve() {
        synchronized (LOCK){
            return m_location;
        }
    }
    
    private void reset() {
    	for (ScaledEncoder enc : m_leftWrappedEncoders)
    		enc.reset();
    
    	for (ScaledEncoder enc : m_rightWrappedEncoders)
    		enc.reset();
    	
        m_location = Point2D.GLOBAL_ORIGIN;
    }
    
}
