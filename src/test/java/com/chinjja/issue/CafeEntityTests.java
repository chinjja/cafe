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
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class CafeEntityTests {
	@Autowired TestEntityManager em;
	TestEntityGenerator gen;

	Cafe cafe;
	User owner;
	User user1;
	User user2;
	
	@BeforeEach
	void prepare() {
		gen = new TestEntityGenerator(em);
		owner = gen.user("owner");
		cafe = gen.cafe("cafe", owner);
		user1 = gen.user("user1");
		user2 = gen.user("user2");
		em.flush();
	}
	
	@Test
	void shouldPassEqualizationTest() {
		val load = load_cafe();
		assertTrue(cafe != load);
		assertEquals(cafe, load);
	}
	
	Cafe load_cafe() {
		em.flush();
		em.clear();
		return em.find(Cafe.class, cafe.getId());
	}
	
	@Test
	void shouldHaveNoMember() {
		val load = load_cafe();
		assertEquals(0, load.getMemberCount());
		assertEquals(0, load.getMembers().size());
	}
	
	@Test
	void shouldHaveNoMemberIfInsertedMemberIsNotApproved() {
		val member = gen.member(cafe, user1);
		member.setApproved(false);
		
		val load = load_cafe();
		assertEquals(0, load.getMemberCount());
		assertEquals(0, load.getMembers().size());
	}
	
	@Test
	void shouldHaveOneMemberIfInsertedMemberIsApproved() {
		val member = gen.member(cafe, user1);
		member.setApproved(true);
		
		val load = load_cafe();
		assertEquals(1, load.getMemberCount());
		assertEquals(1, load.getMembers().size());
		assertEquals(member, load.getMembers().get(0));
	}
	
	@Test
	void shouldHaveNoRootCategory() {
		assertEquals(0, cafe.getRootCategories().size());
	}
	
	@Test
	void shouldHaveRootCategoryAfterInsertCategory() {
		val category = gen.category(cafe, null);
		
		val load = load_cafe();
		assertEquals(1, load.getRootCategories().size());
		assertEquals(category, load.getRootCategories().get(0));
		
	}
}
