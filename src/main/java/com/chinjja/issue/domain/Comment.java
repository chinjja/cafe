package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends Element {
	@ManyToOne
	private Element target;
	
	@Embedded
	private CommentData data;
	
	@OneToMany(mappedBy = "target")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Comment> comments = new ArrayList<>();
}
