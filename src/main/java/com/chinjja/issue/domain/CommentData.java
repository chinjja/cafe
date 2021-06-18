package com.chinjja.issue.domain;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CommentData {
	@Transient
	private Long target;
	@NotBlank
	private String comment;
}
