package com.chinjja.issue.domain;

import java.time.LocalDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CafeMember {
	@EmbeddedId
	@NonNull
	private CafeMemberId id;
	
	private LocalDateTime createdAt;
	@NotBlank
	@NonNull
	private String greeting;
	
	private boolean approved;
}
