package com.chinjja.issue.data;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.chinjja.issue.domain.Likable;

public interface LikableRepository extends Repository<Likable, Long> {
	Optional<Likable> findById(Long id);
	boolean existsById(Long id);
	Iterable<Likable> findAll();
	long count();
}
