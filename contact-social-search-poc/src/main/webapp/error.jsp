<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="com.cisco.css.CssException"%>
<%@ page import="java.lang.Throwable"%>
<%@ page import="java.lang.Exception"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%@ page import="org.codehaus.jackson.map.ObjectMapper"%>
<%@ page import="org.codehaus.jackson.JsonProcessingException"%>
<%@ page import="com.cisco.css.ErrorCode"%>
<%!
	private static ObjectMapper mapper = new ObjectMapper();
	private static final Logger LOG = LoggerFactory.getLogger("com.mdmp");
%>
<%
	Exception e = (Exception)request.getAttribute("javax.servlet.error.exception");
	Map<String, String> errorMap = new HashMap<String, String>();
	errorMap.put("result", "failed");
	if (e == null) {
		e = new CssException(ErrorCode.COMMON_UNDEFINED_ERROR, "");
	}
	Throwable actualException = e.getCause() == null ? e : e.getCause();
	if (actualException instanceof JsonProcessingException) {
		actualException = new CssException(ErrorCode.COMMON_JSON_PARSE_ERROR, actualException);
	}
	String message = actualException.getMessage();
	if (message == null) {
		message = "unknown exception";
	}
	if (!(actualException instanceof CssException)) {
		actualException = new CssException(ErrorCode.COMMON_UNDEFINED_ERROR, e);
	}
	errorMap.put("errorCode", ((CssException) actualException).getErrorCode());
	String detailMessage = actualException.getCause() == null ? message : actualException.getCause().getMessage();
	// errorMap.put("detailMessage", detailMessage.trim());
	LOG.error(detailMessage, actualException);
	out.print(actualException.toString());
	errorMap.put("reason", message.trim());
	response.addHeader("content-type","text/plain");
	out.print(mapper.writeValueAsString(errorMap));
%>