package com.max;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class AppProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppProxyApplication.class, args);
	}

}
