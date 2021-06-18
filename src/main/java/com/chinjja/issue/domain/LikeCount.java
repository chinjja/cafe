package com.chinjja.issue.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class LikeCount {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private Element element;
}
