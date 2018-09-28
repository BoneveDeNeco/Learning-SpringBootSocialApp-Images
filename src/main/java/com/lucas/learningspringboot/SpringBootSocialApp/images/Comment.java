package com.lucas.learningspringboot.SpringBootSocialApp.images;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
	@Id private String id;
	private String imageId;
	private String comment;
}
