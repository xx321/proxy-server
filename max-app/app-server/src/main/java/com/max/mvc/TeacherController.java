package com.max.mvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.max.config.ServerSideProperties;
import com.max.model.Teacher;
import com.max.util.ReqUtil;

@RestController
@RequestMapping("/server/teacher")
public class TeacherController {
	
	@Autowired
	private ServerSideProperties properties;
	
	@PostMapping(value = "/{id}")
	public ResponseEntity<?> post(@PathVariable(value="id") String id, @RequestBody String body, HttpServletRequest request) {
		
		System.out.println(request.getRequestURI());
		
		ReqUtil.show(request);
		System.out.println("ID : " + id);
		System.out.println("BODY : " + body);
		
		return ResponseEntity.status(HttpStatus.OK).body(getTeacher());
	}
	
	private Teacher getTeacher() {
		Teacher t = new Teacher();
		t.setServerName(properties.getServerName());
		t.setHeigh("170");
		t.setWidth("70");
		return t;
	}
	
}
