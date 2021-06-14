package com.chinjja.issue.domain;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueData {
	@NotBlank
	private String summary;
	@NotBlank
	private String contents;
}
