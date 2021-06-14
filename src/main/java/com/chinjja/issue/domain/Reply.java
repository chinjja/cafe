package com.chinjja.issue.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Issue issue;
	
	@ManyToOne
	@NotNull
	private User user;
	
	@NotBlank
	private String comment;
	
	@NotNull
	private Date createdAt;
	
	@PrePersist
	private void createdAt() {
		createdAt = new Date();
	}
}
