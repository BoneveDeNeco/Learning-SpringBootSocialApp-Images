package com.lucas.learningspringboot.SpringBootSocialApp.images;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CommentServiceTests {
	
	private static final Image AN_IMAGE = new Image("1", "Image");
	public static final Comment A_COMMENT = new Comment("c1", "1", "Good");
	
	CommentService commentService;
	RestTemplate restTemplate;
	
	@Before
	public void setup() {
		commentService = new CommentService(restTemplate);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getsAllCommentsForImage() {
		restTemplate = mock(RestTemplate.class);
		
		List<Comment> comments = Arrays.asList(A_COMMENT);
		ResponseEntity<List<Comment>> response = new ResponseEntity<List<Comment>>(comments, HttpStatus.OK);
		
		when(restTemplate.exchange(
				eq("http://COMMENTS/comments/{imageId}"),
				eq(HttpMethod.GET),
				isNull(),
				any(ParameterizedTypeReference.class),
				eq("1")))
		.thenReturn(response);
		
		List<Comment> imageComments = commentService.getComments(AN_IMAGE);
		assertThat(imageComments).contains(A_COMMENT);
	}
	
	@Test
	public void defaultCommentsIsAnEmptyList() {
		List<Comment> comments = commentService.defaultComments(AN_IMAGE);
		assertThat(comments).isEmpty();
	}
}
