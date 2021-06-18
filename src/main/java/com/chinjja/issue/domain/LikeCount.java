package com.chinjja.issue.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "target_id", "user_id" }))
public class LikeCount {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@NotNull
	private Element target;
	
	@ManyToOne
	@NotNull
	private User user;
}
