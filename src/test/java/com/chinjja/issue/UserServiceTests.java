package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.data.UserRoleRepository;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.domain.UserRole;
import com.chinjja.issue.security.RegisterForm;
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
	PasswordEncoderImpl encoder;
	
	static class PasswordEncoderImpl implements PasswordEncoder {
		@Override
		public String encode(CharSequence rawPassword) {
			return "@" + rawPassword;
		}

		@Override
		public boolean matches(CharSequence rawPassword, String encodedPassword) {
			return encodedPassword.equals(encode(rawPassword));
		}
	}
	
	@Test
	void testEncorder() {
		assertNotEquals("1234", encoder.encode("1234"));
	}
	
	static Iterable<List<String>> shouldCreateUser() {
		return Arrays.asList(
				Arrays.asList(),
				Arrays.asList("ROLE_USER", "ROLE_ADMIN")
				);
	}
	
	@ParameterizedTest
	@MethodSource
	void shouldCreateUser(List<String> roles) {
		val user1 = new User();
		user1.setId(null);
		user1.setUsername("admin");
		user1.setPassword(encoder.encode("1234"));
		
		val user2 = new User();
		user2.setId(1L);
		user2.setUsername("admin");
		user2.setPassword(encoder.encode("1234"));
		
		doReturn(user2).when(userRepo).save(user1);
		

		val user3 = new User();
		user3.setId(null);
		user3.setUsername("admin");
		user3.setPassword("1234");
		val user = service.create(user3, roles.toArray(new String[roles.size()]));
		assertEquals(user2, user);
		
		verify(userRepo).save(user3);
		for(val role : roles) {
			verify(userRoleRepo).save(UserRole.create(user, role));
		}
	}
	
	@Test
	void shouldCreateAdminUserIfNotExists() {
		val withId = new User();
		withId.setId(1L);
		withId.setUsername("admin");
		withId.setPassword(encoder.encode("1234"));
		
		doReturn(null).when(userRepo).findByUsername("admin");
		doReturn(withId).when(userRepo).save(any());
		
		service.onCreate();
		
		val withoutId = new User();
		withoutId.setId(null);
		withoutId.setUsername("admin");
		withoutId.setPassword(encoder.encode("1234"));
		
		verify(userRepo).save(withoutId);
		verify(userRoleRepo).save(UserRole.create(withId, "ROLE_ADMIN"));
	}
	
	@Test
	void shouldNotCreateAdminUserIfExists() {
		val user1 = new User();
		user1.setId(1L);
		user1.setUsername("admin");
		user1.setPassword(encoder.encode("1234"));
		
		doReturn(user1).when(userRepo).findByUsername("admin");
		
		service.onCreate();
		
		verify(userRepo, never()).save(any());
		verify(userRoleRepo, never()).save(any());
	}
	
	@Test
	void register() {
		val withId = new User();
		withId.setId(1L);
		withId.setUsername("admin");
		withId.setPassword(encoder.encode("1234"));
		
		doReturn(withId).when(userRepo).save(any());
		
		val form = new RegisterForm();
		form.setUsername("admin");
		form.setPassword("1234");
		form.setConfirm("1234");
		
		val new_user = service.register(form);
		assertEquals(encoder.encode("1234"), new_user.getPassword());
		
		val withoutId = new User();
		withoutId.setId(null);
		withoutId.setUsername("admin");
		withoutId.setPassword(encoder.encode("1234"));
		
		verify(userRepo).save(withoutId);
		verify(userRoleRepo, never()).save(any());
	}
	
	@Test
	void shouldFailWhenInvalidPasswordIsUsed() {
		assertThrows(Exception.class, () -> {
			val form = new RegisterForm();
			form.setUsername("user1");
			form.setPassword("1234");
			form.setConfirm("123");
			service.register(form);
		});
		
		assertThrows(Exception.class, () -> {
			val form = new RegisterForm();
			form.setUsername("user1");
			form.setPassword(null);
			form.setConfirm("123");
			service.register(form);
		});
	}
	
	@Nested
	class WithUser {
		User user;
		
		@BeforeEach
		void beforeEach() {
			user = new User();
			user.setId(1L);
			user.setUsername("admin");
			user.setPassword(encoder.encode("1234"));
		}
		
		@Test
		void addRole() {
			service.addRole(user, "ROLE_ADMIN");
			
			verify(userRepo, never()).save(any());
			verify(userRoleRepo).save(UserRole.create(user, "ROLE_ADMIN"));
		}
		
		@Test
		void deleteRole() {
			service.removeRole(user, "ROLE_ADMIN");
			
			verify(userRepo, never()).save(any());
			verify(userRoleRepo).deleteById(new UserRole.Id(user, "ROLE_ADMIN"));
		}
		
		@Test
		void hasRole() {
			service.hasRole(user, "ROLE_ADMIN");
			
			verify(userRepo, never()).save(any());
			verify(userRoleRepo).existsById(new UserRole.Id(user, "ROLE_ADMIN"));
		}
		
		@Test
		void shouldChangePassword() {
			val user1 = new User();
			user1.setId(1L);
			user1.setUsername("admin");
			user1.setPassword(encoder.encode("5678"));
			
			doReturn(user1).when(userRepo).save(any());
			
			val changed_user = service.changePassword(user, "5678");
			assertEquals(encoder.encode("5678"), changed_user.getPassword());
			
			val user2 = new User();
			user2.setId(1L);
			user2.setUsername("admin");
			user2.setPassword(encoder.encode("5678"));
			verify(userRepo).save(user2);
			verify(userRoleRepo, never()).save(any());
		}
	}
}
