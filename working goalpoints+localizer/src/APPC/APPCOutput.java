package APPC;

import base.Output;
import edu.wpi.first.wpilibj.RobotDrive;

public class APPCOutput implements Output<Double[]> {
    private RobotDrive m_robotDrive;
    
    private static double safteyFactor = 0.1;
    
    public APPCOutput(RobotDrive robotDrive){
        m_robotDrive = robotDrive;
    }
    
    /**
     * 
     * @param output the output to use on the engines. output[0]- power, output[1]- curve
     */
    @Override
    public void use(Double[] output) {
    	  //e^(-r/w)
    	  //throw new Exception("for those of you who dont know yet that this now does shit");
    	  double realCurve = Math.pow(Math.E,(-1/output[1])/0.5);
    	  m_robotDrive.drive(safteyFactor * output[0],realCurve);
    	  System.out.printf("APPCOutput active: power = %f, curve = %f", output[0], output[1]);
    }
    
    public void tankDrive(double left, double right) {
    	m_robotDrive.tankDrive(left, right);
    }
    
    public void arcadeDrive(double magnitude, double curve) {
    	m_robotDrive.arcadeDrive(magnitude, curve);
    }
}