package com.chinjja.issue.domain;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.chinjja.issue.domain.Category.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CategoryData {
	@NotBlank
	@NonNull
	private String name;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	@NonNull
	private Type type;
	
	@Transient
	private Long parentId;
	
	public boolean isDirectory() {
		return type == Type.DIRECTORY;
	}
	
	public boolean isPost() {
		return type == Type.POST;
	}
}
