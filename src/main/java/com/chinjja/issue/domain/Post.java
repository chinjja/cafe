package com.chinjja.issue.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
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
@ToString(callSuper = true)
public class Post extends Likable {
	@ManyToOne
	@NotNull
	private Category category;
	
	@NotBlank
	@NotNull
	private String title;
	
	@NotBlank
	@NotNull
	private String contents;
	
	private int viewCount;
}
