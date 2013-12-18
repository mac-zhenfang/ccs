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
	errorMap.put("detailMessage", "The request sent by the client was syntactically incorrect.");
	errorMap.put("reason", "The request sent by the client was syntactically incorrect.");
	response.addHeader("content-type","text/plain");
	out.print(mapper.writeValueAsString(errorMap));
%>