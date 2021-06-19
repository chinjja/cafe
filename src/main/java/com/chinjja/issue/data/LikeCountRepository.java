package com.chinjja.issue.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Element;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.LikeCountId;
import com.chinjja.issue.domain.User;

public interface LikeCountRepository extends CrudRepository<LikeCount, LikeCountId> {
	List<User> findByIdTarget(Element target);
	int countByIdTarget(Element target);
}
