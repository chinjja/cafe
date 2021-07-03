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

import org.hibernate.annotations.Formula;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
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
	private String title;
	
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
	
	@Formula("(select count(c.id) from category c where c.parent_id = id)")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private int categoryCount;
	
	@OneToMany(mappedBy = "category")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Post> posts = new ArrayList<>();
	
	@Formula("(select count(p.id) from post p where p.category_id = id)")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private int postCount;
	
	public boolean isDirectory() {
		return type == Type.DIRECTORY;
	}
	
	public boolean isPost() {
		return type == Type.POST;
	}
}
