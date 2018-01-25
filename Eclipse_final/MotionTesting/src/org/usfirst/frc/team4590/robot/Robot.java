
package org.usfirst.frc.team4590.robot;

import APPC.APPCOutput;
import APPC.APPController;
import APPC.Localizer;
import APPC.Path;
import APPC.PathFactory;
import base.DrivePort;
import base.ScaledEncoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

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

	@Override
	public void disabledInit() {
		System.out.println("Am I Disabled?");
		if (controller != null) {
			controller = null;
		}
	}

	@Override
	public void autonomousInit() {
		System.out.println("auto Init");
		Path myPath = new PathFactory().genForwardPath(1, false, 0.002).construct();
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
	}

	@Override
	public void teleopPeriodic() {
		Joystick dispairStick = new Joystick(0);
		rd.arcadeDrive(dispairStick.getRawAxis(1), dispairStick.getRawAxis(4));
	}

	public Robot() {
	}

	@Override
	public void robotInit() {
		loc = Localizer.of(
				new ScaledEncoder(RobotMap.CHASSIS_LEFT_ENCODER_PORT_A, RobotMap.CHASSIS_LEFT_ENCODER_PORT_B,
						-RobotStats.ENCODER_SCALE),
				new ScaledEncoder(RobotMap.CHASSIS_RIGHT_ENCODER_PORT_A, RobotMap.CHASSIS_RIGHT_ENCODER_PORT_B,
						RobotStats.ENCODER_SCALE),
				0.68);
		rd = DrivePort.DEFAULT;
		out = new APPCOutput();
	}
}
