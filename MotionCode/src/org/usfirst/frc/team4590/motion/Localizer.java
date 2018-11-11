package org.usfirst.frc.team4590.motion;

import org.usfirst.frc.team4590.robot.RobotMap;

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
	 * sets initial values of Localizer, functions as constructor.
	 * 
	 * @param initialLocation
	 */
	public void configure(Position initialLocation) {
		m_location = initialLocation;
	}

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
