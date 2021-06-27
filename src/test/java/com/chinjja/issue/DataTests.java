package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Category.Type;
import com.chinjja.issue.form.CategoryForm;
import com.chinjja.issue.form.CommentForm;
import com.chinjja.issue.form.PostForm;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.domain.UserRole;

import lombok.val;
import lombok.var;

@DataJpaTest(properties = {
		"spring.jpa.properties.hibernate.show_sql=false",
		"spring.jpa.properties.hibernate.format_sql=true"
})
@ActiveProfiles("test")
class DataTests {
	@Autowired TestEntityManager em;
	
	@Test
	public void user() {
		val user1 = User.builder()
				.username("owner")
				.build();
		assertThrows(Exception.class, () -> em.persistAndFlush(user1));
		em.clear();
		
		var user2 = User.builder()
				.username("owner")
				.password("1234")
				.build();
		user2 = em.persist(user2);
		val cpy = user2.toBuilder().build();
		assertEquals(user2, cpy);
		assertFalse(user2.isAdmin());
		
		em.persist(UserRole.create(user2, "ROLE_ADMIN"));
		em.flush();
		em.clear();
		user2 = em.find(User.class, user2.getId());
		assertTrue(user2.isAdmin());
		
		val loadUser = em.find(User.class, user2.getId());
		assertEquals(user2, loadUser);
	}
	
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
