package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User implements UserDetails {
	@Builder(toBuilder = true)
	public User(Long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}
	
	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	@ToString.Include
	@Getter
	@Setter
	private Long id;
	
	@Column(unique = true)
	@NotNull
	@Pattern(regexp = "[a-z0-9]{4,20}")
	@EqualsAndHashCode.Include
	@ToString.Include
	@Getter
	@Setter
	private String username;
	
	@NotNull
	@EqualsAndHashCode.Include
	@ToString.Include
	@Getter
	@Setter
	private String password;
	
	@OneToMany(mappedBy = "id.user", fetch = FetchType.EAGER)
	@Getter
	private List<UserRole> roles = new ArrayList<>();
	
	@OneToMany(mappedBy = "owner")
	@Getter
	private List<Cafe> cafes = new ArrayList<>();
	
	@OneToMany(mappedBy = "id.member")
	@Getter
	private List<CafeMember> joinedCafes = new ArrayList<>();
	
	@Transient
	private ArrayList<GrantedAuthority> authorities = new ArrayList<>();
	
	@Formula("(select count(ur.role) from user_role ur where ur.user_id = id and ur.role = 'ROLE_ADMIN')")
	@Getter
	private boolean admin;
	
	@PostPersist
	@PostUpdate
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
