package com.chinjja.issue.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Formula;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Cafe {
	@Id
	@Pattern(regexp = "[a-z0-9]{1,20}")
	@NonNull
	private String id;
	@NotBlank
	@NonNull
	private String name;
	private String description;
	
	private LocalDateTime createdAt;
	
	@ManyToOne
	@NotNull
	@NonNull
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
	
	@OneToMany(mappedBy = "id.cafe")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<CafeMember> members = new HashSet<>();
	
	@Formula("(select count(cm.member_id) from cafe_member cm where cm.cafe_id = id)")
	private int memberCount;
}
