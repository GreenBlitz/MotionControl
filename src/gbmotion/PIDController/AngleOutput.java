package gbmotion.PIDController;

import gbmotion.base.DrivePort;
import gbmotion.base.controller.Output;

public class AngleOutput implements Output<Double>{

	
	private DrivePort m_dPort;
	
	public AngleOutput(DrivePort dPort) {
		m_dPort = dPort;	
	}

	@Override
	public void use(Double output) {
		double driveVal = Math.max(-0.8, Math.min(0.8, output));
		m_dPort.tankDrive(driveVal, -driveVal, false);
	}
	
	@Override
	public Double noPower() {
		return 0.0;
	}

}
