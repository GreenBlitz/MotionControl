
package org.usfirst.frc.team4590.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Scheduler;
import gbmotion.appc.APPCOutput;
import gbmotion.appc.APPController;
import gbmotion.appc.Localizer;
import gbmotion.base.DrivePort;
import gbmotion.path.ArenaMap;
import gbmotion.path.PathFactory;
import gbmotion.util.PrintManager;
import gbmotion.util.RobotStats;
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

	private ArenaMap m_arenaMap;

	SmartEncoder left;
	SmartEncoder right;
	public AHRS gyro;

	public static boolean kms = false;
	public static String suicideLetter = "";
	public static Throwable suicideCause = null;

	@Override
	public void disabledInit() {
		System.out.println("Am I Disabled?");
		controller.stop();
		controller.free();
		resetEncoders();
		loc.stop();
	}

	@Override
	public void autonomousInit() {
		reset();
		loc.start();
		new PathFactory().conncetLine(0, 1, 0.01).construct(m_arenaMap);
		controller.start();
	}

	@Override
	public void teleopInit() {
		reset();
		loc.start();
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
		logGyro();
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		logGyro();
		rd.arcadeDrive(-OI.getInstance().getJoystick().getRawAxis(1), OI.getInstance().getJoystick().getRawAxis(4),
				true);
	}

	public Robot() {
	}

	@Override
	public void robotInit() {
		rd = DrivePort.DEFAULT;
		rd.setPowerLimit(FULL_POWER);
		left = rd.getEncoder(false);
		right = rd.getEncoder(true);
		gyro = new AHRS(SPI.Port.kMXP);
		reset();
		loc = Localizer.of(left, right, RobotStats.Lobiiiiiin.Chassis.WHEEL_RADIUS.value, gyro, Localizer.AngleDifferenceCalculation.GYRO_BASED);
		
		output = new APPCOutput();
		m_arenaMap = new ArenaMap();
		controller = new APPController(loc, output, m_arenaMap);
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
	
	public void resetEncoders() {
		if (left != null) left.reset();
		if (right != null) right.reset();
	}
	
	private void printTicks() {
		System.out.println("left ticks: " + left.getTicks());
		System.out.println("right ticks: " + right.getTicks());
		System.out.println("------------------------------------");
	}
	
	private void printDistances() {
		System.out.println("left distance: " + left.getDistance());
		System.out.println("right distance: " + right.getDistance());
		System.out.println("------------------------------------");
	}
	
	private void logGyro() {
		System.out.println("yaw: " + gyro.getYaw());
		System.out.println("pitch: " + gyro.getPitch());
		System.out.println("roll: " + gyro.getRoll());
	}
	
	private void resetGyro() {
		gyro.reset();
		while (gyro.isCalibrating()) {}
	}
	
	private void reset() {
		resetGyro();
		resetEncoders();
	}
}
