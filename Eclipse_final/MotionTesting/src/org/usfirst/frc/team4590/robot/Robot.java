
package org.usfirst.frc.team4590.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Scheduler;
import gbmotion.PIDController.PIDController;
import gbmotion.appc.APPCOutput;
import gbmotion.appc.APPController;
import gbmotion.appc.Localizer;
import gbmotion.base.DrivePort;
import gbmotion.path.ArenaMap;
import gbmotion.util.PrintManager;
import gbmotion.util.SmartEncoder;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final PrintManager managedPrinter = new PrintManager();

	public static final double FULL_POWER = 0.8;
	private Localizer loc;
	private APPCOutput output;
	private DrivePort rd;
	private APPController controller = null;

	private CSVLogger logger;
	private ArenaMap m_arenaMap;

	SmartEncoder left;
	SmartEncoder right;
	public AHRS gyro;

	public static boolean kms = false;
	public static String suicideLetter = "";
	public static Throwable suicideCause = null;

	private PIDController m_PID;

	@Override
	public void disabledInit() {
		logger.disable();
		System.out.println("Am I Disabled?");
		m_PID = null;
		if (controller != null) {
			controller = null;
		}

		logger.enable();
		loc.reset();
	}

	@Override
	public void autonomousInit() {
		/*
		 * new PathFactory().conncetLine(0, 1, 0.005).construct(m_arenaMap);
		 * loc.reset();
		 * 
		 * controller = new APPController(loc, out, m_arenaMap);
		 * controller.setOutputConstrain((in) ->
		 * APPDriveData.of(Math.max(in.power, 0.6), in.dx, in.dy));
		 * controller.start();
		 */
		// m_PID = new AngularPID(new GyroSampler(gyro), new AngleOutput(rd),
		// -Math.PI / 2, 0.5, 1 / 12 * Math.PI);
		// m_PID.start();

	}

	@Override
	public void teleopInit() {
		loc.reset();
		rd.setPowerLimit(FULL_POWER);
	}

	@Override
	public void robotPeriodic() {
		if (kms)
			if (suicideCause == null)
				throw new RuntimeException(suicideLetter);
			else
				throw new RuntimeException(suicideCause);
	}

	@Override
	public void disabledPeriodic() {
		DrivePort.DEFAULT.tankDrive(0, 0);
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		rd.arcadeDrive(OI.getInstance().getJoystick().getRawAxis(1), OI.getInstance().getJoystick().getRawAxis(4),
				true);
	}

	public Robot() {
	}

	@Override
	public void robotInit() {

		logger = new CSVLogger();
		left = rd.getEncoder(false);
		right = rd.getEncoder(true);
		gyro = new AHRS(SPI.Port.kMXP);
		while (gyro.isCalibrating()) {
		}
		/*
		 * if (!gyro.isConnected()) { System.err.println(
		 * "WARNING: Gyro not connected!!!!"); kms = true; suicideLetter =
		 * "gyro isn't connected"; }
		 */
		gyro.reset();
		loc = Localizer.of(left, right, 0.68, gyro, Localizer.AngleDifferenceCalculation.GYRO_BASED);
		rd = DrivePort.DEFAULT;
		output = new APPCOutput();
		m_arenaMap = new ArenaMap();
		initPrintables();
		OI.init(loc);
	}

	public static void killMySelf(String suicideLetter) {
		Robot.suicideLetter = suicideLetter;
		kms = true;
	}

	public static void killMySelf(Throwable cause) {
		Robot.suicideCause = cause;
		kms = true;
	}

	private void initPrintables() {
	}

	public double getDistance() {
		return (left.getDistance() - right.getDistance()) / 2;
	}

	public double getSpeed() {
		return (left.getSpeed() - right.getSpeed()) / 2;
	}

	public double getSpeedL() {
		return -right.getSpeed();
	}

	public double getSpeedR() {
		return left.getSpeed();
	}
}
