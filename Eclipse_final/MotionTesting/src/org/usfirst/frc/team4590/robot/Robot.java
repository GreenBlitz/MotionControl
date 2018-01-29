
package org.usfirst.frc.team4590.robot;

import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_LEFT_ENCODER_PORT_A;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_LEFT_ENCODER_PORT_B;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_RIGHT_ENCODER_PORT_A;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_RIGHT_ENCODER_PORT_B;

import com.kauailabs.navx.frc.AHRS;

import APPC.APPCOutput;
import APPC.APPController;
import APPC.APPController.APPDriveData;
import APPC.Localizer;
import APPC.Orientation2D;
import APPC.Path;
import APPC.PathFactory;
import base.DrivePort;
import base.ScaledEncoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;

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
	private DrivePort rd;
	private APPController controller = null;
	private CSVLogger logger;

	// sensors
	ScaledEncoder left;
	ScaledEncoder right;
	AHRS gyro;

	public void logAllSensors() {
		logger.log("Gyro angle", gyro.getAngle());
		logger.log("Gyro yaw", gyro.getYaw());
		logger.log("Gyro pitch", gyro.getPitch());
		logger.log("Gyro roll", gyro.getRoll());

		logger.log("Gyro accel x", gyro.getWorldLinearAccelX());
		logger.log("Gyro accel y", gyro.getWorldLinearAccelY());
		logger.log("Gyro accel z", gyro.getWorldLinearAccelZ());

		logger.log("Gyro disp x (bad)", gyro.getDisplacementX());
		logger.log("Gyro disp y (bad)", gyro.getDisplacementY());
		logger.log("Gyro disp z (bad)", gyro.getDisplacementZ());

		logger.log("Gyro orr dick", 1);

		logger.log("Gyro bar pressure", gyro.getBarometricPressure());
		logger.log("Robot altitude", gyro.getAltitude());

		logger.log("Gyro RAW accel x", gyro.getRawAccelX());
		logger.log("Gyro RAW accel z", gyro.getRawAccelZ());
		logger.log("Gyro RAW accel y", gyro.getRawAccelY());
		logger.log("Gyro RAW gyro x", gyro.getRawGyroX());
		logger.log("Gyro RAW gyro y", gyro.getRawGyroY());
		logger.log("Gyro RAW gyro z", gyro.getRawGyroZ());

		logger.log("Gyro rotation rate", gyro.getRate());

		logger.log("Encoder speed left", getSpeedL());
		logger.log("Encoder speed right", getSpeedR());
		logger.log("Encoder distsance left", left.getDistance());
		logger.log("Encoder distance right", right.getDistance());
		logger.log("Encoder RAW left", left.getRaw());
		logger.log("Encoder RAW right", right.getRaw());
		logger.log("Encoder distance", getDistance());
		logger.log("Encoder speed", getSpeed());
	}

	@Override
	public void disabledInit() {
		logger.disable();
		System.out.println("Am I Disabled?");
		if (controller != null) {
			controller = null;
		}
	}

	@Override
	public void autonomousInit() {
		logger.enable();
		loc.reset();
		Path myPath = new PathFactory().genStraightLine(1, 0, 0.005).construct();
		controller = new APPController(loc, out, myPath);
		controller.setOutputConstrain(data -> new APPDriveData(data.power * 0.5, data.curve));
		controller.start();
	}
	// 0.49 m

	@Override
	public void teleopInit() {
		logger.enable();
	}

	@Override
	public void robotPeriodic() {
	}

	@Override
	public void disabledPeriodic() {

	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopPeriodic() {
		logAllSensors();
		Joystick dispairStick = new Joystick(0);
		rd.arcadeDrive(dispairStick.getRawAxis(1), dispairStick.getRawAxis(4));
	}

	public Robot() {
	}

	@Override
	public void robotInit() {
		logger = new CSVLogger();
		left = new ScaledEncoder(CHASSIS_LEFT_ENCODER_PORT_A, CHASSIS_LEFT_ENCODER_PORT_B, -RobotStats.ENCODER_SCALE);
		right = new ScaledEncoder(CHASSIS_RIGHT_ENCODER_PORT_A, CHASSIS_RIGHT_ENCODER_PORT_B, RobotStats.ENCODER_SCALE);
		gyro = new AHRS(SPI.Port.kMXP);
		loc = Localizer.of(left, right, 0.68);
		rd = DrivePort.DEFAULT;
		out = new APPCOutput();
		loc = Localizer.of(
				new ScaledEncoder(RobotMap.CHASSIS_LEFT_ENCODER_PORT_A, RobotMap.CHASSIS_LEFT_ENCODER_PORT_B, 
						RobotStats.CHASSIS_LEFT_ENCODER_INVERT, scale), 
				new ScaledEncoder(RobotMap.CHASSIS_RIGHT_ENCODER_PORT_A, RobotMap.CHASSIS_RIGHT_ENCODER_PORT_B,
						RobotStats.CHASSIS_RIGHT_ENCODER_INVERT, scale),
				0.68);
	}

	// getters for sensors
	public double getDistance() {
		return (left.getDistance() - right.getDistance()) / 2;
	}

	public double getSpeed() {
		return (left.getRate() - right.getRate()) / 2;
	}

	public double getSpeedL() {
		return -right.getRate();
	}

	public double getSpeedR() {
		return left.getRate();
	}
}
