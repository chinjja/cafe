package com.chinjja.issue.form;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.chinjja.issue.domain.Category.Type;

import lombok.Data;

@Data
public class CategoryForm {
	@NotBlank
	private String title;
	
	@NotNull
	private Type type;
	
	@Transient
	private Long parentCategoryId;
}
