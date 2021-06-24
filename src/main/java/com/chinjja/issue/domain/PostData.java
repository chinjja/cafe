package com.chinjja.issue.domain;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostData {
	@NotBlank
	private String title;
	@NotBlank
	private String contents;
	
	@Transient
	@NotNull
	private Long categoryId;
}
