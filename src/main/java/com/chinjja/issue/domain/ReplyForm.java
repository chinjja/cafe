package com.chinjja.issue.domain;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ReplyForm {
	@NotBlank
	private String comment;
}
