package com.max.util;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class ReqUtil {
	
	public static final String IS_PROXY = "IS_PROXY";
	public static final String HEADER_PROXY_ = "HEADER_PROXY_";
	
	public static final String REMOTE_ADDR = "REMOTE_ADDR";
	public static final String REMOTE_HOST = "REMOTE_HOST";
	public static final String SERVER_NAME = "SERVER_NAME";
	public static final String SERVER_PORT = "SERVER_PORT";
	public static final String PROTOCOL = "PROTOCOL";
	public static final String CONTENT_LENGTH = "CONTENT_LENGTH";
	
	public static final String X_FORWARDED_FOR = "x-forwarded-for";
	public static final String WE_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
	public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
	
	public static void show(HttpServletRequest request) {
		showMessage(request);
		showHeader(request);
		showParam(request);
	}
	
	public static void showMessage(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		
	    sb.append("RemoteAddr()=="+request.getRemoteAddr()+"\n");
	    sb.append("RemoteHost()=="+request.getRemoteHost()+"\n");
	    sb.append("ServerName()=="+request.getServerName()+"\n");
	    sb.append("ServerPort()=="+request.getServerPort()+"\n");
	    sb.append("Method()=="+request.getMethod()+"\n");
	    sb.append("Protocol()=="+request.getProtocol()+"\n");
	    sb.append("ContentLength()=="+request.getContentLength()+"\n");
	    
	    sb.append("Header(x-forwarded-for)=="+request.getHeader("x-forwarded-for")+"\n");
	    sb.append("Header(WL-Proxy-Client-IP)=="+request.getHeader("WL-Proxy-Client-IP")+"\n");
	    sb.append("Header(Proxy-Client-IP)=="+request.getHeader("Proxy-Client-IP")+"\n");
		
		System.out.println("showMessage");
		System.out.println(sb.toString());
	}
	
	public static void showHeader(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		sb.append("request.getHeaderNames()=="+request.getHeaderNames()+"\n");
		while (headerNames.hasMoreElements()) {
		   String key = headerNames.nextElement();
		   String value = request.getHeader(key);
		   sb.append("header的參數："+key+"=="+value+"\n");
		}
		System.out.println("showHeader");
		System.out.println(sb.toString());
	}
	
	public static HttpHeaders getHeader(HttpServletRequest request) {
	    HttpHeaders headers = new HttpHeaders();
	    Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			headers.add(key, value);
		}
	    return headers;
	}
	
	public static HttpHeaders copyHeader(HttpServletRequest request) {
	    HttpHeaders headers = new HttpHeaders();
	    Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			headers.add(HEADER_PROXY_ + key, value);
		}
		headers.add(IS_PROXY, "true");
		copyAddress(request, headers);
	    return headers;
	}
	
	public static void copyAddress(HttpServletRequest request, HttpHeaders headers) {
		headers.add(REMOTE_ADDR, request.getRemoteAddr());
		headers.add(REMOTE_HOST, request.getRemoteHost());
		headers.add(SERVER_NAME, request.getServerName());
		headers.add(SERVER_PORT, String.valueOf(request.getServerPort()));
		headers.add(PROTOCOL, request.getProtocol());
		headers.add(CONTENT_LENGTH, String.valueOf(request.getContentLength()));
	}
	
	public static void setAddress(HttpServletRequest request, BodyReaderHttpServletRequestWrapper requestWrapper) {
		requestWrapper.setRemoteAddr(request.getHeader(REMOTE_ADDR));
		requestWrapper.setRemoteHost(request.getHeader(REMOTE_HOST));
		requestWrapper.setServerName(request.getHeader(SERVER_NAME));
		requestWrapper.setServerPort(request.getHeader(SERVER_PORT));
		requestWrapper.setProtocol(request.getHeader(PROTOCOL));
		requestWrapper.setContentLength(request.getHeader(CONTENT_LENGTH));
	}
	
	public static void showParam(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		
		Enumeration<String> paramNames = request.getParameterNames();
		sb.append("request.getParameterNames()=="+request.getParameterNames() + "\n");
		while (paramNames.hasMoreElements()) {
		   String paramName = (String) paramNames.nextElement();
		   String[] paramValues = request.getParameterValues(paramName);
		   if (paramValues.length == 1) {
			   String paramValue = paramValues[0];
			   if (paramValue.length() != 0) {
				   sb.append("参数：" + paramName + "==" + paramValue+"\n");
			   }
		   }
		}
		System.out.println("showParam");
		System.out.println(sb.toString());
	}
	
	public static Map<String, String> getParams(HttpServletRequest request) {
		Map<String, String> params = new LinkedHashMap<>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					params.put(paramName, paramValue);
				}
			}
		}
		return params;
	}

	public static void copyProperties(HttpHeaders headers, BodyReaderHttpServletRequestWrapper requestWrapper) {
		Set<String> keys = headers.keySet();
		keys.stream().forEach(key -> {
			if (StringUtils.startsWithIgnoreCase(key, HEADER_PROXY_)) {
				String oriKey = key.substring(HEADER_PROXY_.length(), key.length());
				requestWrapper.addHeader(oriKey, headers.getFirst(key));
			}
		});
	}
	
}
