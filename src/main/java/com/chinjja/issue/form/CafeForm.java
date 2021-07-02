package com.chinjja.issue.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class CafeForm {
	@Pattern(regexp = "[a-z0-9]{1,20}")
	private String id;
	@NotBlank
	private String title;
	@NotBlank
	private String welcome;
	@NotBlank
	private String description;
	private boolean needApproval;
	private boolean privacy;
}
