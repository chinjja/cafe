package com.chinjja.issue.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Entity
@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRole {
	@EmbeddedId
	private Id id;

	@Value
	@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
	@AllArgsConstructor
	public static class Id implements Serializable {
		@ManyToOne
		private User user;
		@NotBlank
		private String role;
	}
	
	public static UserRole create(User user, String role) {
		return new UserRole(new Id(user, role));
	}
}
