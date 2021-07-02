package com.chinjja.issue.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
public class Cafe {
	@Id
	@Pattern(regexp = "[a-z0-9]{1,20}")
	private String id;
	
	@NotBlank
	@NotNull
	private String name;
	
	@NotBlank
	@NotNull
	private String welcome;
	
	@NotBlank
	@NotNull
	private String description;
	private boolean needApproval;
	
	@NotNull
	private LocalDateTime createdAt;
	private boolean privacy;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@ManyToOne
	@NotNull
	private User owner;
	
	@OneToMany(mappedBy = "id.cafe")
	@Where(clause = "approved = true")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private List<CafeMember> members = new ArrayList<>();
	
	@OneToMany(mappedBy = "cafe")
	@Where(clause = "parent_id is null")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private List<Category> rootCategories = new ArrayList<>();
	
	@Formula("(select count(cm.member_id) from cafe_member cm where cm.cafe_id = id and cm.approved = true)")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private int memberCount;
}
