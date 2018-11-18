package org.greenblitz.motion;

import edu.wpi.first.wpilibj.Encoder;

/**
 * runs in a seperate thread calculating the robot position
 * @author Udi & Alexey
 *
 */
public class Localizer implements Runnable {

	private static Localizer instance = null;

	public static Localizer getInstance() {
		if (instance == null)
			instance = new Localizer();
		return instance;
	}

	private Position m_location;
	private double m_wheelDistance;
	private Encoder leftEncoder;
	private Encoder rightEncoder;
	
	private double prevDistanceLeft;
	private double prevDistanceRight;
	
	private long sleepTime = 20;
	
	public Object LOCK;

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
	public void configure(Position initialLocation, double wheelDistance, Encoder left, Encoder right) {
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
	 * update the location
	 * @param rightDist distance right wheel traveled
	 * @param leftDist distance left wheel traveled
	 */
	private void run(double rightDist, double leftDist) {
		double distance = (rightDist + leftDist) / 2;
		double angle = (rightDist - leftDist) / m_wheelDistance;
		
		double circleRadius = distance / angle;
		
		double dy = circleRadius * Math.sin(angle);
		double dx = -circleRadius * (1 - Math.cos(angle));
		
		synchronized(LOCK){
			m_location.translate(dx, dy);
		}
	}

	@Override
	public void run() {
		double encL = leftEncoder.getDistance();
		double encR = rightEncoder.getDistance();
		
		run(encR - prevDistanceRight, encL - prevDistanceLeft);
		prevDistanceLeft = encL;
		prevDistanceRight = encR;
		
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
