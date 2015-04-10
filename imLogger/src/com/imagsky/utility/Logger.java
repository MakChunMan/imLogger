package com.imagsky.utility;

import org.apache.log4j.Level;
import java.util.Date;

/**
   <p><code>Logger</code> is the core part of the component. 
   It contains the API to write log and set the logging level of the logger.
   
   After you created the configuration file, the logger will be created automatically when you use it for the first time. However, if no configuration file is found, <code>java.lang.RuntimeException.NullPointerException</code> will be thrown when the developer tries to initiate the logger. 
   
	 There are seven logging levels defined for each logger. A logging request is said to be enabled if its level is higher than or equal to the effective level of its logger. The order of the level is:
	 <p>
	 ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL < OFF
	 </p>
	 

  @author Jason Mak */


public class Logger {
    private org.apache.log4j.Logger logger;
    
    private static final String FQCN = (com.imagsky.utility.Logger.class).getName() + ".";
    
    /** The highest logging level, all logger printing methods are disabled.*/
    public static final int OFF = Level.OFF_INT;
    
    /** No logging message is enabled except severe error with <code>FATAL</code> level.*/
    public static final int FATAL = Level.FATAL_INT;
    
    /** All error with <code>FATAL</code> and <code>ERROR</code> level are enabled.*/
    public static final int ERROR = Level.ERROR_INT;
    
    /** Log <code>WARN</code> or above level message.*/
    public static final int WARN = Level.WARN_INT;
    
    /** Log program information together with warning and error message.*/
    public static final int INFO = Level.INFO_INT;
    
    /** <code>DEBUG</code> level should be used in development platform.
        All message with other levels also be logged.*/
    public static final int DEBUG = Level.DEBUG_INT;
    
    /** <code>TRACE</code> level designates finer-grained informational events than the DEBUG 
    For example, you can log the content of the objects for debugging.*/
    public static final int TRACE = Level.TRACE_INT;    
    
    /** The lowest logging level, all logger printing methods are enabled.*/
    public static final int ALL = Level.ALL_INT;

    private Logger(String name, String configurationPath) {
        org.apache.log4j.Logger tmpLogger = org.apache.log4j.LogManager.exists(name);
        if(tmpLogger == null) {
            try {
            	Configurator configurator = new Configurator(name);
            	configurator.loadConfigurationFile(configurationPath);
            	configurator.configure();
            	tmpLogger = org.apache.log4j.Logger.getLogger(name);
            } catch (java.io.IOException e) {
            	e.printStackTrace();
            	throw new java.lang.NullPointerException("Cannot create logger, please check the configuration file!");
            }
        }
        this.logger = tmpLogger;
    }
    
    private Logger(String name) {
        org.apache.log4j.Logger tmpLogger = org.apache.log4j.LogManager.exists(name);
        if(tmpLogger == null) {
            try {
            	Configurator configurator = new Configurator(name);
            	configurator.loadConfigurationFile();
            	configurator.configure();
            	tmpLogger = org.apache.log4j.Logger.getLogger(name);
            } catch (java.io.IOException e) {
            	e.printStackTrace();
            	throw new java.lang.NullPointerException("Cannot create logger, please check the configuration file!");
            }
        }
        this.logger = tmpLogger;
    }
    /** Initiate and retrieve logger instance */
		public static Logger getLogger(String name) {
			Logger tmpLogger = new Logger(name);
			return tmpLogger;
    }
    /** set logging level */
    public void setLevel(int i) {
        this.logger.setLevel(Level.toLevel(i));
    }
    /** Return the name of the logger */
    public String getName() {
        return this.logger.getName();
    }
    /** 
    Log a message object with the {@link #DEBUG DEBUG} level.

    <p>If this logger is <code>DEBUG</code> enabled, then it converts the message object
    (passed as parameter) to a string and writes it to the registered destinations.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #debug(Object,
    Throwable)} form instead.
    
    @param obj the message object to log. */
    public void debug(Object obj) {
        this.debug(obj, null);
    }
	  /**  
	   Log a message object with the <code>DEBUG</code> level including
	   the stack trace of the {@link Throwable} <code>throwable</code> passed as
	   parameter.
	   
	   <p>See {@link #debug(Object)} form for more detailed information.
	   
	   @param obj the message object to log.
	   @param throwable the exception to log, including its stack trace.  */  
    public void debug(Object obj, Throwable throwable) {
        this.logger.log(FQCN, Level.DEBUG, obj, throwable);
    }
  	
    public void trace(Object obj) {
    	this.trace(obj, null);
    }
    public void trace(Object obj, Throwable th) {
    	this.logger.log(FQCN, Level.TRACE, obj, th);
    }
    
  	/** 
    Log a message object with the {@link #ERROR ERROR} Level.

    <p>If this logger is <code>ERROR</code> enabled, then it converts the message object
    passed as parameter to a string and writes it to the registered destinations.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #error(Object,
    Throwable)} form instead.
    
    @param obj the message object to log */
    public void error(Object obj) {
        this.error(obj, null);
    }
  	/** 
   Log a message object with the <code>ERROR</code> level including
   the stack trace of the {@link Throwable} <code>throwable</code> passed as
   parameter.
   
   <p>See {@link #error(Object)} form for more detailed information.
   
   @param obj the message object to log.
   @param throwable the exception to log, including its stack trace.  */  
    public void error(Object obj, Throwable throwable) {
        this.logger.log(FQCN, Level.ERROR, obj, throwable);
    }
  /** 
    Log a message object with the {@link #FATAL FATAL} Level.

    <p>If the logger is <code>FATAL</code>
    enabled, then it converts the message object passed as parameter
    to a string and writes it to the registered destinations. 

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #fatal(Object, Throwable)} form
    instead. 
    
    @param obj the message object to log */
    public void fatal(Object obj) {
        this.fatal(obj, null);
    }
  /** 
   Log a message object with the <code>FATAL</code> level including
   the stack trace of the {@link Throwable} <code>throwable</code> passed as
   parameter.
   
   <p>See {@link #fatal(Object)} for more detailed information.
   
   @param obj the message object to log.
   @param throwable the exception to log, including its stack trace.  */
    public void fatal(Object obj, Throwable throwable) {
        this.logger.log(FQCN, Level.FATAL, obj, throwable);
    }
  /** 
    Log a message object with the {@link Level#INFO INFO} Level.

    <p>If the logger is <code>INFO</code>
    enabled, then it converts the message object passed as parameter
    to a string and writes it to the registered destinations.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #info(Object, Throwable)} form
    instead. 
    
    @param obj the message object to log */
    public void info(Object obj) {
        this.info(obj, null);
    }
  /** 
   Log a message object with the <code>INFO</code> level including
   the stack trace of the {@link Throwable} <code>throwable</code> passed as
   parameter.
   
   <p>See {@link #info(Object)} for more detailed information.
   
   @param obj the message object to log.
   @param throwable the exception to log, including its stack trace.  */
    public void info(Object obj, Throwable throwable) {
        this.logger.log(FQCN, Level.INFO, obj, throwable);
    }
    /** 
    Log a message object with the {@link Level#WARN WARN} Level.

    <p>If the logger is <code>WARN</code>
    enabled, then it converts the message object passed as parameter
    to a string and writes it to the registered destinations.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the Throwable but no stack trace. To
    print a stack trace use the {@link #warn(Object, Throwable)} form
    instead.  <p>
    
    @param obj the message object to log.  */
    public void warn(Object obj) {
        this.warn(obj, null);
    }
  /** 
   Log a message with the <code>WARN</code> level including the
   stack trace of the {@link Throwable} <code>throwable</code> passed as
   parameter.
   
   <p>See {@link #warn(Object)} for more detailed information.
   
   @param obj the message object to log.
   @param throwable the exception to log, including its stack trace.  */
    public void warn(Object obj, Throwable throwable) {
        this.logger.log(FQCN, Level.WARN, obj, throwable);
    }
    /** 
    Log a message a CSV file which will be loaded to Tivoli for application monitoring.<br>
    This printing method will write message to the csv whatever the logging level is.<br>
    Therefore, you use this method when application monitoring is required.
    */
    public void critical(Object obj) {
    	TivoliLog.log(FQCN, obj);
    }	
    /** 
    Log a message a CSV file which will be loaded to Tivoli for application monitoring.<br>
    This printing method will write message to the csv whatever the logging level is.<br>
    Therefore, you use this method when application monitoring is required.
    */
	public void critical(Object obj, Throwable throwable) {
    	TivoliLog.log(FQCN, obj, throwable);
    }
    /** 
    Log a message a CSV file which will be loaded to Tivoli for application monitoring.<br>
    This printing method will write message to the csv whatever the logging level is.<br>
    Therefore, you use this method when application monitoring is required.
    */
    public void critical(Date testDate, String appName, String result, long elapsedTime, String errMessage) {
    	TivoliLog.log(FQCN, testDate, appName, result, elapsedTime, errMessage);
    }
    

    public boolean isOff() {
    	return this.logger.isEnabledFor(Level.OFF);
    }
    public boolean isFatal() {
    	return this.logger.isEnabledFor(Level.FATAL);
    }    
    public boolean isError() {
    	return this.logger.isEnabledFor(Level.ERROR);
    }
    public boolean isWarn() {
    	return this.logger.isEnabledFor(Level.WARN);
    }
    public boolean isInfo() {
    	return this.logger.isInfoEnabled();
    }    
    public boolean isDebug() {
    	return this.logger.isDebugEnabled();
    }
    public boolean isTrace() {
    	return this.logger.isEnabledFor(Level.TRACE);
    }


}
