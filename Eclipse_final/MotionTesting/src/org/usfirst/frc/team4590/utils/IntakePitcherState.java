package org.usfirst.frc.team4590.utils;

import org.usfirst.frc.team4590.robot.RobotMap;

public enum IntakePitcherState {
	COLLECT(RobotMap.INTAKE_PITCHER_COLLECT_VALUE),
	SWITCH(RobotMap.INTAKE_PITCHER_SWITCH_VALUE),
	SCALE(RobotMap.INTAKE_PITCHER_SCALE_VALUE),
	DEFAULT(RobotMap.INTAKE_PITCHER_DEFAULT_VALUE);
	
	private int m_degree;
	private IntakePitcherState(int degrees) {
		m_degree = degrees;
	}
	
	public int getDegree() {
		return m_degree;
	}
	
	public double getRad() {
		return Math.toRadians(m_degree);
	}
}
