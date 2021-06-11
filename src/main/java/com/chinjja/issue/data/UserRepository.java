package com.chinjja.issue.data;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}
