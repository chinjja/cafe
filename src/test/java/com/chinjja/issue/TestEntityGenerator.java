package com.chinjja.issue;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Category.Type;
import com.chinjja.issue.domain.User;

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
		cafe.setName("cafe");
		cafe.setDescription("cafe");
		cafe.setOwner(owner);
		cafe.setWelcome("cafe");
		return em.persist(cafe);
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
		category.setName("category");
		category.setType(Type.DIRECTORY);
		return em.persist(category);
	}
}
