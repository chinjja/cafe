package com.chinjja.issue.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

@Entity
@Data
public class User implements UserDetails {
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique = true)
	@NotNull
	@Pattern(regexp = "[a-z0-9]{4,20}")
	private String username;
	
	@NotNull
	private String password;
	
	@NotNull
	private LocalDateTime createdAt;
	
	@PrePersist
	private void createdAt() {
		createdAt = LocalDateTime.now();
	}
	
	@OneToMany(mappedBy = "id.user", fetch = FetchType.EAGER)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<UserRole> roles = new ArrayList<>();
	
	@OneToMany(mappedBy = "owner")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Cafe> cafes = new ArrayList<>();
	
	@OneToMany(mappedBy = "id.member")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<CafeMember> joinedCafes = new ArrayList<>();
	
	@OneToMany(mappedBy = "user")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Post> posts = new ArrayList<>();
	
	@OneToMany(mappedBy = "user")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final List<Comment> comments = new ArrayList<>();
	
	@Transient
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final ArrayList<GrantedAuthority> authorities = new ArrayList<>();
	
	@Formula("(select count(ur.role) from user_role ur where ur.user_id = id and ur.role = 'ROLE_ADMIN')")
	@Setter(AccessLevel.NONE)
	private boolean admin;
	
	@PostLoad
	private void onUpdate() {
		authorities.clear();
		for(val role : getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getId().getRole()));
		}
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
}
