package com.chinjja.issue.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Element;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
	Iterable<Comment> findAllByTarget(Element target, Sort sort);
}
