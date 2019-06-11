package com.max.mvc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.max.async.HttpAsync;
import com.max.config.ProxySideProperties;
import com.max.util.ReqUtil;

@RestController
@RequestMapping("/http")
public class HttpController {
	
    private static final String PREFIX = "/http";
    
	@Autowired
	private ProxySideProperties properties;
	
	@Autowired
	private HttpAsync httpAsync;
	
	private String getUri(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.substring(PREFIX.length());
	}
	
	@RequestMapping("/**")
	public ResponseEntity<?> httpPost(@RequestBody String body, HttpServletRequest request) {
		System.out.println(request.getRequestURI());
		ReqUtil.show(request);
		System.out.println("BODY : " + body);
		String uri = getUri(request);
		
	    HttpHeaders headers = ReqUtil.copyHeader(request);
	    JSONObject obj = JSON.parseObject(body);
	    
	    HttpEntity<JSONObject> entity = new HttpEntity<>(obj, headers);
	    
	    Future<ResponseEntity<?>> result = null;
		for(String serverUrl : properties.getServerUrls().split(",")) {
		    if (!serverUrl.equals(properties.getMainUrl()))
		    	httpAsync.post(serverUrl + uri, entity);
			else
				try {
					result = httpAsync.postCallback(serverUrl + uri, entity);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		try {
			return result.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("fail!");
		}
	}

}
