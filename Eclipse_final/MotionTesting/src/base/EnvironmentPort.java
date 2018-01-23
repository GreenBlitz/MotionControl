package base;

import edu.wpi.first.wpilibj.DriverStation;

public class EnvironmentPort {	
	public boolean isEnabled() {
		return DriverStation.getInstance().isEnabled();
	}
	
	public boolean isDisabled() {
		return DriverStation.getInstance().isDisabled();
	}
}
