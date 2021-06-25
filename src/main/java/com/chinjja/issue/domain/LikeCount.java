package com.chinjja.issue.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class LikeCount {
	@EmbeddedId
	@NonNull
	private LikeCountId id;
}
