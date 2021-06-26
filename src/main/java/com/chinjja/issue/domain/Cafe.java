package com.chinjja.issue.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Formula;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class Cafe {
	@Builder(toBuilder = true)
	public Cafe(String id, String name, String description, boolean needApproval, LocalDateTime createdAt, User owner) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.needApproval = needApproval;
		this.createdAt = createdAt;
		this.owner = owner;
	}
	
	@Id
	@Pattern(regexp = "[a-z0-9]{1,20}")
	private String id;
	@NotBlank
	private String name;
	private String description;
	private boolean needApproval;
	private LocalDateTime createdAt;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@ManyToOne
	@NotNull
	private User owner;
	
	@OneToMany(mappedBy = "id.cafe")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private Set<CafeMember> members = new HashSet<>();
	
	@Formula("(select count(cm.member_id) from cafe_member cm where cm.cafe_id = id and cm.approved = true)")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Setter(AccessLevel.NONE)
	private int memberCount;
}
