package APPC;

import base.Output;
import edu.wpi.first.wpilibj.RobotDrive;

public class APPCOutput implements Output<Double[]> {
    private RobotDrive m_robotDrive;
    
    private static double safteyFactor = 0.1;
    
    public APPCOutput(RobotDrive robotDrive){
        m_robotDrive = robotDrive;
    }
      @Override
    public void use(Double[] output) {
    	  //e^(-r/w)
    	  double realCurve = Math.pow(Math.E,(-1/output[1])/0.5);
    	  //m_robotDrive.drive(safteyFactor*output[0],realCurve);
    	  //m_robotDrive.drive(0.3,0);
    	  //m_robotDrive.arcadeDrive(0,0);
    	  //System.out.println("WORKING!!!!!!!  "+output[1]);
    	  m_robotDrive.drive(0,0);
    }
}
