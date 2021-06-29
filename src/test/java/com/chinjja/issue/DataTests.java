package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Category.Type;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
class DataTests {
	@Autowired TestEntityManager em;
	
	@Test
	public void category() {
		val owner = em.persist(User.builder()
				.username("owner")
				.password("1234")
				.build());
		val cafe = em.persist(Cafe.builder()
				.id("cafe")
				.name("The cafe")
				.owner(owner)
				.build());
		val category = em.persist(Category.builder()
				.cafe(cafe)
				.type(Type.DIRECTORY)
				.name("dir1")
				.build());
		Post post = em.persist(Post.builder()
				.user(owner)
				.category(category)
				.title("post1")
				.contents("post1's content")
				.build());
		val comment = em.persist(Comment.builder()
				.user(owner)
				.likable(post)
				.comment("comment1")
				.build());
		em.flush();
		
		assertEquals(owner, cafe.getOwner());
	}
	
	@Test
	public void shouldFailWithNullId() {
		val owner = em.persist(User.builder()
				.username("owner")
				.password("1234")
				.build());
		
		assertThrows(Exception.class, () -> {
			em.persist(Cafe.builder()
					.name("The cafe")
					.owner(owner)
					.build());
		});
	}
}
