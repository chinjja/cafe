package com.chinjja.issue.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Issue;
import com.chinjja.issue.domain.User;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {
	Iterable<Issue> findAllByUser(User user);
}
