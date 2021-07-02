package com.chinjja.issue.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Likable;
import com.chinjja.issue.domain.LikeUser;
import com.chinjja.issue.domain.User;

public interface LikeUserRepository extends CrudRepository<LikeUser, LikeUser.Id> {
	List<User> findByIdLikable(Likable likable);
	int countByIdLikable(Likable likable);
}
