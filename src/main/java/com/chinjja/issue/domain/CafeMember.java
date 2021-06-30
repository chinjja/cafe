package com.chinjja.issue.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CafeMember {
	@EmbeddedId
	private Id id;
	
	@NotNull
	private LocalDateTime createdAt;
	
	@NotBlank
	@NotNull
	private String greeting;
	
	private boolean approved;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@Value
	@NoArgsConstructor(force = true)
	@AllArgsConstructor
	public static class Id implements Serializable {
		@ManyToOne
		private Cafe cafe;
		
		@ManyToOne
		private User member;
	}
}
