package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class PostEntityTests {
	@Autowired TestEntityManager em;
	TestEntityGenerator gen;

	User owner;
	User user1;
	User user2;
	Cafe cafe;
	Category category;
	Post post;
	
	@BeforeEach
	void prepare() {
		gen = new TestEntityGenerator(em);
		owner = gen.user("owner");
		cafe = gen.cafe("cafe", owner);
		user1 = gen.user("user1");
		user2 = gen.user("user2");
		category = gen.category(cafe, null);
		post = gen.post(category, user1);
		em.flush();
	}
	
	@Test
	void shouldPassEqualizationTest() {
		val load = load_post();
		assertTrue(post != load);
		assertEquals(post, load);
	}
	
	Post load_post() {
		em.flush();
		em.clear();
		return em.find(Post.class, post.getId());
	}
	
	@Test
	void shouldHaveNoComment() {
		val load = load_post();
		assertEquals(0, load.getCommentCount());
		assertEquals(0, load.getComments().size());
	}
	
	@Test
	void shouldHaveCommentAfterInserting() {
		val comment = gen.comment(post, user1);
		
		val load = load_post();
		assertEquals(1, load.getCommentCount());
		assertEquals(1, load.getComments().size());
		assertEquals(comment, load.getComments().get(0));
	}
	
	@Test
	void shouldHaveNoLikeUser() {
		val load = load_post();
		assertEquals(0, load.getLikeCount());
		assertEquals(0, load.getLikes().size());
	}
	
	@Test
	void shouldHaveLikeAfterInserting() {
		val like = gen.like(post, user1);
		
		val load = load_post();
		assertEquals(1, load.getLikeCount());
		assertEquals(1, load.getLikes().size());
		assertEquals(like, load.getLikes().get(0));
		
		assertThrows(Exception.class, () -> {
			gen.like(post, user1);
		});
	}
}
