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
import com.max.model.Student;
import com.max.util.ReqUtil;

@RestController
@RequestMapping("/server/student")
public class StudentController {
	
	@Autowired
	private ServerSideProperties properties;
	
	@PostMapping(value = "/{id}")
	public ResponseEntity<?> post(@PathVariable(value="id") String id, @RequestBody Student s, HttpServletRequest request) {
		
		System.out.println(request.getRequestURI());
		
		ReqUtil.show(request);
		System.out.println("ID : " + id);
		System.out.println("BODY : " + s);
		
		return ResponseEntity.status(HttpStatus.OK).body(getStudent());
	}
	
	private Student getStudent() {
		Student s = new Student();
		s.setServerName(properties.getServerName());
		s.setId("1");
		s.setName("max");
		return s;
	}

}
