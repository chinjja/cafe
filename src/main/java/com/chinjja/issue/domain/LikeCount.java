package com.chinjja.issue.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Entity
@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Builder(toBuilder = true)
public class LikeCount {
	@EmbeddedId
	@NonNull
	private Id id;
	
	@Value
	@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
	@AllArgsConstructor
	public static class Id implements Serializable {
		@ManyToOne
		private Likable likable;
		
		@ManyToOne
		private User user;
	}
	
	public static LikeCount create(Likable likable, User user) {
		return new LikeCount(new Id(likable, user));
	}
}
