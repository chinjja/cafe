package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class CategoryEntityTests {
	@Autowired TestEntityManager em;
	TestEntityGenerator gen;

	User owner;
	User user1;
	User user2;
	Cafe cafe;
	Category parent;
	
	@BeforeEach
	void prepare() {
		gen = new TestEntityGenerator(em);
		owner = gen.user("owner");
		cafe = gen.cafe("cafe", owner);
		user1 = gen.user("user1");
		user2 = gen.user("user2");
		parent = gen.category(cafe, null);
		em.flush();
	}
	
	@Test
	void shouldPassEqualizationTest() {
		val load = load_category();
		assertTrue(parent != load);
		assertEquals(parent, load);
	}
	
	Category load_category() {
		em.flush();
		em.clear();
		return em.find(Category.class, parent.getId());
	}
	
	@Test
	void shouldHaveNoChildCategory() {
		val load = load_category();
		assertEquals(0, load.getCategoryCount());
		assertEquals(0, load.getCategories().size());
	}
	
	@Test
	void shouldHaveZeroWhenInsertingOtherRootCategory() {
		gen.category(cafe, null);
		
		val load = load_category();
		assertEquals(0, load.getCategoryCount());
		assertEquals(0, load.getCategories().size());
	}
	
	@Test
	void shouldHaveOneWhenInsertingChildCategory() {
		val child = gen.category(cafe, parent);
		
		val load = load_category();
		assertEquals(1, load.getCategoryCount());
		assertEquals(1, load.getCategories().size());
		assertEquals(child, load.getCategories().get(0));
	}
	
	@Test
	void shouldHaveNoPost() {
		val load = load_category();
		assertEquals(0, load.getPostCount());
		assertEquals(0, load.getPosts().size());
	}
	
	@Test
	void shouldHavePostWhenInsertingPost() {
		val post = gen.post(parent, user1);
		
		val load = load_category();
		assertEquals(1, load.getPostCount());
		assertEquals(1, load.getPosts().size());
		assertEquals(post, load.getPosts().get(0));
	}
}
