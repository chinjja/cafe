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
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.PostData;
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest(properties = {
		"spring.jpa.properties.hibernate.show_sql=false",
		"spring.jpa.properties.hibernate.format_sql=true"
})
@ActiveProfiles("test")
class DataTests {
	@Autowired TestEntityManager em;
	
	@Test
	public void user() {
		val user = new User();
		assertThrows(Exception.class, () -> em.persistAndFlush(user));
		
		user.setUsername("owner");
		user.setPassword("1234");
		em.persistAndFlush(user);
		
		val loadUser = em.find(User.class, user.getId());
		assertEquals(user, loadUser);
		
		val user2 = new User("owner");
		assertThrows(Exception.class, () -> em.persistAndFlush(user2));
	}
	
	@Test
	public void category() {
		val owner = em.persist(new User("owner"));
		val cafe = em.persist(new Cafe("cafe", "The cafe", owner, false));
		val category = em.persist(new Category(cafe, new CategoryData("dir1", Type.DIRECTORY)));
		val post = em.persist(new Post(owner, category, new PostData("post1", "post1's contents")));
		val comment = em.persist(new Comment(owner, post, new CommentData("comment1")));
		em.flush();
		
		assertEquals(owner, cafe.getOwner());
	}
	
	@Test
	void overlap_cafe() {
		val owner = em.persist(new User("owner"));
		em.persist(new Cafe("cafe", "The cafe", owner, false));
		assertThrows(Exception.class, () -> em.persistAndFlush(new Cafe("cafe", "The cafe", owner, false)));
	}
}
