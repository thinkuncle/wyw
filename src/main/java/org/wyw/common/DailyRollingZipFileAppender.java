package org.wyw.common;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * @author wanghaibo
 */
public class DailyRollingZipFileAppender extends FileAppender {

	static final int TOP_OF_TROUBLE = -1;

	static final int TOP_OF_MINUTE = 0;

	static final int TOP_OF_HOUR = 1;

	static final int HALF_DAY = 2;

	static final int TOP_OF_DAY = 3;

	static final int TOP_OF_WEEK = 4;

	static final int TOP_OF_MONTH = 5;

	static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

	static boolean zip(File file, String name) {
		try {
			FileInputStream fin = new FileInputStream(file);
			FileOutputStream fout = new FileOutputStream(name + ".zip");
			ZipOutputStream zout = new ZipOutputStream(fout);
			zout.putNextEntry(new ZipEntry(file.getName()));
			byte buf[] = new byte[1024];
			int num;
			while ((num = fin.read(buf)) != -1) {
				zout.write(buf, 0, num);
				zout.flush();
			}
			zout.close();
			fout.close();
			fin.close();
		} catch (IOException e) {
			LogLog.error("catch IOException while zipping file " + file.getName(), e);
			return false;
		}
		return true;
	}

	private String datePattern;

	private String scheduledFilename;

	private long nextCheck;

	Date now;
	SimpleDateFormat sdf;
	RollingCalendar rc;
	int checkPeriod;
	private int fileReservePeriod;

	public DailyRollingZipFileAppender() {
		datePattern = "'.'yyyy-MM-dd";
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollingCalendar();
		checkPeriod = -1;
		fileReservePeriod = 15;
	}

	public DailyRollingZipFileAppender(Layout layout, String filename, String datePattern, String fileReservePeriod)
			throws IOException {
		super(layout, filename, true);
		this.datePattern = "'.'yyyy-MM-dd";
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollingCalendar();
		checkPeriod = -1;
		this.fileReservePeriod = 15;
		this.datePattern = datePattern;
		this.fileReservePeriod = Integer.parseInt(fileReservePeriod);
		activateOptions();
	}

	public void activateOptions() {
		super.activateOptions();
		if (datePattern != null && fileName != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			int type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			File file = new File(fileName);
			scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));
		} else {
			LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
		}
	}

	int computeCheckPeriod() {
		RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
		Date epoch = new Date(0L);
		if (datePattern != null) {
			for (int i = 0; i <= 5; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
				simpleDateFormat.setTimeZone(gmtTimeZone);
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				if (r0 != null && r1 != null && !r0.equals(r1))
					return i;
			}

		}
		return -1;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public int getFileReservePeriod() {
		return fileReservePeriod;
	}

	void printPeriodicity(int type) {
		switch (type) {
		case 0: // '\0'
			LogLog.debug("Appender [" + name + "] to be rolled every minute.");
			break;

		case 1: // '\001'
			LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");
			break;

		case 2: // '\002'
			LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");
			break;

		case 3: // '\003'
			LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
			break;

		case 4: // '\004'
			LogLog.debug("Appender [" + name + "] to be rolled at start of week.");
			break;

		case 5: // '\005'
			LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");
			break;

		default:
			LogLog.warn("Unknown periodicity for appender [" + name + "].");
			break;
		}
	}

	void rollOver() throws IOException {
		if (datePattern == null) {
			errorHandler.error("Missing DatePattern option in rollOver().");
			return;
		}
		String datedFilename = fileName + sdf.format(now);
		if (scheduledFilename.equals(datedFilename))
			return;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(now);
		gc.add(5, -1 * fileReservePeriod);
		String delfilename = fileName + sdf.format(gc.getTime()) + ".zip";
		File delfile = new File(delfilename);
		if (delfile.exists())
			delfile.delete();
		gc = null;
		delfile = null;
		closeFile();
		File target = new File(scheduledFilename);
		if (target.exists())
			target.delete();
		File file = new File(fileName);
		boolean result = file.renameTo(target);
		if (result) {
			LogLog.debug(fileName + " -> " + scheduledFilename);
			result = zip(target, scheduledFilename);
			if (result)
				LogLog.debug(fileName + " -> " + scheduledFilename + ".gzip");
			else
				LogLog.error("Failed to zip [" + fileName + "] to [" + scheduledFilename + ".gzip].");
			target.delete();
		} else {
			LogLog.error("Failed to rename [" + fileName + "] to [" + scheduledFilename + ".gzip].");
		}
		try {
			setFile(fileName, false, bufferedIO, bufferSize);
		} catch (IOException e) {
			errorHandler.error("setFile(" + fileName + ", false) call failed.");
		}
		scheduledFilename = datedFilename;
	}

	public void setDatePattern(String pattern) {
		datePattern = pattern;
	}

	public void setFileReservePeriod(int fileReservePeriod) {
		this.fileReservePeriod = fileReservePeriod;
	}

	protected void subAppend(LoggingEvent event) {
		long n = System.currentTimeMillis();
		if (n >= nextCheck) {
			now.setTime(n);
			nextCheck = rc.getNextCheckMillis(now);
			try {
				rollOver();
			} catch (IOException ioe) {
				LogLog.error("rollOver() failed.", ioe);
			}
		}
		super.subAppend(event);
	}

}