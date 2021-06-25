package com.chinjja.issue.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class CafeData {
	@Pattern(regexp = "[a-z0-9]{1,20}")
	private String id;
	@NotBlank
	private String name;
	private String description;
	private boolean needApproval;
}
