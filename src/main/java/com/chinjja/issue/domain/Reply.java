package com.chinjja.issue.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Reply {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private Issue issue;
	
	@ManyToOne
	@NotNull
	private User user;
	
	@NotBlank
	private String comment;
	
	@NotNull
	private LocalDateTime createdAt;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
}
