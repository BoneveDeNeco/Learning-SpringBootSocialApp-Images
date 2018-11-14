package com.lucas.learningspringboot.SpringBootSocialApp.images;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucas.learningspringboot.SpringBootSocialApp.images.Comment;

import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Mono;

@RestController
@EnableBinding(Source.class)
public class CommentController {
	
	private final MeterRegistry meterRegistry;
	private final CommentMessageSender sender;
	
	@Autowired
	public CommentController(MeterRegistry meterRegistry, CommentMessageSender sender) {
		this.meterRegistry = meterRegistry;
		this.sender = sender;
	}

	@PostMapping("/comments")
	public Mono<ResponseEntity<?>> addComment(Mono<Comment> newComment) {
		return newComment.map(comment -> {
				sender.send(comment);
				return comment;
			})
			.log("commentController-publish:")
			.flatMap(comment -> {
				meterRegistry.counter("comments.produced", "imageId", comment.getImageId()).increment();
				return Mono.just(ResponseEntity.noContent().build());
			});
	}
}
