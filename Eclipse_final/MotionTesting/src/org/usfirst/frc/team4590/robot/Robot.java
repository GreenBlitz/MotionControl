
package org.usfirst.frc.team4590.robot;

import com.ctre.CANTalon;

import APPC.APPCOutput;
import APPC.APPController;
import APPC.Localizer;
import APPC.Path;
import APPC.PathFactory;
import APPC.Point2D;
import base.WrappedEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	private Localizer loc;
	private APPCOutput out;
	private RobotDrive rd;
	private APPController controller = null;

	@Override
	public void disabledInit() {
		System.out.println("Am I Disabled?");
		if (controller != null) {
			controller = null;

			// System.gc();
		}

	}
	Point2D GP;
	@Override
	public void autonomousInit() {
		System.out.println("auto Init");
		Path myPath = new PathFactory().genForwardPath(1.5, false, 0.002).construct();
		GP = myPath.getLast();
		controller = new APPController(loc, out, myPath);
		controller.start();
	}
	// 0.49 m


	@Override
	public void teleopInit() {

	}

	@Override
	public void robotPeriodic() {
	}

	@Override
	public void disabledPeriodic() {

	}

	@Override
	public void autonomousPeriodic() {
		if (loc.recieve().distance(GP) <= 0.5)
			controller.stop();
	}

	@Override
	public void teleopPeriodic() {

		// TODO Auto-generated method stub
		/*
		 * rd.tankDrive(0.4, 0.4, false); System.out.println(loc.recieve());
		 */
		Joystick dispairStick = new Joystick(0);
		rd.arcadeDrive(dispairStick.getRawAxis(1), dispairStick.getRawAxis(4));
	}

	private static Robot instance;
	/*
	 * double scale = 2.4/650;
	 * 
	 * //WrappedEncoder[] leftEncoders = {new WrappedEncoder(new Encoder(0),a),new
	 * WrappedEncoder()}; Point2D p = new Point2D(0.0,0.0,0.0); Localizer l = new
	 * Localizer(new WrappedEncoder(new Encoder(2,3),scale),new WrappedEncoder(new
	 * Encoder(0,1),scale),p,70.0); APPCOutput ooo = new
	 * APPCOutput(Chassis.getInstance().<RobotDrive>getActuator("Robot Drive"));
	 * /*new RobotDrive( new CANTalon(RobotMap.CHASSIS_FRONT_LEFT_MOTOR_PORT), new
	 * CANTalon(RobotMap.CHASSIS_REAR_LEFT_MOTOR_PORT), new
	 * CANTalon(RobotMap.CHASSIS_FRONT_RIGHT_MOTOR_PORT), new
	 * CANTalon(RobotMap.CHASSIS_REAR_RIGHT_MOTOR_PORT)));
	 */

	public Robot() {
	}

	@Override
	public void robotInit() {

		// double scale = 2.4/650;
		double scale = 0.0036;// (1.0 / 220.5);// * POWER_GEARS_RATIO /
		// SPEED_GEARS_RATIO;

		// WrappedEncoder[] leftEncoders = {new WrappedEncoder(new
		// Encoder(0),a),new WrappedEncoder()};

		// WrappedEncoder[] leftEncoders = {new WrappedEncoder(new
		// Encoder(0),a),new WrappedEncoder()};
		System.out.println("robo");
		loc = Localizer.of(new WrappedEncoder(new Encoder(2, 3), -scale), new WrappedEncoder(new Encoder(0, 1), scale),
				0.68);
		rd = new RobotDrive(new CANTalon(RobotMap.CHASSIS_FRONT_LEFT_MOTOR_PORT),
				new CANTalon(RobotMap.CHASSIS_REAR_LEFT_MOTOR_PORT),
				new CANTalon(RobotMap.CHASSIS_FRONT_RIGHT_MOTOR_PORT),
				new CANTalon(RobotMap.CHASSIS_REAR_RIGHT_MOTOR_PORT));
		out = new APPCOutput(rd);
	}

}
