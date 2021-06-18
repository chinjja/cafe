package com.chinjja.issue.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Element;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.User;

public interface LikeCountRepository extends CrudRepository<LikeCount, Long> {
	List<User> findByTarget(Element target);
	int countByTarget(Element target);
	LikeCount findByTargetAndUser(Element target, User user);
}
