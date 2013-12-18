package com.cisco.css;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class CssException extends RuntimeException {
	private static ResourceBundle rb = ResourceBundle.getBundle("errorCode");

	private String errorCode;
	private String message;
	private Throwable cause = this;
	
	public CssException(){
		
	}
	public CssException(String errorCode) {
		this.errorCode = errorCode;
		init(null);
	}

	public CssException(String errorCode, Object... parameters) {
		this.errorCode = errorCode;
		init(parameters);
	}

	public CssException(String errorCode, Throwable cause) {
		this.errorCode = errorCode;
		this.cause = cause;
		init(null);
	}

	public CssException(String errorCode, Throwable cause,
			Object... parameters) {
		this.errorCode = errorCode;
		this.cause = cause;
		init(parameters);
	}

	private void init(Object[] parameters) {
		if (errorCode == null) {
			message = "unknown exception";
			return;
		}
		message = rb.getString(errorCode);
		if (message == null) {
			message = errorCode;
		}
		message = MessageFormat.format(message, parameters);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getCause() {
		return (cause == this ? null : cause);
	}

}
