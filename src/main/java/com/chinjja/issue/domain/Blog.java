package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Blog extends Element {
	private int viewCount;
	
	@Embedded
	private BlogData data;
	
	@OneToMany
	@JoinColumn(name = "target_id")
	private List<Comment> comments = new ArrayList<>();
}
