package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	
	@Embedded
	private CategoryData data;
	
	@ManyToOne
	private Category parent;
	
	@ManyToOne
	@NotNull
	private Cafe cafe;
	
	@OneToMany(mappedBy = "parent")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Category> categories = new ArrayList<>();
	
	@OneToMany(mappedBy = "category")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Post> posts = new ArrayList<>();
}
