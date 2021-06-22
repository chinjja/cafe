package com.chinjja.issue.domain;

import javax.persistence.Column;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryData {
	@NotBlank
	private String name;
	@Column(name = "odr")
	private int order;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private Type type;
	
	@Transient
	private Long parentId;
	@Transient
	@NotNull
	private String cafeId;
	
	public boolean isDirectory() {
		return type == Type.DIRECTORY;
	}
	
	public boolean isPost() {
		return type == Type.POST;
	}
}
