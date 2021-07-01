package com.chinjja.issue.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Comment extends Likable {
	@ManyToOne
	@NotNull
	private Likable likable;
	
	@NotBlank
	@NotNull
	private String comment;
}
