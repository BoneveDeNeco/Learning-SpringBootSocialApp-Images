package com.lucas.learningspringboot.SpringBootSocialApp;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.lucas.learningspringboot.SpringBootSocialApp.images.Comment;
import com.lucas.learningspringboot.SpringBootSocialApp.images.CommentService;
import com.lucas.learningspringboot.SpringBootSocialApp.images.Image;
import com.lucas.learningspringboot.SpringBootSocialApp.images.ImageService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {
	protected static final String BASE_PATH = "/images";
	private static final String FILENAME = "{filename:.+}";
	
	private final ImageService imageService;
	private final RestTemplate restTemplate;
	private final CommentService commentService;
	
	@Autowired
	public HomeController(ImageService imageService, RestTemplate restTemplate, CommentService commentService) {
		this.imageService = imageService;
		this.restTemplate = restTemplate;
		this.commentService = commentService;
	}
	
	@GetMapping(value = BASE_PATH + "/" + FILENAME + "/raw",
			produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public Mono<ResponseEntity<?>> oneRawImage(
			@PathVariable String filename) {
		return imageService.findImage(filename)
				.map(resource -> {
					try {
						return ResponseEntity.ok()
								.contentLength(resource.contentLength())
								.body(new InputStreamResource(
										resource.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
						return ResponseEntity.badRequest()
							.body("Couldn't find " + filename + 
									" => " + e.getMessage());
					}
				});
	}
	
	@PostMapping(value=BASE_PATH)
	public Mono<String> createFile(@RequestPart(name = "file") Flux<FilePart> files) {
		return imageService.createImage(files)
				.then(Mono.just("redirect:/"));
	}
	
	@DeleteMapping(value=BASE_PATH + "/" + FILENAME)
	public Mono<String> deleteFile(@PathVariable String filename) {
		return imageService.deleteImage(filename)
			.then(Mono.just("redirect:/"));
	}
	
	@GetMapping(value="/")
	public Mono<String> index(Model model) {
		model.addAttribute("images", 
				imageService.findAllImages()
				.map(image -> {
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", image.getId());
					map.put("name", image.getName());
					/*
					 * RestTemplate is blocking, but supports Eureka logical hostname resolution.
					 * Consider using to WebClient in the future, when it supports logical hostname resolution
					 */
					map.put("comments", commentService.getComments(image));
					return map;
				}));
		return Mono.just("index");
	}
} 
