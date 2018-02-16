
package org.usfirst.frc.team4590.robot;

import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_LEFT_ENCODER_PORT_A;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_LEFT_ENCODER_PORT_B;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_RIGHT_ENCODER_PORT_A;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_RIGHT_ENCODER_PORT_B;

import com.kauailabs.navx.frc.AHRS;

import APPC.APPCOutput;
import APPC.APPController;
import APPC.Localizer;
import APPC.PathFactory;
import base.DrivePort;
import base.PrintManager;
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
	public static final double DEFUALT_ARENA_MAP_ACC = 0.1;
	public static final double DEFUALT_ARENA_LENGTH = 16.4592;
	public static final double DEFUALT_ARENA_WIDTH = 8.2296;

	public static final PrintManager managedPrinter = new PrintManager();

	private Localizer loc;
	private APPCOutput out;
	private DrivePort rd;
	private APPController controller = null;
	private CSVLogger logger;
	private APPC.ArenaMap m_arenaMap;

	ScaledEncoder left;
	ScaledEncoder right;
	AHRS gyro;

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
		new PathFactory().genStraightLine(1, 0, 0.005).construct(m_arenaMap);
		loc.reset();
		controller = new APPController(loc, out, m_arenaMap);
		controller.start();
	}

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
		m_arenaMap = new APPC.ArenaMap(DEFUALT_ARENA_MAP_ACC, DEFUALT_ARENA_LENGTH, DEFUALT_ARENA_WIDTH);
		initPrintables();
	}

	private void initPrintables() {
		// p.registerPrintable(APPController.AbsoluteTolerance.class);
		// p.registerPrintable(IterativeController.IterativeCalculationTask.class);
		managedPrinter.registerPrintable(Localizer.LocalizeTimerTask.class);
		// p.registerPrintable(APPCOutput.class);
		managedPrinter.registerPrintable(APPController.class);
	}

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
