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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(indexes = {
		@Index(columnList = "title"),
		})
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Post extends Likable {
	public Post(User user, Category category, PostData data) {
		super(user);
		setCategory(category);
		setData(data);
	}
	
	@ManyToOne
	@NotNull
	private Category category;
	
	@Embedded
	private PostData data;
	
	private int viewCount;
	
	@OneToMany(mappedBy = "likable")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Comment> comments = new ArrayList<>();
}
