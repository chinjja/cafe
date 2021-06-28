package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.data.UserRoleRepository;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.service.UserService;

import lombok.val;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
	@InjectMocks
	UserService service;
	
	@Mock
	UserRepository userRepo;
	
	@Mock
	UserRoleRepository userRoleRepo;
	
	@Spy
	BCryptPasswordEncoder encoder;
	
	@Test
	void contextLoad() {
		
	}
	
	@Test
	void shouldCreateUser() {
		val encoded = User.builder()
				.username("admin")
				.password(encoder.encode("1234"));
		
		doReturn(encoded.id(1L).build()).when(userRepo).save(any());
		
		val raw = User.builder()
				.username("admin")
				.password("1234");
		val user = service.create(raw.build());
		
		assertNotNull(user);
		assertEquals(1, user.getId());
		assertTrue(encoder.matches("1234", user.getPassword()));
		
		verify(userRepo).save(any());
	}
}
