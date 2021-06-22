package com.chinjja.issue.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CafeData {
	@Size(min = 4, max = 20)
	@NotBlank
	private String id;
	@NotNull
	@NotBlank
	private String name;
	@NotNull
	private String description;
}
