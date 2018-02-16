package org.green4590.robot.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;

public class CSVRobotLogger extends Logger {
	
	public static String getFormattedDate(){
		
		/*not coping code from stack overflow */
		
		// Create an instance of SimpleDateFormat used for formatting 
		// the string representation of date (month/day/year)
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss");

		// Get the date today using Calendar object.
		Date today = Calendar.getInstance().getTime();        
		// Using DateFormat format method we can create a string 
		// representation of a date with the defined format.
		return df.format(today);
	}
	
	private static final String FILE_PATH = "C:/Users/Peleg Caduri/Desktop/Robot Output/";
	private static final String FILE_TYPE = ".csv";
	
	private NetworkTable ntHelper;
	private NetworkTable ntValues;
	
	public CSVRobotLogger() {
		super(FILE_PATH + getFormattedDate() + " Robot log" + FILE_TYPE);
		
		
	}
	
	public void initNT(){
		
		System.out.print("initializing NT... ");
		NetworkTable.setIPAddress("10.45.90.2");
		NetworkTable.setClientMode();
		
		NetworkTable.initialize();
		System.out.println("Success");
		
		ntHelper = NetworkTable.getTable("Logger Helper");
		ntValues = NetworkTable.getTable("Logged");
		
	}
	
	public boolean isEnabled() {
		return ntHelper.getBoolean("Enabled", false);
	}
	
	protected String getTimeSample() {
		return ntHelper.getString("Time sample", null);
	}
	
	public void logAll(){
		Set<String> allKeys = ntValues.getKeys();
		for(String key : allKeys){
			String value = ntValues.getString(key, null);
			//String[] separeted = value.split(",");
			logCSV(key, getTimeSample(), value /*separeted*/);
		}
		flushData();
	}
	
	
	
}
