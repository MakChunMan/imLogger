package com.imagsky.utility;

import org.apache.log4j.Level;
import org.apache.log4j.FileAppender;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.Enumeration;
import java.util.Date;

import java.text.SimpleDateFormat;

class TivoliLog {

	private static org.apache.log4j.Logger logger; 
	
	private static final String FQCN = (com.imagsky.utility.TivoliLog.class).getName() + ".";
	private static final String DATE_FORMAT = "dd/MM/yyyy,HH:mm:ss";
	private static final String LOG_HEADER = "Date, Time, Resource, Result, Elapsed Time, Message";
	
	static {
		try {
			Configurator configurator = new Configurator("tivoli");
			configurator.loadConfigurationFile();
			configurator.configure();
			logger = org.apache.log4j.Logger.getLogger("tivoli");
				
			//check whether the output file is empty,
			//if yes, print the header of the csv first
			
			Enumeration e = logger.getAllAppenders();
			while (e.hasMoreElements()) {
				Object obj = e.nextElement();
				if (obj instanceof FileAppender) {
					FileAppender fa = (FileAppender) obj;
					File f = new File(fa.getFile());
					if (f.length() == 0) {
						PrintWriter out = null;
						try {
							out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
							out.println(LOG_HEADER);			
						} finally {
							if (out != null) out.close();
						}
					}
				}
			}	
			
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw new java.lang.NullPointerException("Cannot create tivoli logger, please check the configuration file!");
		}
	}

	public static void log(String callerFQCN, Object obj) {
		log(callerFQCN, obj, null);
	}

	public static void log(String callerFQCN, Object obj, Throwable throwable) {
		logger.log(callerFQCN, Level.FATAL, obj, throwable);
	}

	public static void log(String callerFQCN, Date testDate, String appName, String result, long elapsedTime, String errMessage) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		String strDate = formatter.format(testDate);
		formatter = null;		
		
		if (!"RESULT_OK".equals(result)) {
			result = "RESULT_FAIL";
		}
		logger.log(callerFQCN, Level.INFO, strDate + ", " + 
																			appName + ", " + 
																			result + ", " + 
																			elapsedTime + ", " + 
																			errMessage, null);
	}
}