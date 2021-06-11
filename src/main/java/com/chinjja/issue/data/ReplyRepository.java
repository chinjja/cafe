package com.chinjja.issue.data;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Issue;
import com.chinjja.issue.domain.Reply;
import com.chinjja.issue.domain.User;

public interface ReplyRepository extends CrudRepository<Reply, Long> {
	Iterable<Reply> findAllByIssue(Issue issue);
	Iterable<Reply> findAllByUser(User user);
}
