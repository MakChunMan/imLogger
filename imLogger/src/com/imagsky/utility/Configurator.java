package com.imagsky.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.StandaloneRollingFileAppender;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelMatchFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
   <p><code>Configurator</code> is used to runtime configure the loggers.
   
   <P>You have to use the name of the logger, which you want to configure, 
   to create an instant of the <code>Configurator</code>. After you called 
   the methods that changed the configuration of the logger, you have to call 
   <code>Configurator.configure() </code>
   to make the change effective.

  @author Jason Mak */

public class Configurator {

	protected final static PatternLayout PATTERN_LAYOUT = new PatternLayout("%d %-5p [%t] %C{2} (%F:%L) - %m%n");
	protected final static DenyAllFilter DENY_ALL_FILTER = new DenyAllFilter();
	
	protected Level level;  
	protected HashMap appenders;
	protected boolean isReset = false;
	protected String loggerName = null;

	public final static int SYSTEM_ERR = 0;
	public final static int SYSTEM_OUT = 1;

	public Configurator (String loggerName) { 
		this.loggerName = loggerName;
		this.appenders = new HashMap();
		this.level = null;
	}
	/** Make the change of the logger effective */
	public void configure() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.loggerName);
		//Don't write log to RootLogger
		logger.setAdditivity(false);
		if (this.isReset) {
			reset();			
		}
		logger.setLevel(this.level);
		Collection c = this.appenders.values();
		Iterator i = c.iterator();
 		
		while(i.hasNext()) {
			Appender appender = (Appender)i.next();
			if (appender.getLayout() == null) 
				appender.setLayout(PATTERN_LAYOUT);

			logger.addAppender(appender);
		}
	}

	/** Load default xml configuration file for the logger 
	 * -- Remove all system dependent check to determine the xml. Refer to web.xml instead.
	 * -- When the mainServlet init, there should be a step (PropertiesUtil) to add a property "sys.propFolder" to the system properties.
	 * and the value is loaded from web.xml
	 * */
	
  public void loadConfigurationFile () throws IOException {
    
    File f = new File(System.getProperty("sys.propFolder") + this.loggerName + ".xml");
	System.out.println(System.getProperty("sys.propFolder") + this.loggerName + ".xml exist  = "+ f.exists());
	loadConfigurationFile(new FileInputStream(f));
    /***
    if (osName != null && osName.indexOf("Windows") >= 0) {
    	//for windows user, the configuration file should be placed in current dir.
    	File f = new File(System.getProperty("sys.propFolder") + this.loggerName + ".xml");
    	System.out.println(System.getProperty("sys.propFolder") + this.loggerName + ".xml exist  = "+ f.exists());
    	//loadConfigurationFile(this.getClass().getResourceAsStream("Y:\\working\\prop\\" + this.loggerName + ".xml"));
    	loadConfigurationFile(new FileInputStream(f));
    } else {
    	String defaultLocation = "/repos/prop/logger/";
    	if (loggerLocation != null) {
    		defaultLocation = defaultLocation + loggerLocation + "/";
    	}
    	File f = new File(defaultLocation + this.loggerName + ".xml");
    	loadConfigurationFile(new FileInputStream(f));
    }***/
  }
  /** Load <code>inputStream</code> which points to xml configuration file */
  public void loadConfigurationFile(InputStream inputStream) {
    loadConfigurationFile(new InputSource(inputStream));
  }
  /** Specify the filename of the xml configuration file */
	public void loadConfigurationFile(String filename) throws IOException {
		FileInputStream fis = null;
		fis = new FileInputStream(filename);
		loadConfigurationFile(fis);
	}
	protected void loadConfigurationFile(InputSource inputSource) {
		try {
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
			//dfactory.setValidating(true);
			DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
			//docBuilder.setErrorHandler(new SAXErrorHandler());
			System.out.println(inputSource.toString());
			Document doc = docBuilder.parse(inputSource);
	    parse(doc.getDocumentElement());
	  } catch (Exception e) {
	  	e.printStackTrace();
	  }
  }
	protected void parse (Element element) {
		String   tagName = null;
	    Element  currentElement = null;
	    Node     currentNode = null;
	    NodeList children = element.getChildNodes();
	    final int length = children.getLength();
			
			if ("true".equals(element.getAttribute("reset"))) 
				setClear();
	
			for (int loop = 0; loop < length; loop++) {
				currentNode = children.item(loop);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					currentElement = (Element) currentNode;
					tagName = currentElement.getTagName();
					if (tagName.equals("level")) {
						setLevel(currentElement);
					} else if (tagName.equals("dest")) {
						setDestination(currentElement);
					}
				}
			}
	}
	protected void reset() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.loggerName);
		logger.removeAllAppenders();				
	}
	/** Clear all the previous configuration of the logger. */
	public void setClear () {
		this.isReset = true;
	}
/**
 * 
 * This is used to filter the logging event. For this version, only logging level can be used as filter. <p>
 * 
 * Creation date: (07-02-2002 02:33 PM)
 * @param name It is the name of the destination to which the filter add. 
 * @param filterType It is the type of the filter. In this version, only "LevelMatchFilter" is accept
 * @param filterValue It is the logging level which is matched by this filter 
 */
public void setDestFilter(String name, String filterType, String filterValue) {
	
	Appender appender = (Appender)this.appenders.get(name);
	if (appender != null) {
		setDestFilter(appender, filterType, filterValue);		
	}
}
/**
 * Insert the method's description here.
 * Creation date: (07-02-2002 03:09 PM)
 * @param appender org.apache.log4j.Appender
 * @param filterType java.lang.String
 * @param filterValue java.lang.String
 */
protected void setDestFilter(Appender appender, String filterType, String filterValue) {
	
	if ("LevelMatchFilter".equals(filterType)) {
		LevelMatchFilter f = new LevelMatchFilter();
		f.setLevelToMatch(filterValue);
		appender.addFilter(f);	
		appender.addFilter(DENY_ALL_FILTER);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (07-02-2002 03:04 PM)
 */
protected void setDestFilter(Appender appender, Element element) {
	
	String filterType = element.getAttribute("type");
	String filterValue = element.getAttribute("value");

	setDestFilter(appender, filterType, filterValue);
	
}
	/** Add new logging destination to system out or error.
	
	@param name the name of the destination. 
	@param val recognized values are <code>Configurator.SYSTEM_OUT</code> and <code>Configurator.SYSTEM_ERR</code>. Any other value will be ignored.
  */
	public void setDestination (String name, int val) {
		ConsoleAppender appender = new ConsoleAppender();
		switch (val) {
			case SYSTEM_ERR: appender.setTarget(ConsoleAppender.SYSTEM_ERR);
		}
		setDestination(appender);
	}
	/** Add new logging destination to a <code>Writer</code>.
	
	@param name the name of the destination. 
	@param out a reference to a <code>Writer</code>.
  */
	public void setDestination (String name, Writer out) {
		WriterAppender appender = new WriterAppender();
		appender.setWriter(out);
	}
	/** Add new logging destination to a file without rollover file
	
	@param name the name of the destination. 
	@param filename the absolute path and filename.
  */
	public void setDestination (String name, String filename) {
		setDestination (name, filename, null);
	}
	/** Add new logging destination to a file with rollover file
	@param name the name of the destination. 
	@param filename the absolute path and filename.
	@param rolling the rollover frequency of the log file. Only "DAILY", "WEEKLY" and "MONTHLY" are allowed.
  */
	public void setDestination (String name, String filename, String rolling) {
		FileAppender appender = null;
		if (rolling != null) {
			String datePattern = getRollingPattern(rolling);
			appender = new DailyRollingFileAppender();
			((DailyRollingFileAppender)appender).setDatePattern(datePattern);
		} else {
			appender = new FileAppender();
		}
		appender.setName(name);
		appender.setFile(filename);
		appender.setAppend(true);
		setDestination(appender);
	}
	protected void setDestination (Appender appender) {
		this.appenders.put(appender.getName(), appender);
	}
	protected void setDestination (Element element) {
		String destType = element.getAttribute("type");
		String destName = element.getAttribute("name");

		Appender appender = null;
		if (destType.equals("FILE")) {
			String rolling = element.getAttribute("rolling");
			String standalone = element.getAttribute("standalone");
			if ("true".equals(standalone)) {
				appender = new StandaloneRollingFileAppender();
			} else if (rolling != "") {
				appender = new DailyRollingFileAppender();
				String datePattern = getRollingPattern(rolling);
				((DailyRollingFileAppender)appender).setDatePattern(datePattern);
			 } else {
			 	appender = new FileAppender();
			 }
			 ((FileAppender)appender).setAppend(true);
		} else if (destType.equals("SYSTEM_ERR")) {
			appender = new ConsoleAppender();
			((ConsoleAppender)appender).setTarget(ConsoleAppender.SYSTEM_ERR);
		} else {
			appender = new ConsoleAppender();
			((ConsoleAppender)appender).setTarget(ConsoleAppender.SYSTEM_OUT);
		}
		appender.setName(destName);
		PropertySetter propSetter = new PropertySetter(appender);
		NodeList params = element.getChildNodes();
		final int length = params.getLength();
		for (int loop = 0; loop < length; loop++) {
			Node currentNode = (Node)params.item(loop);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				Element currentElement = (Element) currentNode;
				String tagName = currentElement.getTagName();
				if(tagName.equals("parm")) {
					setParameter(currentElement, propSetter);
				} else if (tagName.equals("filter")) {
					setDestFilter(appender, currentElement);
				} else if (tagName.equals("layout")) {
					setDestLayout(appender, currentElement);
				}
			}
		}
    propSetter.activate();
    setDestination(appender); 
	}
	/** set logging level */
	public void setLevel (int val) {
		this.level = Level.toLevel(val);
	}
	protected void setLevel (Element element) {
		this.level = Level.toLevel(element.getAttribute("value"), Level.OFF);
	}
	protected void setParameter(Element element, PropertySetter propSetter) {
    String name = element.getAttribute("name");
    String value = element.getAttribute("value");
    propSetter.setProperty(name, value);
  }


public void setDestLayout(String name, String pattern) {
	
	Appender appender = (Appender)this.appenders.get(name);
	if (appender != null) {
		setDestLayout(appender, pattern);		
	}
}

protected void setDestLayout(Appender appender, String pattern) {
	PatternLayout pl = new PatternLayout(pattern);

	appender.setLayout(pl);
	
}

protected void setDestLayout(Appender appender, Element element) {
	String pattern = element.getAttribute("pattern");
	
	setDestLayout(appender, pattern);	
}


protected String getRollingPattern (String rollingString) {
	String datePattern = null;
	if ("MINUTE".equals(rollingString)) {
		datePattern = "yyyy-MM-dd-HH-mm";
	} else if ("DAILY".equals(rollingString)) {
		datePattern = "yyyy-MM-dd";
	} else if ("WEEKLY".equals(rollingString)) {
		datePattern = "yyyy-ww";
	} else if ("MONTHLY".equals(rollingString)) {
		datePattern = "yyyy-MM";					
	}
	return "'.'" + datePattern + "'.backup'";
}

}
