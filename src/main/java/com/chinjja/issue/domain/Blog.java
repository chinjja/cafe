package com.chinjja.issue.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Blog extends Element {
	private int viewCount;
	
	@Embedded
	private BlogData data;
}
