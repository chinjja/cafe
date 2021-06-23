package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class User implements UserDetails {
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique = true)
	private String username;
	private String password;
	
	private String[] roles = {"ROLE_USER"};
	
	@OneToMany(mappedBy = "owner")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Cafe> cafes = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(
			name = "cafe_members",
			joinColumns = @JoinColumn(name = "members_id"),
			inverseJoinColumns = @JoinColumn(name = "cafe_id"))
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Cafe> joinedCafes = new ArrayList<>();
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> list = new ArrayList<>();
		for(String role : getRoles()) {
			list.add(new SimpleGrantedAuthority(role));
		}
		return list;
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
	
	public boolean isAdmin() {
		for(String role : getRoles()) {
			if("ROLE_ADMIN".equals(role)) return true;
		}
		return false;
	}
}
