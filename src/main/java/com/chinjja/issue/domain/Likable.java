package com.chinjja.issue.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Likable {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private LocalDateTime createdAt;
	
	@ManyToOne
	@NotNull
	private User user;

	@NotBlank
	@NotNull
	private String text;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@OneToMany(mappedBy = "id.likable")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<LikeUser> likes = new ArrayList<>();
	
	@Formula("(select count(lu.user_id) from like_user lu where lu.likable_id = id)")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private int likeCount;
	
	@OneToMany(mappedBy = "likable")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Comment> comments = new ArrayList<>();
	
	@Formula("(select count(c.id) from comment c where c.likable_id = id)")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private int commentCount;
}
