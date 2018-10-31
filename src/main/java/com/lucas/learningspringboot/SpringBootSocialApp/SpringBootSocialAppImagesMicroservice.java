package com.lucas.learningspringboot.SpringBootSocialApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

@SpringCloudApplication
public class SpringBootSocialAppImagesMicroservice {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSocialAppImagesMicroservice.class, args);
	}
	
	//Delete is not a valid action in HTML5 form, but this makes thymeleaf do a small workaround to support it
	@Bean
	HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}
}
