package org.usfirst.frc.team4590.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	// Joystick ports
	public static final int MAINJS_ID = 0, MAINJS_VIRTUALID = 0;

	// Chassis motor ports
	public static final int CHASSIS_FRONT_LEFT_MOTOR_PORT = 3, CHASSIS_REAR_LEFT_MOTOR_PORT = 8,
			CHASSIS_FRONT_RIGHT_MOTOR_PORT = 2, CHASSIS_REAR_RIGHT_MOTOR_PORT = 10;

	// Catapult ports
	public static final int CATAPULT_MOTOR_PORT = 0, CATAPULT_MIRCOSWITCH_PORT = 0;

	// Intake ports
	public static final int INTAKE_RIGHT_MOTOR_PORT = 0, INTAKE_LEFT_MOTOR_PORT = 0, INTAKE_MIRCOSWITCH_PORT = 0;

	// Intake pitcher ports
	public static final int INTAKE_PITCHER_MOTOR_PORT = 0, INTAKE_POTENTIOMETER_PORT = 0;

	// Pitcher state values
	public static final int INTAKE_PITCHER_COLLECT_VALUE = 0, INTAKE_PITCHER_DEFAULT_VALUE = 0,
			INTAKE_PITCHER_SWITCH_VALUE = 0, INTAKE_PITCHER_SCALE_VALUE = 0;
}
