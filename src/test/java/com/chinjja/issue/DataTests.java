package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.chinjja.issue.data.IssueRepository;
import com.chinjja.issue.data.ReplyRepository;
import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.Issue;
import com.chinjja.issue.domain.IssueData;
import com.chinjja.issue.domain.Reply;
import com.chinjja.issue.domain.User;

import lombok.val;

@SpringBootTest
class DataTests {
	@Autowired IssueRepository issueRepo;
	@Autowired ReplyRepository replyRepo;
	@Autowired UserRepository userRepo;
	
	@BeforeEach
	void afterEach() {
		replyRepo.deleteAll();
		issueRepo.deleteAll();
		userRepo.deleteAll();
	}
	
	User new_user() {
		val user = new User();
		user.setUsername("chinjja");
		user.setPassword("1234");
		return userRepo.save(user);
	}
	
	@Test
	void user() {
		new_user();
	}
	
	@Test
	void user_equals() {
		val user1 = new_user();
		val user2 = userRepo.findByUsername("chinjja");
		assertEquals(user1, user2);
	}
	
	@Test
	void duplicate_user() {
		new_user();
		assertThrows(Exception.class, () -> {
			new_user();
		});
	}
	
	Issue new_issue() {
		val user = new_user();
		val issue = new Issue();
		issue.setData(new IssueData("issue summary", "issue contents"));
		issue.setUser(user);
		return issueRepo.save(issue);
	}
	@Test
	void issue() {
		new_issue();
	}
	
	@Test
	void issue_without_user() {
		val issue = new Issue();
		issue.setData(new IssueData("issue summary", "issue contents"));
		assertThrows(Exception.class, () -> {
			issueRepo.save(issue);
		});
	}
	
	Reply new_reply() {
		val issue = new_issue();
		val reply = new Reply();
		reply.setComment("reply");
		reply.setIssue(issue);
		reply.setUser(issue.getUser());
		return replyRepo.save(reply);
	}
	
	@Test
	void reply() {
		new_reply();
	}
}
