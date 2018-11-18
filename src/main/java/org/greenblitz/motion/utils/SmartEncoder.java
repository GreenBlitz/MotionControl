package org.greenblitz.motion.utils;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class SmartEncoder {
	private final TalonSRX m_talon;
	private final int m_ticksPerMeterPower;
	private final int m_ticksPerMeterVelocity;

	public SmartEncoder(TalonSRX talon, int ticksPerMeterPower, int ticksPerMeterVelocity) {
		if (ticksPerMeterPower == +0.0 || !Double.isFinite(ticksPerMeterPower) || ticksPerMeterPower == -0.0)
			throw new IllegalArgumentException("invalid ticks per meter value '" + ticksPerMeterPower + "'");

		if (ticksPerMeterVelocity == +0.0 || !Double.isFinite(ticksPerMeterVelocity) || ticksPerMeterVelocity == -0.0)
			throw new IllegalArgumentException("invalid ticks per meter value '" + ticksPerMeterVelocity + "'");

		m_talon = talon;
		m_ticksPerMeterVelocity = ticksPerMeterVelocity;
		m_ticksPerMeterPower = ticksPerMeterPower;
	}

	public int getTicksPerMeter() {
		return m_ticksPerMeterPower;
	}


	public int getTicks() {
		return m_talon.getSensorCollection().getQuadraturePosition();
	}

	public double getDistance() {
		return ((double) getTicks()) / getTicksPerMeter();
	}

	public double getSpeed() {
		return ((double) m_talon.getSensorCollection().getQuadratureVelocity()) / getTicksPerMeter();
	}

	public ErrorCode reset() {
		ErrorCode ec = m_talon.getSensorCollection().setQuadraturePosition(0, 100);
		if (ec != ErrorCode.OK) {
			System.err.println("error occured while reseting encoder '" + m_talon.getHandle() + "': " + ec);
		}
		return getTicks() == 0 ? ec : reset();
	}
	
	public SmartEncoder invert() {
		return new SmartEncoder(m_talon, -m_ticksPerMeterPower, -m_ticksPerMeterVelocity);
	}
}
