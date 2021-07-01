package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Category {
	public static enum Type {
		DIRECTORY,
		POST
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank
	@NotNull
	private String name;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private Type type = Type.DIRECTORY;
	
	@ManyToOne
	@NotNull
	private Cafe cafe;
	
	@ManyToOne
	private Category parent;
	
	@OneToMany(mappedBy = "parent")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Category> categories = new ArrayList<>();
	
	@OneToMany(mappedBy = "category")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Post> posts = new ArrayList<>();
	
	public boolean isDirectory() {
		return type == Type.DIRECTORY;
	}
	
	public boolean isPost() {
		return type == Type.POST;
	}
}
