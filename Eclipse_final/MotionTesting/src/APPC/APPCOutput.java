package APPC;

import org.usfirst.frc.team4590.robot.RobotStats;

import base.Output;
import edu.wpi.first.wpilibj.RobotDrive;

public class APPCOutput implements Output<Double[]> {
    private RobotDrive m_robotDrive;
    
    private static double safteyFactor = 0.25;
    private static double fullPower = 0.8;
    
    public APPCOutput(RobotDrive robotDrive){
        m_robotDrive = robotDrive;
        
    }

    
    public void curveDrive(RobotDrive r,double power,double curve){
    	if(curve == 0){
    		r.tankDrive(power, power,false);
    		//System.out.println(power+"   "+power);
    		return;
    	}
    	
    	double d = RobotStats.RightLeftWheelDistance;
    	double R = 1 / Math.abs(curve);
        double ratio;
        if (R - d / 2 == 0)
        	ratio = 0;
        else
        	ratio = (R + d / 2) / (R - d / 2);    
    	if(curve > 0)
    		r.tankDrive(power, power*ratio,false);
    		//System.out.println(power+"   "+power*ratio);// left faster
    	else
    		r.tankDrive(power*ratio, power,false);
    		//System.out.println(power*ratio+"   "+power); // right faster
    }

    /**
     * 
     * @param output the output to use on the engines. output[0]- power, output[1]- curve
     */
    @Override
    public void use(Double[] output) {
  	  //e^(-r/w)
  	  //throw new Exception("for those of you who dont know yet that this now does shit");
  	  //double realCurve = Math.pow(Math.E,(-1/output[1])/0.5);
  	  //m_robotDrive.drive(safteyFactor * output[0],realCurve);
    	curveDrive(m_robotDrive,output[0]*fullPower*safteyFactor,output[1]);
    	//m_robotDrive.tankDrive(0, 0);
    	  //m_robotDrive.tankDrive(0, 0);
    	System.out.printf("APPCOutput active: power = %f, curve = %f\n", output[0]*fullPower*safteyFactor, output[1]);
    }
    
    
    
    @Override
	public Double[] noPower() {
		// TODO Auto-generated method stub
		return new Double[] {.0, .0};
	}


	public void tankDrive(double left, double right) {
    	m_robotDrive.tankDrive(left, right);
    }
    
    public void arcadeDrive(double magnitude, double curve) {
    	m_robotDrive.arcadeDrive(magnitude, curve);
    }
}
