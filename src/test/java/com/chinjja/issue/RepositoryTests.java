package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.chinjja.issue.data.CafeMemberRepository;
import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.LikeCountRepository;
import com.chinjja.issue.data.LikableRepository;
import com.chinjja.issue.data.PostRepository;
import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Category.Type;
import com.chinjja.issue.form.CategoryForm;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.PostData;
import com.chinjja.issue.domain.User;

import lombok.val;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryTests {
	@Autowired TestEntityManager em;
	@Autowired CafeRepository cafeRepo;
	@Autowired CafeMemberRepository cafeMemberRepo;
	@Autowired CommentRepository commentRepo;
	@Autowired LikeCountRepository likeCountRepo;
	@Autowired LikableRepository likableRepo;
	@Autowired PostRepository postRepo;
	@Autowired UserRepository userRepo;
	
	@Test
	public void findByIdCafeInAndApprovedIsFalse() {
		val user1 = userRepo.save(User.builder()
				.username("user1")
				.password("1234")
				.build());
		
		val cafe1 = Cafe.builder()
				.name("user1")
				.owner(user1)
				.needApproval(true)
				;
		val cafe11 = cafeRepo.save(cafe1.id("cafe11").build());
		val cafe12 = cafeRepo.save(cafe1.id("cafe12").build());
		val cafe13 = cafeRepo.save(cafe1.id("cafe13").build());
		
		val user2 = userRepo.save(User.builder()
				.username("user2")
				.password("1234")
				.build());
		val cafe2 = Cafe.builder()
				.name("user2")
				.owner(user2)
				.needApproval(true)
				;
		val cafe21 = cafeRepo.save(cafe2.id("cafe21").build());

		val user3 = userRepo.save(User.builder()
				.username("user3")
				.password("1234")
				.build());
		
		cafeMemberRepo.save(CafeMember.builder()
				.id(new CafeMember.Id(cafe11, user2))
				.greeting("hi1")
				.build());
		cafeMemberRepo.save(CafeMember.builder()
				.id(new CafeMember.Id(cafe12, user2))
				.greeting("hi1")
				.build());
		cafeMemberRepo.save(CafeMember.builder()
				.id(new CafeMember.Id(cafe13, user2))
				.greeting("hi1")
				.build());
		cafeMemberRepo.save(CafeMember.builder()
				.id(new CafeMember.Id(cafe21, user1))
				.greeting("hi1")
				.build());
		cafeMemberRepo.save(CafeMember.builder()
				.id(new CafeMember.Id(cafe11, user3))
				.greeting("hi1")
				.build());
		em.flush();
		em.clear();
		
		val user = userRepo.findByUsername("user1");
		val cafes = user.getCafes();
		assertEquals(3, cafes.size());
		
		int count1 = 0;
		int count2 = 0;
		for(val cafe : cafes) {
			count1 += cafeMemberRepo.findByIdCafeAndApproved(cafe, false).size();
			for(val member : cafe.getMembers()) {
				if(!member.isApproved()) {
					count2++;
				}
			}
		}
		assertEquals(4, count1);
		assertEquals(4, count2);
	}
}
