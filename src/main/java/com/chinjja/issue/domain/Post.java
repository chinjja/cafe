package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(indexes = {
		@Index(columnList = "title"),
		})
@Data
@EqualsAndHashCode(callSuper = true)
public class Post extends Element {
	private int viewCount;
	
	@Embedded
	private PostData data;
	
	@OneToMany(mappedBy = "target")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Comment> comments = new ArrayList<>();
	
	@ManyToOne
	@NotNull
	private Category category;
}
