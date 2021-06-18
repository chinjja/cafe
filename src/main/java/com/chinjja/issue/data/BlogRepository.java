package com.chinjja.issue.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Blog;

public interface BlogRepository extends PagingAndSortingRepository<Blog, Long> {

}
