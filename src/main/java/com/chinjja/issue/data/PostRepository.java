package com.chinjja.issue.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.Category;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
	Page<Post> findAllByCategory(Category category, Pageable pageable);
	Page<Post> findAllByCategoryIn(Iterable<Category> categories, Pageable pageable);
}
