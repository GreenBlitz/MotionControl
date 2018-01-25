package base;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class EnvironmentPort {	
	public static final EnvironmentPort DEFAULT = new EnvironmentPort();
	
	protected DriverStation ds = DriverStation.getInstance();
	
	protected EnvironmentPort() {}
	
	public boolean isEnabled() {
		return ds.isEnabled();
	}
	
	public boolean isDisabled() {
		return ds.isDisabled();
	}

	public void release() {
		ds.release();
	}

	public double getStickAxis(int stick, int axis) {
		return ds.getStickAxis(stick, axis);
	}

	public int getStickPOV(int stick, int pov) {
		return ds.getStickPOV(stick, pov);
	}

	public int getStickButtons(int stick) {
		return ds.getStickButtons(stick);
	}

	public boolean getStickButton(int stick, byte button) {
		return ds.getStickButton(stick, button);
	}

	public int getStickAxisCount(int stick) {
		return ds.getStickAxisCount(stick);
	}

	public int getStickPOVCount(int stick) {
		return ds.getStickPOVCount(stick);
	}

	public int getStickButtonCount(int stick) {
		return ds.getStickButtonCount(stick);
	}

	public boolean getJoystickIsXbox(int stick) {
		return ds.getJoystickIsXbox(stick);
	}

	public int getJoystickType(int stick) {
		return ds.getJoystickType(stick);
	}

	public String getJoystickName(int stick) {
		return ds.getJoystickName(stick);
	}

	public int getJoystickAxisType(int stick, int axis) {
		return ds.getJoystickAxisType(stick, axis);
	}

	public boolean isAutonomous() {
		return ds.isAutonomous();
	}

	public boolean isOperatorControl() {
		return ds.isOperatorControl();
	}

	public boolean isTest() {
		return ds.isTest();
	}

	public boolean isDSAttached() {
		return ds.isDSAttached();
	}

	public boolean isNewControlData() {
		return ds.isNewControlData();
	}

	public boolean isFMSAttached() {
		return ds.isFMSAttached();
	}

	public boolean isSysActive() {
		return ds.isSysActive();
	}

	public boolean isBrownedOut() {
		return ds.isBrownedOut();
	}

	public Alliance getAlliance() {
		return ds.getAlliance();
	}

	public int getLocation() {
		return ds.getLocation();
	}

	public void waitForData() {
		ds.waitForData();
	}

	public boolean waitForData(double timeout) {
		return ds.waitForData(timeout);
	}

	public double getMatchTime() {
		return ds.getMatchTime();
	}

	public double getBatteryVoltage() {
		return ds.getBatteryVoltage();
	}

	public void InDisabled(boolean entering) {
		ds.InDisabled(entering);
	}

	public void InAutonomous(boolean entering) {
		ds.InAutonomous(entering);
	}

	public void InOperatorControl(boolean entering) {
		ds.InOperatorControl(entering);
	}

	public void InTest(boolean entering) {
		ds.InTest(entering);
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void putNumber(String name, double value) {
		SmartDashboard.putNumber(name, value);
	}
}
