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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails {
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique = true)
	@NotBlank
	@Pattern(regexp = "[a-z0-9]{4,20}")
	@NonNull
	private String username;
	private String password;
	
	private String[] roles = {"ROLE_USER"};
	
	@OneToMany(mappedBy = "owner")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Cafe> cafes = new ArrayList<>();
	
	@OneToMany(mappedBy = "id.member")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<CafeMember> joinedCafes = new ArrayList<>();
	
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
