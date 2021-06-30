package com.chinjja.issue.data;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
	Iterable<Category> findAllByCafeAndParentIsNull(Cafe cafe);
	Iterable<Category> findAllByCafe(Cafe cafe);
}
