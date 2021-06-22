package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Category {
	public static enum Type {
		DIRECTORY,
		BLOG
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Embedded
	private CategoryData data;
	
	@ManyToOne
	private Category parent;
	
	@ManyToOne
	@NotNull
	private Cafe cafe;
	
	@OneToMany
	@JoinColumn(name = "parent_id")
	private List<Category> categories = new ArrayList<>();
	
	@OneToMany
	@JoinColumn(name = "category_id")
	private List<Blog> blogs = new ArrayList<>();
}
