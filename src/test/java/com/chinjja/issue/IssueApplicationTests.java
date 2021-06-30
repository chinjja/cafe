package com.chinjja.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.data.UserRoleRepository;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.domain.UserRole;

import lombok.val;

@SpringBootTest
public class IssueApplicationTests {
	@Autowired EntityManager em;
	@Autowired UserRepository userRepo;
	@Autowired UserRoleRepository roleRepo;
	
	@Test
	@Transactional
	void shouldBeEnabledWithTransaction() {
		assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
	}
	
	@Test
	void shouldBeDisabledWithoudTransaction() {
		assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
	}
	
	@Test
	void shouldExistsAdminUser() {
		assertNotNull(userRepo.findByUsername("admin"));
	}
}
