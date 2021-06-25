package com.chinjja.issue.web;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class JoinCafeForm {
	@NotBlank
	private String greeting;
}
