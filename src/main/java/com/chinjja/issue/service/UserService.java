package com.chinjja.issue.service;

import javax.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.data.UserRoleRepository;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.domain.UserRole;
import com.chinjja.issue.security.RegisterForm;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepo;
	private final UserRoleRepository userRoleRepo;
	private final PasswordEncoder passwordEncoder;
	
	@PostConstruct
	@Transactional
	public void onCreate() {
		if(userRepo.findByUsername("admin") == null) {
			val user = new User();
			user.setUsername("admin");
			user.setPassword("1234");
			create(user, "ROLE_ADMIN");
		}
	}
	
	@Transactional
	public void addRole(User user, String role) {
		userRoleRepo.save(UserRole.create(user, role));
	}
	
	@Transactional
	public void removeRole(User user, String role) {
		userRoleRepo.deleteById(new UserRole.Id(user, role));
	}
	
	public boolean hasRole(User user, String role) {
		return userRoleRepo.existsById(new UserRole.Id(user, role));
	}
	
	@Transactional
	public User create(User user, String...roles) {
		user.setId(null);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user = userRepo.save(user);
		
		if(roles != null) {
			for(String role : roles) {
				userRoleRepo.save(UserRole.create(user, role));
			}
		}
		return user;
	}
	
	@Transactional
	public User register(RegisterForm form) {
		String password1 = form.getPassword();
		String password2 = form.getConfirm();
		if(password1 == null || !password1.equals(password2)) {
			throw new IllegalArgumentException("invalid password");
		}
		val user = new User();
		user.setUsername(form.getUsername());
		user.setPassword(password1);
		return create(user);
	}
	
	@Transactional
	public User changePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		return userRepo.save(user);
	}
	
	public User byUsername(String username) {
		return userRepo.findByUsername(username);
	}
	
	public User byId(Long id) {
		return userRepo.findById(id).get();
	}
	
	public boolean matchPassword(User user, String rawPassword) {
		return passwordEncoder.matches(rawPassword, user.getPassword());
	}
}
