package org.green4590.robot.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Logger {
	
	public static void makeFile(File file, boolean overwrite){
		if (file.exists()) {
			System.out.print("File exists... ");
			if(overwrite){
				System.out.print("Deleting file... ");
				if(file.delete()) {
					makeFile(file, overwrite);
				} System.err.println("Failed");
			}
		} else {
			try {
				System.out.print("Creating file... ");
				if(file.createNewFile()) System.out.println("Success");
				else System.err.println("Failed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** The file data is logged to */
	private File logFile;
	
	/** Throws strings at the file, DO NOT forget to flush to clean the shit */
	private BufferedWriter writer;
	
	public Logger(String filepath){
		logFile = new File(filepath);
		makeFile(logFile, true);
		try {
			writer = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logCSV(String name, String time, String... values){
		StringBuilder stringShitMemer = new StringBuilder();
		
		stringShitMemer.append(name);
		
		stringShitMemer.append(',');
		stringShitMemer.append(time);
		
		for(String value : values) {
			stringShitMemer.append(value);
			stringShitMemer.append(',');
		}
		stringShitMemer.deleteCharAt(stringShitMemer.length()-1);
		String logged = stringShitMemer.toString();
		
		try {
			System.out.println("logging: " + logged);
			writer.write(logged);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void flushData(){
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
