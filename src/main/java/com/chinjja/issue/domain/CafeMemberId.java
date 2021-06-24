package com.chinjja.issue.domain;

import java.io.Serializable;

import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CafeMemberId implements Serializable {
	@ManyToOne
	private Cafe cafe;
	
	@ManyToOne
	private User member;
}
