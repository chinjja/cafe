package com.chinjja.issue.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Element {
	@Id
	@GeneratedValue
	private Long id;
	private LocalDateTime createdAt;
	@ManyToOne
	@NotNull
	private User user;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@OneToMany(mappedBy = "id.target")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<LikeCount> likes = new ArrayList<>();
}
