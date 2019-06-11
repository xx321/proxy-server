package com.max.async;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

@Component
public class HttpAsync {
	
	@Autowired
	private RestTemplate template;
	
	@Async
	public void post(String serverUrl, HttpEntity<JSONObject> entity) {
		template.postForEntity(serverUrl, entity, String.class);
	}
	
    @Async
    public Future<ResponseEntity<?>> postCallback(String serverUrl, HttpEntity<JSONObject> entity) throws Exception {
	    ResponseEntity<?> result = template.postForEntity(serverUrl, entity, String.class);
	    return new AsyncResult<ResponseEntity<?>>(result);
    }

}
