package com.chinjja.issue.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Formula;

import lombok.Data;

@Entity
@Data
public class Cafe {
	@Id
	@Size(min = 4, max = 20)
	@NotBlank
	private String id;
	@NotNull
	@NotBlank
	private String name;
	@NotNull
	private String description;
	
	private LocalDateTime createdAt;
	
	@ManyToOne
	@NotNull
	private User owner;
	
	public void setData(CafeData data) {
		setId(data.getId());
		setName(data.getName());
		setDescription(data.getDescription());
	}
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@ManyToMany
	private Set<User> members = new HashSet<>();
	
	@Formula("(select count(cm.members_id) from cafe_members cm where cm.cafe_id = id)")
	private int memberCount;
}
