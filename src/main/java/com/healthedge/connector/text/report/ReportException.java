/*
 * Copyright © 2001-2018 HealthEdge Software, Inc. All Rights Reserved.
 *
 * This software is proprietary information of HealthEdge Software, Inc.
 * and may not be reproduced or redistributed for any purpose.
 */

package com.healthedge.connector.text.report;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jtripathy
 *
 */
public class ReportException extends RuntimeException {
	private static final Logger logger = Logger.getLogger(ReportException.class.getName());
	private static final long serialVersionUID = 1L;


	public ReportException(String message, Exception e) {
		super(message, e);
		logStackTrace(e);
	}

	public ReportException(String message) {
		super(message);
	}
	
	private void logStackTrace(Throwable throwable) {
        final String stackTrace = getStackTrace(throwable);
    	logger.log(Level.SEVERE, getMessage() + " ==> " + stackTrace, throwable);
    }

	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		if(t != null){
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.flush();
			sw.flush();
		}
		return sw.toString();
	}
}
