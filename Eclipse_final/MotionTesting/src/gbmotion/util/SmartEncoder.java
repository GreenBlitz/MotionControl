package gbmotion.util;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class SmartEncoder {
	private final TalonSRX m_talon;
	private final double m_ticksPerMeterPower;
	private final double m_ticksPerMeterVelocity;

	public SmartEncoder(TalonSRX talon, double ticksPerMeterPower, double ticksPerMeterVelocity) {
		if (ticksPerMeterPower == +0.0 || !Double.isFinite(ticksPerMeterPower) || ticksPerMeterPower == -0.0)
			throw new IllegalArgumentException("invalid ticks per meter value '" + ticksPerMeterPower + "'");

		if (ticksPerMeterVelocity == +0.0 || !Double.isFinite(ticksPerMeterVelocity) || ticksPerMeterVelocity == -0.0)
			throw new IllegalArgumentException("invalid ticks per meter value '" + ticksPerMeterVelocity + "'");

		m_talon = talon;
		m_ticksPerMeterVelocity = ticksPerMeterVelocity;
		m_ticksPerMeterPower = ticksPerMeterPower;
	}

	public double getTicksPerMeter(RobotStats.Gear gear) {
		return gear == RobotStats.Gear.POWER ? m_ticksPerMeterPower : m_ticksPerMeterVelocity;
	}
	
	public double getTicksPerMeter() {
		return getTicksPerMeter(Shifter.getInstance().getState());
	}

	public int getTicks() {
		return m_talon.getSensorCollection().getQuadraturePosition();
	}

	public double getDistance() {
		return m_talon.getSensorCollection().getQuadraturePosition() / getTicksPerMeter();
	}

	public double getSpeed() {
		return m_talon.getSensorCollection().getQuadratureVelocity() / getTicksPerMeter();
	}

	public ErrorCode reset() {
		ErrorCode ec = m_talon.getSensorCollection().setQuadraturePosition(0, 100);
		if (ec != ErrorCode.OK) {
			System.err.println("error occured while reseting encoder '" + m_talon.getHandle() + "': " + ec);
		}
		return ec;
	}
}
