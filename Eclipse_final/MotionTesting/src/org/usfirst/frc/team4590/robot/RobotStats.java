package org.usfirst.frc.team4590.robot;

public final class RobotStats {
	
	public static final double HORIZONTAL_WHEEL_DIST = 0.68,
							   VERTICAL_WHEEL_DIST = 0.7,
							   WHEEL_RADIUS = 0.045,
							   SPEED_GEARS_RATIO = 4.17,
	                           POWER_GEARS_RATIO = 11.03,
	                           ENCODER_SCALE = 0.0036,
							   LEFT_ENCODER_SCALE = 168/45281.0,
							   RIGHT_ENCODER_SCALE = 168/45321.0,
							   ENCODER_TICKS_PER_RADIAN = 1 / Math.PI;
							    
	public static final boolean CHASSIS_LEFT_ENCODER_INVERT = true,
								CHASSIS_RIGHT_ENCODER_INVERT = false;

	
	
}
