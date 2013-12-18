package com.cisco.css.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cisco.css.CssException;
import com.cisco.css.ErrorCode;

public class StringValidator {
	public static void verifyEmpty(String paraName, String parameter) {
		if (parameter == null || parameter.trim().isEmpty()) {
			throw new CssException(ErrorCode.COMMON_EMPTY_STRING, paraName);
		}
	}

	public static void verifyNotNull(String paraName, String parameter) {
		if (parameter == null || parameter.isEmpty()) {
			throw new CssException(ErrorCode.COMMON_EMPTY_STRING, paraName);
		}
	}

	public static void verifyLength(String paraName, String parameter, int lengthLimit) {
		if (parameter != null && parameter.length() > lengthLimit) {
			throw new CssException(ErrorCode.COMMON_LENGTH_EXCEED, paraName, lengthLimit);
		}
	}

	public static void verifyPattern(String paraName, String parameter,
			String pattern) {
		if (parameter != null && !parameter.matches(pattern)) {
			throw new CssException(ErrorCode.COMMON_ILLEGAL_STRING,
					paraName, parameter, pattern);
		}
	}
	
	public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public static Date verifyDate(String date) {
		return verifyDate(date, DEFAULT_DATE_FORMAT);
	}
	
	public static Date verifyDate(String date, String dateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		if (date == null || date.isEmpty()) {
			throw new CssException(ErrorCode.COMMON_EMPTY_STRING, "date");
		}
		try {
			format.setLenient(false);
			return format.parse(date);
		} catch (ParseException e) {
			throw new CssException(ErrorCode.COMMON_DATEFORMAT_ILLEGAL,
					date, dateFormat);
		}
	}
	
	
	public static String encodeToHex(String s) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			hexString.append(s4);
		}
		return hexString.toString();
	}
	
	public static String decodeFromHex(String hexString, String charsetName) throws UnsupportedEncodingException {
		byte[] baKeyword = new byte[hexString.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			baKeyword[i] = (byte) (0xff & Integer.parseInt(
					hexString.substring(i * 2, i * 2 + 2), 16));
		}
		hexString = new String(baKeyword, charsetName);// UTF-16le:Not
		return hexString;
	}
}
