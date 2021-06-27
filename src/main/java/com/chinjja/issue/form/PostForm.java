package com.chinjja.issue.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PostForm {
	@NotBlank
	private String title;
	
	@NotBlank
	private String contents;
	
	@NotNull
	private Long categoryId;
}
