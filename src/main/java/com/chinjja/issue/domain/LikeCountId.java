package com.chinjja.issue.domain;

import java.io.Serializable;

import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCountId implements Serializable {
	@ManyToOne
	private Likable likable;
	
	@ManyToOne
	private User user;
}
