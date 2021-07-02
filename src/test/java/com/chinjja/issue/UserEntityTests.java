package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.issue.data.PostRepository;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class UserEntityTests {
	@Autowired TestEntityManager em;
	@Autowired PostRepository postRepo;
	TestEntityGenerator gen;

	User owner;
	
	User load() {
		em.flush();
		em.clear();
		return em.find(User.class, owner.getId());
	}
	
	@BeforeEach
	void prepare() {
		gen = new TestEntityGenerator(em);
		owner = gen.user("owner");
	}

	@Test
	void shouldFailIfAlreadyExistsCafeId() {
		gen.cafe("cafe1", owner);
		assertThrows(Throwable.class, () -> {
			gen.cafe("cafe1", owner);
		});
	}
	
	@Test
	void shouldHaveEmptyCafe() {
		val user = load();
		assertEquals(0, user.getCafes().size());
	}
	
	@Test
	void shouldHaveCafeList() {
		gen.cafe("cafe1", owner);
		gen.cafe("cafe2", owner);

		val user = load();
		assertEquals(2, user.getCafes().size());
	}
	
	@Test
	void shouldHaveEmptyRole() {
		val user = load();
		assertEquals(0, user.getRoles().size());
		assertEquals(0, user.getAuthorities().size());
	}
	
	@Test
	void shouldFailIfAlreadyExistsRole() {
		gen.role(owner, "ROLE_USER");
		assertThrows(Throwable.class, () -> {
			gen.role(owner, "ROLE_USER");
		});
	}
	
	@Test
	void shouldHaveTwoRole() {
		gen.role(owner, "ROLE_USER");
		gen.role(owner, "ROLE_ADMIN");

		val user = load();
		assertEquals(2, user.getRoles().size());
		assertEquals(2, user.getAuthorities().size());
	}
	
	@Test
	void shouldBeNotAdmin() {
		val user = load();
		assertFalse(user.isAdmin());
	}
	
	@Test
	void shouldBeAdmin() {
		gen.role(owner, "ROLE_ADMIN");
		
		val user = load();
		assertEquals(1, user.getRoles().size());
		assertTrue(user.isAdmin());
	}
	
	@Test
	void shouldHaveEmptyPost() {
		val user = load();
		
		assertEquals(0, user.getPosts().size());
	}
	
	@Test
	void shouldHavePost() {
		val cafe = gen.cafe("cafe", owner);
		val category = gen.category(cafe, null);
		val post = gen.post(category, owner);
		gen.comment(post, owner);
		assertEquals(1, load().getPosts().size());
		
		gen.post(category, owner);
		assertEquals(2, load().getPosts().size());
	}
	
	@Test
	void shouldHaveEmptyComment() {
		val user = load();
		
		assertEquals(0, user.getComments().size());
	}
	
	@Test
	void shouldHaveComment() {
		val cafe = gen.cafe("cafe", owner);
		val category = gen.category(cafe, null);
		val post = gen.post(category, owner);
		
		gen.comment(post, owner);
		assertEquals(1, load().getComments().size());
		
		gen.comment(post, owner);
		assertEquals(2, load().getComments().size());
	}
}
