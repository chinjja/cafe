package com.chinjja.issue.domain;

import java.util.ArrayList;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends Element {
	@ManyToOne
	private Element target;
	
	@Embedded
	private CommentData data;
	
	@Transient
	private Iterable<Comment> comments = new ArrayList<>();
}
