package com.chinjja.issue.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Element {
	@Id
	@GeneratedValue
	private Long id;
	private LocalDateTime createdAt;
	@ManyToOne
	@NotNull
	private User user;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@Transient
	private int likeCount;
}
