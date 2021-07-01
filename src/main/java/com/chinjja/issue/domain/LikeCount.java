package com.chinjja.issue.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;

@Entity
@Data
public class LikeCount {
	@EmbeddedId
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
		val o = new LikeCount();
		o.setId(new Id(likable, user));
		return o;
	}
}
