<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="com.cisco.css.ErrorCode"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.codehaus.jackson.map.ObjectMapper"%>
<%!
	private static ObjectMapper mapper = new ObjectMapper();
%>
<%
	Map<String, String> errorMap = new HashMap<String, String>();
	errorMap.put("result", "failed");
	errorMap.put("errorCode", ErrorCode.COMMON_UNDEFINED_ERROR);
	errorMap.put("detailMessage", "Request method not supported");
	errorMap.put("reason", "Request method not supported");
	response.addHeader("content-type","text/plain");
	out.print(mapper.writeValueAsString(errorMap));
%>