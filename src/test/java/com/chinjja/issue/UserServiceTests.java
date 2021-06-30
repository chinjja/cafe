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
import java.util.Optional;

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
		val expect = User.builder()
				.id(null)
				.username("admin")
				.password(encoder.encode("1234"));
		
		doReturn(expect.id(1L).build()).when(userRepo).save(expect.id(null).build());
		
		val raw = User.builder()
				.id(null)
				.username("admin")
				.password("1234");
		val user = service.create(raw.build(), roles.toArray(new String[roles.size()]));
		assertEquals(expect.id(1L).build(), user);
		
		verify(userRepo).save(expect.id(null).build());
		for(val role : roles) {
			verify(userRoleRepo).save(UserRole.create(user, role));
		}
	}
	
	@Test
	void shouldCreateAdminUserIfNotExists() {
		val template = User.builder()
				.id(null)
				.username("admin")
				.password(encoder.encode("1234"));
		
		doReturn(null).when(userRepo).findByUsername("admin");
		doReturn(template.id(1L).build()).when(userRepo).save(any());
		
		service.onCreate();
		
		verify(userRepo).save(template.id(null).build());
		verify(userRoleRepo).save(UserRole.create(template.id(1L).build(), "ROLE_ADMIN"));
	}
	
	@Test
	void shouldNotCreateAdminUserIfExists() {
		val template = User.builder()
				.id(null)
				.username("admin")
				.password(encoder.encode("1234"));
		
		doReturn(template.id(1L).build()).when(userRepo).findByUsername("admin");
		
		service.onCreate();
		
		verify(userRepo, never()).save(any());
		verify(userRoleRepo, never()).save(any());
	}
	
	@Test
	void register() {
		val template = User.builder()
				.id(null)
				.username("user1")
				.password(encoder.encode("1234"));
		doReturn(template.id(1L).build()).when(userRepo).save(any());
		
		val form = new RegisterForm();
		form.setUsername("user1");
		form.setPassword("1234");
		form.setConfirm("1234");
		
		val new_user = service.register(form);
		assertEquals(encoder.encode("1234"), new_user.getPassword());
		
		verify(userRepo).save(template.id(null).build());
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
			user = User.builder()
					.id(1L)
					.username("admin")
					.password(encoder.encode("1234"))
					.build();
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
			val template = User.builder()
					.id(1L)
					.username("admin")
					.password(encoder.encode("5678"));
			
			doReturn(template.build()).when(userRepo).save(any());
			
			val changed_user = service.changePassword(user, "5678");
			assertEquals(encoder.encode("5678"), changed_user.getPassword());
			
			verify(userRepo).save(template.build());
			verify(userRoleRepo, never()).save(any());
		}
	}
}
