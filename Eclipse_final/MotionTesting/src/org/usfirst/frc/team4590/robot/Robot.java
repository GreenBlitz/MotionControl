
package org.usfirst.frc.team4590.robot;

import java.util.ArrayList;
import java.util.LinkedList;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;

import APPC.*;
import base.WrappedEncoder;


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
	private static double SPEED_GEARS_RATIO = 4.17;
	private static double POWER_GEARS_RATIO = 11.03;
	
	@Override
	public void startCompetition() {
		// TODO Auto-generated method stub
		super.startCompetition();
	}

	@Override
	public void disabledInit() {
		// TODO Auto-generated method stub
		super.disabledInit();
	}

	@Override
	public void autonomousInit() {
		// TODO Auto-generated method stub
		System.out.println("auto Init");
    	APPController ctrl = new APPController(loc,out,genPath());

    }
    // 0.49 m
	
    public Path genPath(){
        ArrayList<Point2D> pointList= new ArrayList<Point2D>();
        for(double i = 0;i < 5;i+=0.001)
            pointList.add(new Point2D(0,i,0));
        System.out.println(pointList.size());
        return new Path(pointList);
    }

	@Override
	public void teleopInit() {
		// TODO Auto-generated method stub
		super.teleopInit();
	}

	@Override
	public void robotPeriodic() {
		// TODO Auto-generated method stub
		super.robotPeriodic();
	}

	@Override
	public void disabledPeriodic() {
		// TODO Auto-generated method stub
		super.disabledPeriodic();
	}

	@Override
	public void autonomousPeriodic() {
		// TODO Auto-generated method stub
		super.autonomousPeriodic();
	}

	@Override
	public void teleopPeriodic() {
		// TODO Auto-generated method stub
		super.teleopPeriodic();
	}

	private static Robot instance;
	/*double scale = 2.4/650;
		
		//WrappedEncoder[] leftEncoders = {new WrappedEncoder(new Encoder(0),a),new WrappedEncoder()};
		Point2D p = new Point2D(0.0,0.0,0.0);
    	Localizer l = new Localizer(new WrappedEncoder(new Encoder(2,3),scale),new WrappedEncoder(new Encoder(0,1),scale),p,70.0);
    	APPCOutput ooo = new APPCOutput(Chassis.getInstance().<RobotDrive>getActuator("Robot Drive"));
    	/*new RobotDrive(
				new CANTalon(RobotMap.CHASSIS_FRONT_LEFT_MOTOR_PORT),
				new CANTalon(RobotMap.CHASSIS_REAR_LEFT_MOTOR_PORT), 
				new CANTalon(RobotMap.CHASSIS_FRONT_RIGHT_MOTOR_PORT), 
				new CANTalon(RobotMap.CHASSIS_REAR_RIGHT_MOTOR_PORT)));*/
 	
    public Robot() {
    }
    
    @Override
    public void robotInit() {
		
		//double scale = 2.4/650;
		double scale = 0.0036;//(1.0 / 220.5);// * POWER_GEARS_RATIO / SPEED_GEARS_RATIO;
		
		//WrappedEncoder[] leftEncoders = {new WrappedEncoder(new Encoder(0),a),new WrappedEncoder()};
		
		//WrappedEncoder[] leftEncoders = {new WrappedEncoder(new Encoder(0),a),new WrappedEncoder()};
		System.out.println("robo");
    	loc = new Localizer(new WrappedEncoder(new Encoder(2,3),-scale),new WrappedEncoder(new Encoder(0,1),scale),new Point2D(0,0,0),0.68);
    	out = new APPCOutput(new RobotDrive(
				new CANTalon(RobotMap.CHASSIS_FRONT_LEFT_MOTOR_PORT),
				new CANTalon(RobotMap.CHASSIS_REAR_LEFT_MOTOR_PORT), 
				new CANTalon(RobotMap.CHASSIS_FRONT_RIGHT_MOTOR_PORT), 
				new CANTalon(RobotMap.CHASSIS_REAR_RIGHT_MOTOR_PORT)));
    }

	
}
