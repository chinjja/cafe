package com.chinjja.issue.form;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class JoinCafeForm {
	@NotBlank
	private String greeting;
}
