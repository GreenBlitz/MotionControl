
package org.usfirst.frc.team4590.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Scheduler;
import gbmotion.appc.APPCOutput;
import gbmotion.appc.APPController;
import gbmotion.appc.Localizer;
import gbmotion.base.DrivePort;
import gbmotion.base.point.orientation.IOrientation2D;
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
		if(controller!=null){
			controller.stop();
			controller.free();
		}
		resetEncoders();
		loc.stop();
	}

	@Override
	public void autonomousInit() {
		reset();
		loc.start();
		new PathFactory().connectLine(0, 2, 0.01).connectLine(-1.5, 2, 0.01).connectLine(-1.5, 4, 0.01).construct(m_arenaMap);
		controller = new APPController(loc, output, m_arenaMap);
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
		reset();
	}

	@Override
	public void autonomousPeriodic() {
	
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		System.out.println(loc.recieve());
		
		edu.wpi.first.wpilibj.networktables.NetworkTable motion = edu.wpi.first.wpilibj.networktables.NetworkTable.getTable("motion");
		motion.putNumber("locX", loc.recieve().getX());
		motion.putNumber("locY", loc.recieve().getY());
		motion.putNumber("locAngle", ((IOrientation2D)loc.recieve()).getDirection());
		motion.putNumber("gyroAngle", Math.toRadians(gyro.getYaw()));
		//System.out.println(gyro.getYaw());
		//motion.putNumber("pathLength", 0);
		motion.putNumber("encLeft", loc.getLeftDistance());
		motion.putNumber("encRight", loc.getRightDistance());
		motion.putBoolean("isUpdated", true);
		rd.arcadeDrive(-OI.getInstance().getJoystick().getRawAxis(1), OI.getInstance().getJoystick().getRawAxis(4),
				true);
	}

	public Robot() {
	}

	@Override
	public void robotInit() {
		rd = DrivePort.DEFAULT;
		rd.setPowerLimit(FULL_POWER);
		left = rd.getEncoder(true);
		right = rd.getEncoder(false);
		gyro = new AHRS(SPI.Port.kMXP);
		loc = Localizer.of(left, right, RobotStats.Cerberous.Chassis.HORIZONTAL_DISTANCE.value, gyro, Localizer.AngleDifferenceCalculation.ENCODER_BASED);
		output = new APPCOutput();
		m_arenaMap = new ArenaMap();
		initPrintables();
		OI.init(loc);
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
		return left.getSpeed();
	}

	public double getSpeedR() {
		return right.getSpeed();
	}
	
	public void resetEncoders() {
		left.reset();
		right.reset();
	}
	
	private void resetGyro() {
		do {
			gyro.reset();
		} while (gyro.getYaw() != 0);
		while (gyro.isCalibrating()) {}
	}
	
	private void reset() {
		resetGyro();
		resetEncoders();
		loc.reset();
	}
}
