package com.chinjja.issue.data;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
	Iterable<Category> findAllRootByParentIsNull();
}
