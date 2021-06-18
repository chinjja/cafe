package com.chinjja.issue.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Blog;
import com.chinjja.issue.domain.Category;

public interface BlogRepository extends PagingAndSortingRepository<Blog, Long> {
	Iterable<Blog> findAllByCategory(Category  category, Sort sort);
}
