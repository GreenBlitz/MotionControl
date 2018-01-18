package APPC;

import base.Output;
import edu.wpi.first.wpilibj.RobotDrive;

public class APPCout implements Output<Double[]> {
    private RobotDrive m_robotDrive;
    public APPCout(RobotDrive robotDrive){
        m_robotDrive = robotDrive;
    }
      @Override
    public void use(Double[] output) {
        m_robotDrive.arcadeDrive(output[1],-(output[0]-0.5)*2);
    }
}