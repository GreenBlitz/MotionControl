package gbmotion.PIDController;

import com.kauailabs.navx.frc.AHRS;

import gbmotion.base.controller.Input;

public class GyroSampler implements Input<Double> {
	private AHRS m_gyro;
	
	public GyroSampler(AHRS gyro) {
		m_gyro = gyro;
	}
	
	@Override
	public Double recieve() {
		return new Double(m_gyro.getYaw());
	}
}
