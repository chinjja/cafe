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

import org.hibernate.annotations.Formula;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(builderMethodName = "likable", toBuilder = true)
@Inheritance(strategy = InheritanceType.JOINED)
public class Likable {
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
	
	@OneToMany(mappedBy = "id.likable")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<LikeCount> likes = new ArrayList<>();
	
	@OneToMany(mappedBy = "likable")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Comment> comments = new ArrayList<>();
	
	@Formula("(select count(lc.user_id) from like_count lc where lc.likable_id = id)")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private int likeCount;
}
