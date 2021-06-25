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
import com.chinjja.issue.domain.CafeMemberId;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Category.Type;
import com.chinjja.issue.domain.CategoryData;
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
		val user1 = userRepo.save(new User("user1"));
		val cafe11 = cafeRepo.save(new Cafe("cafe11", "cafe1", user1, true));
		val cafe12 = cafeRepo.save(new Cafe("cafe12", "cafe2", user1, true));
		val cafe13 = cafeRepo.save(new Cafe("cafe13", "cafe3", user1, true));
		
		val user2 = userRepo.save(new User("user2"));
		val cafe21 = cafeRepo.save(new Cafe("cafe21", "cafe21", user2, true));

		val user3 = userRepo.save(new User("user3"));
		
		cafeMemberRepo.save(new CafeMember(new CafeMemberId(cafe11, user2), "hi1"));
		cafeMemberRepo.save(new CafeMember(new CafeMemberId(cafe12, user2), "hi2"));
		cafeMemberRepo.save(new CafeMember(new CafeMemberId(cafe13, user2), "hi3"));
		cafeMemberRepo.save(new CafeMember(new CafeMemberId(cafe21, user1), "hi4"));
		cafeMemberRepo.save(new CafeMember(new CafeMemberId(cafe11, user3), "hi4"));
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
