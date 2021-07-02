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
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class CommentEntityTests {
	@Autowired TestEntityManager em;
	TestEntityGenerator gen;

	User owner;
	User user1;
	User user2;
	Cafe cafe;
	Category category;
	Post post;
	Comment comment;
	
	@BeforeEach
	void prepare() {
		gen = new TestEntityGenerator(em);
		owner = gen.user("owner");
		cafe = gen.cafe("cafe", owner);
		user1 = gen.user("user1");
		user2 = gen.user("user2");
		category = gen.category(cafe, null);
		post = gen.post(category, user1);
		comment = gen.comment(post, user1);
		em.flush();
	}
	
	@Test
	void shouldPassEqualizationTest() {
		val load = load_comment();
		assertTrue(comment != load);
		assertEquals(comment, load);
	}
	
	Comment load_comment() {
		em.flush();
		em.clear();
		return em.find(Comment.class, comment.getId());
	}
	
	@Test
	void shouldHaveNoComment() {
		val load = load_comment();
		assertEquals(0, load.getCommentCount());
		assertEquals(0, load.getComments().size());
	}
	
	@Test
	void shouldHaveCommentWhenInserting() {
		val child = gen.comment(comment, user1);
		
		val load = load_comment();
		assertEquals(1, load.getCommentCount());
		assertEquals(1, load.getComments().size());
		assertEquals(child, load.getComments().get(0));
	}
	
	@Test
	void shouldHaveNoLikeUser() {
		val load = load_comment();
		assertEquals(0, load.getLikeCount());
		assertEquals(0, load.getLikes().size());
	}
	
	@Test
	void shouldHaveLikeWhenInserting() {
		val like = gen.like(comment, user1);
		
		val load = load_comment();
		assertEquals(1, load.getLikeCount());
		assertEquals(1, load.getLikes().size());
		assertEquals(like, load.getLikes().get(0));
		
		assertThrows(Exception.class, () -> {
			gen.like(comment, user1);
		});
	}
}
