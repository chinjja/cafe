package com.chinjja.issue.domain;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PostData {
	@NotBlank
	@NonNull
	private String title;
	@NotBlank
	@NonNull
	private String contents;
	
	@Transient
	@NotNull
	private Long categoryId;
}
