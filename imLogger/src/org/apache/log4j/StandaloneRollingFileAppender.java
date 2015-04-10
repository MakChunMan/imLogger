package org.apache.log4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Jason MAK
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StandaloneRollingFileAppender extends FileAppender {

	private boolean doesFirstLogging = false;
	private static final String DATE_PATTERN = "yyyy-MM-dd-HH-mm-ss";
	private static final String BACKUP_SUFFIX = ".backup";

	protected void subAppend(LoggingEvent event) {
		if (!doesFirstLogging) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
			
			this.closeFile();

			String backupFileName = fileName + "." + sdf.format(new Date()) + BACKUP_SUFFIX; 
			File target  = new File(backupFileName);
			if (target.exists()) {
			  target.delete();
			}

			File file = new File(fileName);
			file.renameTo(target);
			LogLog.debug(fileName + " -> "+ backupFileName);

			try {
			  this.setFile(fileName, false, this.bufferedIO, this.bufferSize);
			}
			catch(IOException e) {
			  errorHandler.error("setFile("+fileName+", false) call failed.");
			}
			doesFirstLogging = true;
		}
		super.subAppend(event);
	}

}
