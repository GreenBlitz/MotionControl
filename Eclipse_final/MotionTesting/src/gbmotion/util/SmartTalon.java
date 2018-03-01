package gbmotion.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class SmartTalon extends TalonSRX {

	public SmartTalon(int deviceNumber) {
		super(deviceNumber);
	}

	public void set(double power) {
		set(ControlMode.PercentOutput, power);
	}
}