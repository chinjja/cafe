package com.chinjja.issue.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.Data;

@Entity
@Data
public class LikeCount {
	@EmbeddedId
	private LikeCountId id;
}
