package com.chinjja.issue;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Category.Type;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Likable;
import com.chinjja.issue.domain.LikeUser;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.domain.UserRole;

import lombok.val;

public class TestEntityGenerator {
	TestEntityManager em;
	
	TestEntityGenerator(TestEntityManager em) {
		this.em = em;
	}
	
	User user(String username) {
		val u = new User();
		u.setUsername(username);
		u.setPassword("1234");
		return em.persist(u);
	}
	
	Cafe cafe(String id, User owner) {
		val cafe = new Cafe();
		cafe.setId(id);
		cafe.setTitle("cafe");
		cafe.setDescription("cafe");
		cafe.setOwner(owner);
		cafe.setWelcome("cafe");
		return em.persist(cafe);
	}
	
	UserRole role(User user, String role) {
		val o = new UserRole();
		o.setId(new UserRole.Id(user, role));
		return em.persist(o);
	}
	
	CafeMember member(Cafe cafe, User user) {
		val member = new CafeMember();
		member.setId(new CafeMember.Id(cafe, user));
		member.setGreeting("hi");
		return em.persist(member);
	}
	
	Category category(Cafe cafe, Category parent) {
		val category = new Category();
		category.setCafe(cafe);
		category.setParent(parent);
		category.setTitle("category");
		category.setType(Type.DIRECTORY);
		return em.persist(category);
	}
	
	Post post(Category category, User author) {
		val post = new Post();
		post.setCategory(category);
		post.setUser(author);
		post.setTitle("title");
		post.setText("contents");
		return em.persist(post);
	}
	
	Comment comment(Likable likable, User author) {
		val comment = new Comment();
		comment.setLikable(likable);
		comment.setUser(author);
		comment.setText("comment");
		return em.persist(comment);
	}
	
	LikeUser like(Likable likable, User user) {
		val like = new LikeUser();
		like.setId(new LikeUser.Id(likable, user));
		return em.persist(like);
	}
}
