
package org.usfirst.frc.team4590.robot;

import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_LEFT_ENCODER_PORT_A;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_LEFT_ENCODER_PORT_B;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_RIGHT_ENCODER_PORT_A;
import static org.usfirst.frc.team4590.robot.RobotMap.CHASSIS_RIGHT_ENCODER_PORT_B;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import gbmotion.appc.APPCOutput;
import gbmotion.appc.APPController;
import gbmotion.appc.APPController.APPDriveData;
import gbmotion.appc.Localizer;
import gbmotion.base.DrivePort;
import gbmotion.base.ScaledEncoder;
import gbmotion.path.ArenaMap;
import gbmotion.path.PathFactory;
import gbmotion.util.PrintManager;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final PrintManager managedPrinter = new PrintManager();

	private Localizer loc;
	private APPCOutput out;
	private DrivePort rd;
	private APPController controller = null;
	private CSVLogger logger;
	private ArenaMap m_arenaMap;

	ScaledEncoder left;
	ScaledEncoder right;
	public AHRS gyro;

	private boolean setupFailed = false;
	private String setupFailureMessage = "";

	@Override
	public void disabledInit() {
		DrivePort.DEFAULT.tankDrive(0, 0);
		logger.disable();
		System.out.println("Am I Disabled?");
		if (controller != null) {
			controller = null;
		}

		logger.enable();

		loc.reset();

		
	}

	@Override
	public void autonomousInit() {
		new PathFactory().conncetLine(0, 1, 0.005).construct(m_arenaMap);
		loc.reset();

		controller = new APPController(loc, out, m_arenaMap);
		controller.setOutputConstrain((in) -> APPDriveData.of(Math.max(in.power, 0.6), in.dx, in.dy));
		controller.start();
	}

	@Override
	public void teleopInit() {
		logger.enable();

		loc.reset();

		NetworkTable motionTable = NetworkTable.getTable("motion");
		motionTable.putNumber("pathLength", 0);
	}

	@Override
	public void robotPeriodic() {
		if (setupFailed)
			throw new RuntimeException(setupFailureMessage);
	}

	@Override
	public void disabledPeriodic() {

	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		final double FULL_POWER = 0.8;
		rd.arcadeDrive(regulate(OI.getInstance().getJoystick().getRawAxis(1), FULL_POWER),
				regulate(OI.getInstance().getJoystick().getRawAxis(4), FULL_POWER));
	}

	private static double regulate(double velocity, final double FULL_POWER) {
		if (velocity < 0)
			velocity = Math.max(velocity, -FULL_POWER);
		else if (velocity > 0)
			velocity = Math.min(velocity, FULL_POWER);
		return velocity;
	}

	public Robot() {
	}

	@Override
	public void robotInit() {
		logger = new CSVLogger();
		left = new ScaledEncoder(CHASSIS_LEFT_ENCODER_PORT_A, CHASSIS_LEFT_ENCODER_PORT_B,
				-RobotStats.LEFT_ENCODER_SCALE);
		right = new ScaledEncoder(CHASSIS_RIGHT_ENCODER_PORT_A, CHASSIS_RIGHT_ENCODER_PORT_B,
				RobotStats.RIGHT_ENCODER_SCALE);
		gyro = new AHRS(SPI.Port.kMXP);
		if (!gyro.isConnected()) {
			System.err.println("WARNING: Gyro not connected!!!!");
			setupFailed = true;
			setupFailureMessage = "gyro isn't connected";
		}

		loc = Localizer.of(left, right, 0.68, gyro, Localizer.AngleCalculation.GYRO_BASED);
		rd = DrivePort.DEFAULT;
		out = new APPCOutput();
		m_arenaMap = new ArenaMap();
		initPrintables();
		OI.init(loc);
	}

	public static void KillMyself(String suicideLetter) {
		throw new RuntimeException(suicideLetter);
	}

	private void initPrintables() {
		// managedPrinter.registerPrintable(APPController.AbsoluteTolerance.class);
		// managedPrinter.registerPrintable(IterativeController.IterativeCalculationTask.class);
		// managedPrinter.registerPrintable(IterativeController.class);
		managedPrinter.registerPrintable(Localizer.LocalizeTimerTask.class);
		// managedPrinter.registerPrintable(Localizer.class);
		// managedPrinter.registerPrintable(APPCOutput.class);
		// managedPrinter.registerPrintable(APPController.class);
		// managedPrinter.registerPrintable(gbmotion.path.ArenaMap.class);
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
