package com.chinjja.issue.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Blog;
import com.chinjja.issue.domain.Category;

public interface BlogRepository extends PagingAndSortingRepository<Blog, Long> {
	Page<Blog> findAllByCategory(Category category, Pageable pageable);
	Page<Blog> findAllByCategoryIn(Iterable<Category> categories, Pageable pageable);
}
