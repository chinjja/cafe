package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.data.UserRoleRepository;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.domain.UserRole;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
public class UserTests {
	@Autowired TestEntityManager em;
	@Autowired UserRepository userRepo;
	@Autowired UserRoleRepository roleRepo;
	
	static final String username = "admin11";
	static final String password = "1234";

	User new_user() {
		return User.builder()
				.id(null)
				.username(username)
				.password(password)
				.build();
	}
	
	@Test
	void shouleFailIfPasswordIsNull() {
		assertThrows(Throwable.class, () -> {
			userRepo.save(User.builder()
					.id(null)
					.username(username)
					.password(null)
					.build());
			em.flush();
		});
	}
	
	static Iterable<String> shouldFailWithInvalidPattern() {
		List<String> list = new ArrayList<>();
		list.addAll(Arrays.asList("!@#$%^&*()_+-=[]{};':,.<>/?|`~\"".split("")));
		list.add("asdfgasdfgasdfgasdfga");
		list.add("");
		list.add(null);
		return list;
	}
	
	@ParameterizedTest
	@MethodSource
	void shouldFailWithInvalidPattern(String username) {
		val user = User.builder()
				.id(null)
				.username(username)
				.password("1234")
				.build();
		assertThrows(Throwable.class, () -> {
			userRepo.save(user);
			em.flush();
		});
	}
	
	@Nested
	class WithUser {
		User user;
		
		@BeforeEach
		void createUser() {
			user = userRepo.save(new_user());
			em.flush();
		}
		
		@Test
		void shouldExistsUser() {
			assertTrue(userRepo.existsById(user.getId()));
		}
		
		@Test
		void shouldEqualsTo() {
			val read = userRepo.findById(user.getId()).get();
			assertEquals(user, read);
		}
		
		@Test
		void shouldFailIfUsernameIsDuplicated() {
			assertThrows(Throwable.class, () -> {
				userRepo.save(new_user());
				em.flush();
			});
		}
		
		@Test
		void shouldDeleteUser() {
			userRepo.deleteById(user.getId());
			assertFalse(userRepo.existsById(user.getId()));
		}
		
		@Nested
		class WithRole {
			UserRole role;
			
			@BeforeEach
			void addRole() {
				role = roleRepo.save(UserRole.create(user, "ROLE_ADMIN"));
				em.flush();
			}
			
			@Test
			void shouldExistsRole() {
				assertTrue(roleRepo.existsById(role.getId()));
			}
			
			@Test
			void shouldRemoveRole() {
				roleRepo.deleteById(role.getId());
				assertFalse(roleRepo.existsById(role.getId()));
			}
			
			@Test
			void shouldFailIfHaveStillRole() {
				assertTrue(userRepo.existsById(user.getId()));
				assertTrue(roleRepo.existsById(role.getId()));
				userRepo.deleteById(user.getId());
				assertThrows(Throwable.class, () -> {
					em.flush();
				});
			}
		}
	}
}
