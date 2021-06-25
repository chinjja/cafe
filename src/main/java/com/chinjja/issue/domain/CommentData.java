package com.chinjja.issue.domain;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.chinjja.issue.domain.Category.Type;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CommentData {
	@Transient
	@NotNull
	private Long likableId;
	@NotBlank
	@NonNull
	private String comment;
}
