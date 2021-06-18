package com.chinjja.issue.domain;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogData {
	@NotBlank
	private String title;
	@NotBlank
	private String contents;
}
