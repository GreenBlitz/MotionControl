package org.usfirst.frc.team4590.motion;

import org.usfirst.frc.team4590.robot.RobotMap;

/**
 * runs in a seperate thred calculating the robot position
 * @author Udi & Alexey
 *
 */
public class Localizer {

	private static Localizer instance = null;
	
	private Localizer(){
		m_wheelDistance = RobotMap.wheelDistance;
	}

	public static Localizer getInstance() {
		if (instance == null)
			instance = new Localizer();
		return instance;
	}

	private Position m_location;
	private double m_wheelDistance;
	
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
		return m_location.clone();
	}
	
	/**
	 * sets initial values of Localizer, functions as constructor.
	 * 
	 * @param initialLocation
	 */
	public void configure(Position initialLocation) {
		m_location = initialLocation;
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
		double dx = circleRadius * (1 - Math.cos(angle));
		
		synchronized(LOCK){
			m_location.translate(dx, dy);
		}
	}

}
