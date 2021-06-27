package com.chinjja.issue.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CommentForm {
	@NotNull
	private Long likableId;
	@NotBlank
	private String comment;
}
