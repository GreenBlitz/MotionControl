package org.green4590.robot.logger;

import sun.security.x509.IssuerAlternativeNameExtension;

public class Main {
	public static void main(String[] args){
		CSVRobotLogger logger = new CSVRobotLogger();
		
		logger.initNT();
		
		System.out.print("Waiting for enabled.... ");
		while(!logger.isEnabled());
		System.out.println("Enabled");
		
		while(logger.isEnabled()) logger.logAll();
		
		System.out.println("rip");
	}
}
