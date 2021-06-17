package com.chinjja.issue.domain;

import java.time.LocalDateTime;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Issue {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@NotNull
	private User user;
	
	@Valid
	@Embedded
	private IssueData data;
	
	@NotNull
	private LocalDateTime createdAt;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}

}
