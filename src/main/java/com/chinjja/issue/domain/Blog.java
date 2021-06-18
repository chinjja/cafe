package com.chinjja.issue.domain;

import java.util.ArrayList;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Blog extends Element {
	private int viewCount;
	
	@Embedded
	private BlogData data;
	
	@Transient
	private Iterable<Comment> comments = new ArrayList<>();
}
