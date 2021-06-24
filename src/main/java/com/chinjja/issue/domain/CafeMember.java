package com.chinjja.issue.domain;

import java.time.LocalDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.Data;

@Entity
@Data
public class CafeMember {
	@EmbeddedId
	private CafeMemberId id;
	
	private LocalDateTime createdAt;
	private String greeting;
}
