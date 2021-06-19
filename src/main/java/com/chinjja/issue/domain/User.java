package com.chinjja.issue.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

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
