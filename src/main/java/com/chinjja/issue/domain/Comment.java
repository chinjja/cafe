package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Comment extends Likable {
	public Comment(User user, Likable likable, CommentData data) {
		super(user);
		setLikable(likable);
		setData(data);
	}
	
	@ManyToOne
	@NotNull
	private Likable likable;
	
	@Embedded
	private CommentData data;
	
	@OneToMany(mappedBy = "likable")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Comment> comments = new ArrayList<>();
}
