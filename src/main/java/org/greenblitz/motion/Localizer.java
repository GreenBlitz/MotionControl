package org.greenblitz.motion;

import org.greenblitz.motion.utils.SmartEncoder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * runs in a seperate thread calculating the robot position
 * @author Udi & Alexey
 *
 */
public class Localizer extends TimerTask {

	private static Localizer instance = null;

	public static Localizer getInstance() {
		if (instance == null)
			instance = new Localizer();
		return instance;
	}

	private Position m_location;
	private double m_wheelDistance;
	private SmartEncoder leftEncoder;
	private SmartEncoder rightEncoder;
	
	private double prevDistanceLeft;
	private double prevDistanceRight;
	
	private static long SLEEP_TIME = 20L;
	
	private final Object LOCK = new Object();

	/**
	 * <p>Get the robot location. This is the system: </p>
	 *                      ^<br>
	 *                      |<br>
	 *                      |<br>
	 *                      R ---->
	 * <br> <br> Where 'R' is the robot, up is the y coord and right is the x coord
	 * @return
	 */
	public Position getLocation(){
		synchronized (LOCK) {
			return m_location.clone();

		}
	}
	
	/**
	 *
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
	 * Reset prevDistanceLeft and prevDistanceRight.
	 * You want to call this when reseting encoders for example
	 */
	public void resetEncoderDistances(){
		prevDistanceLeft = leftEncoder.getDistance();
		prevDistanceRight = rightEncoder.getDistance();
	}
	
	/**
	 * calculateDiff the location
	 * @param rightDist distance right wheel traveled
	 * @param leftDist distance left wheel traveled
     * @return x difference, y difference, angle difference
	 */
	public static double[] calculateDiff(double rightDist, double leftDist, double wheelDistance) {
	    if(rightDist == leftDist)
            return new double[]{0, rightDist, 0};
		double distance = (rightDist + leftDist) / 2;
		double angle = (rightDist - leftDist) / wheelDistance;
		
		double circleRadius = distance / angle;
		
		double dy = circleRadius * Math.sin(angle);
		double dx = -circleRadius * (1 - Math.cos(angle));

		return new double[]{dx, dy, angle};
	}

	@Override
	public void run() {
		double encL = leftEncoder.getDistance(),
                encR = rightEncoder.getDistance();
		double[] dXdYdAngle = calculateDiff(encR - prevDistanceRight, encL - prevDistanceLeft, m_wheelDistance);
		synchronized (LOCK){
		    m_location.translate(dXdYdAngle[0], dXdYdAngle[1]);
		    m_location.changeAngleBy(dXdYdAngle[2]);
        }
		prevDistanceLeft = encL;
		prevDistanceRight = encR;

	}

	public static void startLocalizer(){
        Timer t = new Timer();
        t.schedule(getInstance(), 0, SLEEP_TIME);
    }

}
