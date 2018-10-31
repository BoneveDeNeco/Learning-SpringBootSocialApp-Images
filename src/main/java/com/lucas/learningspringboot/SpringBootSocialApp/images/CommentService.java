package com.lucas.learningspringboot.SpringBootSocialApp.images;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class CommentService {
	
	RestTemplate restTemplate;
	
	public CommentService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@HystrixCommand(fallbackMethod = "defaultComments") //Can handle failures, such as remote service down.
	public List<Comment> getComments(Image image) {
		return restTemplate.exchange(
				"http://COMMENTS/comments/{imageId}",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<Comment>>() {},
				image.getId()).getBody();
	}
	
	protected List<Comment> defaultComments(Image image) {
		return Collections.emptyList();
	}
}
