package APPC;



import org.usfirst.frc.team4590.robot.RobotStats;

import base.Output;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class APPCOutput implements Output<APPDriveData> {
    private RobotDrive m_robotDrive;
    
    private static double safteyFactor = 0.5;
    private static double fullPower = 0.8;
    
    public APPCOutput(RobotDrive robotDrive){
        m_robotDrive = robotDrive;
        
    }

    
    public void curveDrive(RobotDrive r,double power,double curve){
    	SmartDashboard.putNumber("Curve", curve);
    	if(curve == 0){
    		r.tankDrive(power, power, false);
    		SmartDashboard.putNumber("powerR", power);
    		SmartDashboard.putNumber("powerL", power);
    		return;
    	}
    	//test
    	double d = RobotStats.HORIZONTAL_WHEEL_DIST;
    	double R = 1 / Math.abs(curve);
        double ratio;
        ratio = (R - d / 2) / (R + d / 2);
        SmartDashboard.putNumber("Ratio", ratio);
    	if(curve > 0){
	    r.tankDrive(power, power*ratio, false);
	    SmartDashboard.putNumber("powerL", power);
	    SmartDashboard.putNumber("powerR", power*ratio);
    	}
    	else {
    	    r.tankDrive(power*ratio, power, false);
    	    SmartDashboard.putNumber("powerL", power*ratio);
    	    SmartDashboard.putNumber("powerR", power);
    	}
    }

    /**
     * 
     * @param output the output to use on the engines. output[0]- power, output[1]- curve
     */
    @Override
    public void use(APPDriveData output) {
    	System.out.println("power: " + output.power + ", curve: " + output.curve);
  	  //e^(-r/w)
  	  //throw new Exception("for those of you who dont know yet that this now does shit");
  	  //double realCurve = Math.pow(Math.E,(-1/output[1])/0.5);
  	  //m_robotDrive.drive(safteyFactor * output[0],realCurve);
    	curveDrive(m_robotDrive,output.power*fullPower*safteyFactor,output.curve);
    	//m_robotDrive.tankDrive(0, 0);
    	  //m_robotDrive.tankDrive(0, 0);
    	//System.out.printf("APPCOutput active: power = %f, curve = %f\n", output[0]*fullPower*safteyFactor, output[1]);
    }
    
    
    
    @Override
	public APPDriveData noPower() {
		// TODO Auto-generated method stub
		return new APPDriveData(.0, .0);
	}


	public void tankDrive(double left, double right) {
    	m_robotDrive.tankDrive(left, right);
    }
    
    public void arcadeDrive(double magnitude, double curve) {
    	m_robotDrive.arcadeDrive(magnitude, curve);
    }
}
