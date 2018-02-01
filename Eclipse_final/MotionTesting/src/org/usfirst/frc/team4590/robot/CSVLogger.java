package org.usfirst.frc.team4590.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class CSVLogger {
	
	private NetworkTable ntHelper = NetworkTable.getTable("Logger Helper");
	private NetworkTable ntValues = NetworkTable.getTable("Logged");
	
	private boolean enabled = false;
	
	public CSVLogger(){}
	
	public void enable(){
		enabled = true;
		ntHelper.putBoolean("Enabled", enabled);
	}
	
	public void disable(){
		enabled = false;
		ntHelper.putBoolean("Enabled", enabled);
	}
	
	public void log(String key, String value){
		updateTime();
		ntValues.putString(key, value);
	}
	
	private void updateTime(){
		ntHelper.putString("Time sample", "" + System.currentTimeMillis());
	}

	public void log(String key, double value) {
		log(key,value + "");
	}
}
